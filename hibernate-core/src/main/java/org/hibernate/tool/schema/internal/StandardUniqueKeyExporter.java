/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.tool.schema.internal;

import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.schema.spi.Exporter;
import org.hibernate.tool.schema.spi.SchemaActionMetadata;

/**
 * Unique constraint Exporter.  Note that it's parameterized for Constraint, rather than UniqueKey.  This is
 * to allow Dialects to decide whether or not to create unique constraints for unique indexes.
 * 
 * @author Brett Meyer
 */
public class StandardUniqueKeyExporter implements Exporter<Constraint> {
	private final Dialect dialect;

	public StandardUniqueKeyExporter(Dialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public String[] getSqlCreateStrings(Constraint constraint, SchemaActionMetadata metadata) {
		return new String[] {
				dialect.getUniqueDelegate().getAlterTableToAddUniqueKeyCommand(
						(UniqueKey) constraint,
						metadata
				)
		};
	}

	@Override
	public String[] getSqlDropStrings(Constraint constraint, SchemaActionMetadata metadata) {
		return new String[] {
				dialect.getUniqueDelegate().getAlterTableToDropUniqueKeyCommand(
						(UniqueKey) constraint,
						metadata
				)
		};
	}
}
