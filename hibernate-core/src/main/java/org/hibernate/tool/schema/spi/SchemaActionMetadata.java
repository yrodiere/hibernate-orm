/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.tool.schema.LegacySchemaActionMetadata;
import org.hibernate.tool.schema.LightSchemaActionMetadata;

/**
 * Limited metadata passed to schema actions to describe the schema to be created/dropped/validated/etc.
 */
@Incubating
public interface SchemaActionMetadata {

	/**
	 * @return The database model.
	 */
	Database getDatabase();

	/**
	 * @return The full {@link Metadata} object, if available.
	 * @throws org.hibernate.AssertionFailure If the full metadata object is not available.
	 * @deprecated This should eventually be removed as we complete the switch from {@link Metadata}
	 * to {@link SchemaActionMetadata} in schema-related code.
	 */
	@Deprecated
	Metadata getFullMetadataOrFail();

}
