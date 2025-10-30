/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.annotation;

import org.hibernate.processor.model.MetaAttribute;
import org.hibernate.processor.model.Metamodel;
import org.hibernate.processor.spi.ProcessorCustomizer;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;

import static org.hibernate.metamodel.mapping.EntityIdentifierMapping.ID_ROLE_NAME;
import static org.hibernate.processor.util.Constants.ENTITY_MANAGER;
import static org.hibernate.processor.util.Constants.OBJECTS;
import static org.hibernate.processor.util.TypeUtils.hasAnnotation;

/**
 * @author Gavin King
 */
public abstract class AbstractAnnotatedMethod implements MetaAttribute {

	final AnnotationMetaEntity annotationMetaEntity;
	final ExecutableElement method;
	final String sessionType;
	final String sessionName;

	public AbstractAnnotatedMethod(
			AnnotationMetaEntity annotationMetaEntity,
			ExecutableElement method,
			String sessionName, String sessionType) {
		this.annotationMetaEntity = annotationMetaEntity;
		this.method = method;
		this.sessionName = sessionName;
		this.sessionType = sessionType;
	}

	@Override
	public Metamodel getHostingEntity() {
		return annotationMetaEntity;
	}

	boolean isUsingEntityManager() {
		return ENTITY_MANAGER.equals(sessionType);
	}

	boolean isUsingStatelessSession() {
		return annotationMetaEntity.isStatelessSession();
	}

	boolean isReactive() {
		return annotationMetaEntity.isReactive();
	}

	boolean isReactiveSessionAccess() {
		return annotationMetaEntity.isReactiveSessionAccess();
	}

	String localSessionName() {
		return isReactiveSessionAccess() ? "_session" : sessionName;
	}

	String getObjectCall() {
		return annotationMetaEntity.isProvidedSessionAccess() ? ".getObject()" : "";
	}

	@Override
	public List<AnnotationMirror> inheritedAnnotations() {
		List<AnnotationMirror> result = new ArrayList<>();

		// TODO cache this, it's not reasonable to recompute for every method
		var inheritedAnnotations = new HashSet<>();
		var context = new ProcessorCustomizer.Context() {
			@Override
			public void addInheritedAnnotations(String... annotations) {
				Collections.addAll( inheritedAnnotations, annotations );
			}
		};

		// TODO remove
		annotationMetaEntity.getContext().logMessage( Diagnostic.Kind.WARNING, "Processing with customizers");
		for ( ProcessorCustomizer processorCustomizer : ServiceLoader.load( ProcessorCustomizer.class ) ) {
			// TODO remove
			annotationMetaEntity.getContext().logMessage( Diagnostic.Kind.WARNING, "Processing with customizer " + processorCustomizer.getClass().getSimpleName());
			processorCustomizer.customize( context );
		}
		method.getAnnotationMirrors().stream()
				.filter( annotationMirror -> {
					return inheritedAnnotations.contains( annotationMirror.getAnnotationType().asElement().toString() );
				} )
				.forEach( result::add );

		if ( annotationMetaEntity.isJakartaDataRepository() ) {
			method.getAnnotationMirrors().stream()
					.filter( annotationMirror -> hasAnnotation( annotationMirror.getAnnotationType().asElement(),
							"jakarta.interceptor.InterceptorBinding" ) )
					.forEach( result::add );
		}

		return result;
	}

	void nullCheck(StringBuilder declaration, String paramName) {
		declaration
				.append('\t')
				.append(annotationMetaEntity.staticImport(OBJECTS, "requireNonNull"))
				.append('(')
				.append(parameterName(paramName))
				.append(", \"Null ")
				.append(ID_ROLE_NAME.equals(paramName) ? "id" : paramName)
				.append("\");\n");
	}

	static String parameterName(String name) {
		return name.equals(ID_ROLE_NAME)
				? "id"
				: name.replace('.', '$');
	}

	protected void handle(StringBuilder declaration, String handled, String rethrown) {
		if ( isReactive() ) {
			declaration.append( "\n\t\t\t.onFailure(" )
					.append( annotationMetaEntity.importType( handled ) )
					.append( ".class)\n" )
					.append( "\t\t\t\t\t.transform(_ex -> new " )
					.append( annotationMetaEntity.importType( rethrown ) )
					.append( "(_ex.getMessage(), _ex))" );

		}
		else {
			declaration
					.append( "\tcatch (" )
					.append( annotationMetaEntity.importType( handled ) )
					.append( " _ex) {\n" )
					.append( "\t\tthrow new " )
					.append( annotationMetaEntity.importType( rethrown ) )
					.append( "(_ex.getMessage(), _ex);\n" )
					.append( "\t}\n" );
		}
	}
}
