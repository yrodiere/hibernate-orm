/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.tool.schema;

import org.hibernate.AssertionFailure;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.tool.schema.spi.SchemaActionMetadata;

/**
 * A "light" implementation of {@link SchemaActionMetadata},
 * which only references part of the original {@link Metadata}
 * in an effort to reduce its memory footprint,
 * so that it can be stored in the {@link org.hibernate.SessionFactory}.
 */
public class LightSchemaActionMetadata implements SchemaActionMetadata {
	private final Database database;

	public LightSchemaActionMetadata(Metadata metadata) {
		this.database = metadata.getDatabase();
	}

	@Override
	public Database getDatabase() {
		return database;
	}

	@Override
	public Metadata getFullMetadataOrFail() {
		throw new AssertionFailure( "The schema tool tried to access the full metadata, but it's not available at this point."
				+ " There is a bug in the schema tool, which should not try to access the full metadata" );
	}
}
