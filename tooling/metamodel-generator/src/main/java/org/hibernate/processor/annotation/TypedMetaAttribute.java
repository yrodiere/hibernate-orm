/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.annotation;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.processor.model.Metamodel;

import static org.hibernate.processor.util.StringUtil.nameToMethodName;

/**
 * Represents a named query or named entity graph.
 *
 * @author Gavin King
 */
class TypedMetaAttribute extends NameMetaAttribute {
	private final String prefix;
	private final String resultType;
	private final String referenceType;
	private final @Nullable String query;

	public TypedMetaAttribute(
			Metamodel annotationMetaEntity,
			String name,
			String prefix,
			String resultType,
			String referenceType,
			@Nullable String query) {
		super( annotationMetaEntity, name, prefix );
		this.prefix = prefix;
		this.resultType = resultType;
		this.referenceType = referenceType;
		this.query = query;
	}

	@Override
	public boolean hasTypedAttribute() {
		return true;
	}

	@Override
	public String getAttributeDeclarationString() {
		final boolean isQuery = "QUERY_".equals(prefix);  //UGLY!
		final Metamodel entity = getHostingEntity();
		final StringBuilder declaration = new StringBuilder();
		declaration
				.append("\n/**")
				.append("\n * The ")
				.append(isQuery ? "query" : "entity graph")
				.append(" named {@value ")
				.append(prefix)
				.append(fieldName())
				.append("}\n");
		if ( query != null ) {
			declaration.append(" * <pre>");
			query.lines()
					.forEach( line -> declaration.append("\n * ").append( line ) );
			declaration.append("\n * </pre>\n");
		}
		declaration
				.append(" *\n * @see ")
				.append(entity.getQualifiedName())
				.append("\n **/\n")
				.append("public static volatile ")
				.append(entity.importType(referenceType))
				.append('<')
				.append(entity.importType(resultType))
				.append('>')
				.append(' ')
				.append('_')
				.append(nameToMethodName(getPropertyName()));
		if ( isQuery ) {
			declaration.append('_');
		}
		declaration.append(';');
		return declaration.toString();
	}
}
