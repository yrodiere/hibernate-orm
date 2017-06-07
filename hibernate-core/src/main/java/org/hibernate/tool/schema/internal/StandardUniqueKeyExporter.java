/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.tool.schema.internal;

import org.hibernate.dialect.Dialect;
import org.hibernate.metamodel.model.relational.spi.DatabaseModel;
import org.hibernate.metamodel.model.relational.spi.UniqueKey;
import org.hibernate.tool.schema.spi.Exporter;

/**
 * Unique constraint Exporter.  Note that it's parameterized for Constraint, rather than UniqueKey.  This is
 * to allow Dialects to decide whether or not to create unique constraints for unique indexes.
 * 
 * @author Brett Meyer
 */
public class StandardUniqueKeyExporter implements Exporter<UniqueKey> {
	private final Dialect dialect;

	public StandardUniqueKeyExporter(Dialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public String[] getSqlCreateStrings(UniqueKey uniqueKey, DatabaseModel databaseModel) {
		return new String[] {
				dialect.getUniqueDelegate().getAlterTableToAddUniqueKeyCommand(
						uniqueKey,
						databaseModel
				)
		};
	}

	@Override
	public String[] getSqlDropStrings(UniqueKey uniqueKey, DatabaseModel databaseModel) {
		return new String[] {
				dialect.getUniqueDelegate().getAlterTableToDropUniqueKeyCommand(
						uniqueKey,
						databaseModel
				)
		};
	}
}
