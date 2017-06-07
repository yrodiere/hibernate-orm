/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.tool.schema.internal;

import org.hibernate.dialect.Dialect;
import org.hibernate.metamodel.model.creation.spi.RuntimeModelCreationContext;
import org.hibernate.metamodel.model.relational.spi.Namespace;
import org.hibernate.metamodel.model.relational.spi.Table;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaFilter;

/**
 * @author Andrea Boriero
 *
 * This implementation executes a single {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])} call
 * to retrieve all the database table in order to determine if all the {@link javax.persistence.Entity} have a mapped database tables.
 */
public class GroupedSchemaValidatorImpl extends AbstractSchemaValidator {

	public GroupedSchemaValidatorImpl(
			HibernateSchemaManagementTool tool,
			RuntimeModelCreationContext modelCreationContext,
			SchemaFilter validateFilter) {
		super( tool, validateFilter );
	}

	@Override
	protected void validateTables(
			DatabaseInformation databaseInformation,
			ExecutionOptions options,
			Dialect dialect,
			Namespace namespace) {

		final NameSpaceTablesInformation tables = databaseInformation.getTablesInformation( namespace );
		for ( Table table : namespace.getTables() ) {
			if ( schemaFilter.includeTable( table ) && table.isExportable() ) {
				validateTable(
						table,
						tables.getTableInformation( table ),
						options,
						dialect
				);
			}
		}
	}
}
