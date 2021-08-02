/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.tool.schema.spi;


import org.hibernate.AssertionFailure;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Exportable;

/**
 * Defines a contract for exporting of database objects (tables, sequences, etc) for use in SQL {@code CREATE} and
 * {@code DROP} scripts.
 * <p/>
 * This is an ORM-centric contract
 *
 * @author Steve Ebersole
 */
public interface Exporter<T extends Exportable> {
	String[] NO_COMMANDS = new String[0];

	/**
	 * Get the commands needed for creation.
	 *
	 * @param exportable An object to export.
	 * @param metadata The relevant Hibernate ORM metadata.
	 * @return The commands needed for creation scripting.
	 */
	// Default implementation for backwards compatibility only.
	@SuppressWarnings("deprecation")
	default String[] getSqlCreateStrings(T exportable, SchemaActionMetadata metadata) {
		return getSqlCreateStrings( exportable, metadata.getFullMetadataOrFail() );
	}

	/**
	 * Get the commands needed for creation.
	 *
	 * @return The commands needed for creation scripting.
	 * @deprecated Implement {@link #getSqlCreateStrings(Exportable, SchemaActionMetadata)} instead.
	 */
	@Deprecated
	default String[] getSqlCreateStrings(T exportable, Metadata metadata) {
		throw new AssertionFailure( "getSqlCreateStrings(Object, SchemaActionMetadata) is not implemented as required" );
	}

	/**
	 * Get the commands needed for dropping.
	 *
	 * @param exportable An object to export.
	 * @param metadata The relevant Hibernate ORM metadata.
	 * @return The commands needed for drop scripting.
	 */
	// Default implementation for backwards compatibility only.
	@SuppressWarnings("deprecation")
	default String[] getSqlDropStrings(T exportable, SchemaActionMetadata metadata) {
		return getSqlDropStrings( exportable, metadata.getFullMetadataOrFail() );
	}

	/**
	 * Get the commands needed for dropping.
	 *
	 * @return The commands needed for drop scripting.
	 * @deprecated Implement {@link #getSqlDropStrings(Exportable, SchemaActionMetadata)} instead.
	 */
	@Deprecated
	default String[] getSqlDropStrings(T exportable, Metadata metadata) {
		throw new AssertionFailure( "getSqlDropStrings(Object, SchemaActionMetadata) is not implemented as required" );
	}
}
