/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.graph.spi;

import javax.persistence.metamodel.EntityType;

import org.hibernate.graph.RootGraph;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;

/**
 * Integration version of the RootGraph contract
 *
 * @author Steve Ebersole
 */
public interface RootGraphImplementor<J> extends RootGraph<J>, GraphImplementor<J>, EntityGraphImplementor<J> {
	boolean appliesTo(EntityTypeDescriptor<? super J> entityType);

	/**
	 * @deprecated Use {@link #appliesTo(EntityTypeDescriptor)} instead.
	 */
	@Deprecated
	@Override
	boolean appliesTo(EntityType<? super J> entityType);

	@Override
	@SuppressWarnings("unchecked")
	default boolean appliesTo(ManagedTypeDescriptor<? super J> managedType) {
		assert managedType instanceof EntityTypeDescriptor;
		return appliesTo( (EntityTypeDescriptor) managedType );
	}

	@Override
	RootGraphImplementor<J> makeRootGraph(String name, boolean mutable);

	@Override
	SubGraphImplementor<J> makeSubGraph(boolean mutable);

	/**
	 * @deprecated Use {@link #makeCopy(boolean)} instead.
	 */
	@Override
	@Deprecated
	EntityGraphImplementor<J> makeMutableCopy();

	/**
	 * Make an immutable copy of this entity graph, using the given name.
	 *
	 * @param name The name to apply to the immutable copy
	 *
	 * @return The immutable copy
	 */
	@Override
	default RootGraphImplementor<J> makeImmutableCopy(String name) {
		return makeRootGraph( name, false );
	}
}
