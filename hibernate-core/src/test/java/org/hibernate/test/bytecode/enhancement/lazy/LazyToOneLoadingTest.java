/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.proxy.HibernateProxy;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.MatcherAssert;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hibernate.Hibernate.isPropertyInitialized;
import static org.hibernate.testing.bytecode.enhancement.EnhancerTestUtils.checkDirtyTracking;
import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Luis Barreiro
 * @author Yoann Rodiere
 */
@RunWith(BytecodeEnhancerRunner.class)
@TestForIssue(jiraKey = "HHH-12642")
public class LazyToOneLoadingTest extends BaseCoreFunctionalTestCase {

	private static final int CHILDREN_SIZE = 10;
	private Long parentID;
	private Long lastChildID;

	@Override
	public Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { Parent.class, Child.class };
	}

	@Before
	public void prepare() {
		doInHibernate( this::sessionFactory, s -> {
			Parent parent = new Parent( "Parent" );
			for ( int i = 0; i < CHILDREN_SIZE; i++ ) {
				Child child = new Child( "Child #" + i );
				child.parent = parent;
				s.persist( child );
				lastChildID = child.id;
			}
			s.persist( parent );
			parentID = parent.id;
		} );
	}

	@Test
	public void test() {
		doInHibernate( this::sessionFactory, s -> {
			Child loadedChild = s.load( Child.class, lastChildID );
			MatcherAssert.assertThat( loadedChild, notNullValue() );
			MatcherAssert.assertThat( loadedChild, not( instanceOf( HibernateProxy.class ) ) );

			// The parent must initially be loaded as a proxy
			assertTrue( isPropertyInitialized( loadedChild, "parent" ) );
			Parent parent = loadedChild.parent;
			// Before we fixed HHH-12642, we used to get a fully loaded instance here
			assertFalse( isPropertyInitialized( parent, "name" ) );
			assertEquals( parentID, parent.id );

			// Accessing a field of the parent must initialize it
			String parentName = parent.name;
			assertTrue( isPropertyInitialized( parent, "name" ) );
			assertEquals( "Parent", parentName );
		} );
	}

	// --- //

	@Entity
	@Table(name = "PARENT")
	private static class Parent {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		Long id;

		String name;

		Parent() {
		}

		Parent(String name) {
			this.name = name;
		}
	}

	@Entity
	@Table(name = "CHILD")
	private static class Child {

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		Long id;

		@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
		Parent parent;

		String name;

		Child() {
		}

		Child(String name) {
			this.name = name;
		}
	}
}
