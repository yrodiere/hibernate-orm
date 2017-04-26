/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate;

import java.util.List;
import javax.persistence.EntityGraph;
import javax.persistence.metamodel.EntityType;

import org.hibernate.persister.entity.spi.EntityPersister;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Access to Hibernate's runtime metamodel (domain model mapping) information,
 * as an extension to the JPA {@link javax.persistence.metamodel.Metamodel}
 * contract.
 *
 * @author Steve Ebersole
 */
public interface Metamodel extends javax.persistence.metamodel.Metamodel {
	/**
	 * Access to the TypeConfiguration in effect for this SessionFactory/Metamodel
	 *
	 * @return Access to the TypeConfiguration
	 */
	TypeConfiguration getTypeConfiguration();

	@Override
	@SuppressWarnings("unchecked")
	default <X> EntityType<X> entity(Class<X> cls) {
		final EntityPersister entityPersister = getTypeConfiguration().findEntityPersister( cls );
		if ( entityPersister == null ) {
			// per JPA, this condition needs to be an (illegal argument) exception
			throw new IllegalArgumentException( "Not an entity: " + cls );
		}
		return entityPersister;
	}

	/**
	 * Access to an entity supporting Hibernate's entity-name feature
	 *
	 * @param entityName The entity-name
	 *
	 * @return The entity descriptor
	 *
	 * @deprecated Use {@link TypeConfiguration#findEntityPersister(java.lang.String)} instead
	 */
	@Deprecated
	default <X> EntityType<X> entity(String entityName) {
		return getTypeConfiguration().findEntityPersister( entityName );
	}

	/**
	 * @deprecated Use {@link #getTypeConfiguration} -> {@link TypeConfiguration#addNamedEntityGraph} instead
	 */
	@Deprecated
	default <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
		getTypeConfiguration().addNamedEntityGraph( graphName, entityGraph );
	}

	/**
	 * @deprecated Use {@link #getTypeConfiguration} -> {@link TypeConfiguration#findEntityGraphByName} instead
	 */
	@Deprecated
	default <T> EntityGraph<T> findEntityGraphByName(String name) {
		return getTypeConfiguration().findEntityGraphByName( name );
	}

	/**
	 * @deprecated Use {@link #getTypeConfiguration} -> {@link TypeConfiguration#findEntityGraphsByType} instead
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	default <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
		return getTypeConfiguration().findEntityGraphsByType( entityClass );
	}
}
