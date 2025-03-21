/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.annotations.beanvalidation;

import java.math.BigDecimal;
import java.util.Map;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;

import org.hibernate.testing.DialectChecks;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;

import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @author Vladimir Klyushnikov
 * @author Hardy Ferentschik
 */
public class DDLWithoutCallbackTest extends BaseNonConfigCoreFunctionalTestCase {

	@Override
	protected void addSettings(Map<String,Object> settings) {
		settings.put( "jakarta.persistence.validation.mode", "ddl" );
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				Address.class,
				CupHolder.class,
				MinMax.class,
				RangeEntity.class
		};
	}

	@Override
	protected boolean isCleanupTestDataRequired() {
		return true;
	}

	@Test
	@RequiresDialectFeature(DialectChecks.SupportsColumnCheck.class)
	public void testListeners() {
		CupHolder ch = new CupHolder();
		ch.setRadius( new BigDecimal( "12" ) );
		assertDatabaseConstraintViolationThrown( ch );
	}

	@Test
	@RequiresDialectFeature(DialectChecks.SupportsColumnCheck.class)
	public void testMinAndMaxChecksGetApplied() {
		MinMax minMax = new MinMax( 1 );
		assertDatabaseConstraintViolationThrown( minMax );

		minMax = new MinMax( 11 );
		assertDatabaseConstraintViolationThrown( minMax );

		final MinMax validMinMax = new MinMax( 5 );

		doInHibernate( this::sessionFactory, session -> {
			session.persist( validMinMax );
		} );
	}

	@Test
	@RequiresDialectFeature(DialectChecks.SupportsColumnCheck.class)
	public void testRangeChecksGetApplied() {
		RangeEntity range = new RangeEntity( 1 );
		assertDatabaseConstraintViolationThrown( range );

		range = new RangeEntity( 11 );
		assertDatabaseConstraintViolationThrown( range );

		RangeEntity validRange = new RangeEntity( 5 );

		doInHibernate( this::sessionFactory, session -> {
			session.persist( validRange );
		} );
	}

	@Test
	public void testDDLEnabled() {
		PersistentClass classMapping = metadata().getEntityBinding( Address.class.getName() );
		Column countryColumn = (Column) classMapping.getProperty( "country" ).getSelectables().get( 0 );
		assertFalse( "DDL constraints are not applied", countryColumn.isNullable() );
	}

	private void assertDatabaseConstraintViolationThrown(Object o) {
		doInHibernate( this::sessionFactory, session -> {
			try {
				session.persist( o );
				session.flush();
				fail( "expecting SQL constraint violation" );
			}
			catch (PersistenceException pe) {
				final Throwable cause = pe.getCause();
				if ( cause instanceof ConstraintViolationException ) {
					fail( "invalid object should not be validated" );
				}
				else if ( cause instanceof org.hibernate.exception.ConstraintViolationException ) {
					if ( getDialect().supportsColumnCheck() ) {
						// expected
					}
					else {
						org.hibernate.exception.ConstraintViolationException cve = (org.hibernate.exception.ConstraintViolationException) cause;
						fail( "Unexpected SQL constraint violation [" + cve.getConstraintName() + "] : " + cve.getSQLException() );
					}
				}
			}
		} );
	}

	@Entity(name = "RangeEntity")
	public static class RangeEntity {

		@Id
		@GeneratedValue
		private Long id;

		@org.hibernate.validator.constraints.Range(min = 2, max = 10)
		private Integer rangeProperty;

		private RangeEntity() {
		}

		public RangeEntity(Integer value) {
			this.rangeProperty = value;
		}
	}
}
