/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.tool.schema;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.tool.schema.spi.SchemaActionMetadata;

public class LegacySchemaActionMetadata implements SchemaActionMetadata {

	private final Metadata metadata;

	public LegacySchemaActionMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public Database getDatabase() {
		return metadata.getDatabase();
	}

	@Override
	public Metadata getFullMetadataOrFail() {
		return metadata;
	}
}
