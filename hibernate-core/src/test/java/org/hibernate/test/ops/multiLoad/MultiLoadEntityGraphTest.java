/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops.multiLoad;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;

import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Before;
import org.junit.Test;

import static javax.persistence.GenerationType.AUTO;
import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiLoadEntityGraphTest extends BaseNonConfigCoreFunctionalTestCase {
	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { Parent.class, Child.class, Pet.class };
	}

	@Before
	public void before() {
		Session session = sessionFactory().openSession();
		session.getTransaction().begin();
		for ( int i = 0; i < 5; i++ ) {
			Parent p = new Parent( i, "Entity #" + i );
			for ( int j = 0; j < 5; j++ ) {
				Child child = new Child();
				child.setParent( p );
				p.getChildren().add( child );
			}
			for ( int j = 0; j < 5; j++ ) {
				Pet pet = new Pet();
				pet.setMaster( p );
				p.getPets().add( pet );
			}
			session.persist( p );
		}
		session.getTransaction().commit();
		session.close();
	}

	@Test
	public void testFetchGraph() {
		doInHibernate( this::sessionFactory, session -> {
			List<Parent> list = session.byMultipleIds( Parent.class ).multiLoad( 1, 2, 3 );
			assertEquals( 3, list.size() );

			// Collections should be loaded according to their defaults
			for ( Parent p : list ) {
				assertFalse( Hibernate.isInitialized( p.children ) );
				assertTrue( Hibernate.isInitialized( p.pets ) );
			}
		} );

		doInHibernate( this::sessionFactory, session -> {
			List<Parent> list = session.byMultipleIds( Parent.class )
					.with( (RootGraph) session.getEntityGraph( "eager" ), GraphSemantic.FETCH )
					.multiLoad( 1, 2, 3 );
			assertEquals( 3, list.size() );

			// Collections should be loaded eagerly if mentioned in the graph, or lazily otherwise.
			// Since the graph contains all collections, all collections should be loaded eagerly.
			for ( Parent p : list ) {
				assertTrue( Hibernate.isInitialized( p.children ) );
				assertTrue( Hibernate.isInitialized( p.pets ) );
			}
		} );

		doInHibernate( this::sessionFactory, session -> {
			List<Parent> list = session.byMultipleIds( Parent.class )
					.with( (RootGraph) session.getEntityGraph( "lazy" ), GraphSemantic.FETCH )
					.multiLoad( 1, 2, 3 );
			assertEquals( 3, list.size() );

			// Collections should be loaded eagerly if mentioned in the graph, or lazily otherwise.
			// Since the graph is empty, all collections should be loaded lazily.
			for ( Parent p : list ) {
				assertFalse( Hibernate.isInitialized( p.children ) );
				assertFalse( Hibernate.isInitialized( p.pets ) );
			}
		} );
	}

	@Test
	public void testLoadGraph() {
		doInHibernate( this::sessionFactory, session -> {
			List<Parent> list = session.byMultipleIds( Parent.class ).multiLoad( 1, 2, 3 );
			assertEquals( 3, list.size() );

			// Collections should be loaded according to their defaults
			for ( Parent p : list ) {
				assertFalse( Hibernate.isInitialized( p.children ) );
				assertTrue( Hibernate.isInitialized( p.pets ) );
			}
		} );

		doInHibernate( this::sessionFactory, session -> {
			List<Parent> list = session.byMultipleIds( Parent.class )
					.with( (RootGraph) session.getEntityGraph( "eager" ), GraphSemantic.LOAD )
					.multiLoad( 1, 2, 3 );
			assertEquals( 3, list.size() );

			// Collections should be loaded eagerly if mentioned in the graph, or according to their default otherwise.
			// Since the graph contains all collections, all collections should be loaded eagerly.
			for ( Parent p : list ) {
				assertTrue( Hibernate.isInitialized( p.children ) );
				assertTrue( Hibernate.isInitialized( p.pets ) );
			}
		} );

		doInHibernate( this::sessionFactory, session -> {
			List<Parent> list = session.byMultipleIds( Parent.class )
					.with( (RootGraph) session.getEntityGraph( "lazy" ), GraphSemantic.LOAD )
					.multiLoad( 1, 2, 3 );
			assertEquals( 3, list.size() );

			// Collections should be loaded eagerly if mentioned in the graph, or according to their default otherwise.
			// Since the graph is empty, all collections should be loaded according to their default.
			for ( Parent p : list ) {
				assertFalse( Hibernate.isInitialized( p.children ) );
				assertTrue( Hibernate.isInitialized( p.pets ) );
			}
		} );
	}

	@Entity(name = "Parent")
	@NamedEntityGraph(name = "eager", includeAllAttributes = true)
	@NamedEntityGraph(name = "lazy")
	public static class Parent {
		@Id
		private Integer id;
		private String text;
		@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
		private List<Child> children = new ArrayList<>();
		@OneToMany(mappedBy = "master", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
		private List<Pet> pets = new ArrayList<>();

		public Parent() {
		}

		public Parent(Integer id, String text) {
			this.id = id;
			this.text = text;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public List<Child> getChildren() {
			return children;
		}

		public void setChildren(List<Child> children) {
			this.children = children;
		}

		public List<Pet> getPets() {
			return pets;
		}

		public void setPets(List<Pet> pets) {
			this.pets = pets;
		}
	}

	@Entity(name = "Child")
	public static class Child {

		@Id
		@GeneratedValue(strategy = AUTO)
		private int id;

		@ManyToOne
		private Parent parent;

		public Child() {
		}

		public Parent getParent() {
			return parent;
		}

		public void setParent(Parent parent) {
			this.parent = parent;
		}

		public int getId() {
			return id;
		}
	}

	@Entity(name = "Pet")
	public static class Pet {

		@Id
		@GeneratedValue(strategy = AUTO)
		private int id;

		@ManyToOne
		private Parent master;

		public Pet() {
		}

		public Parent getMaster() {
			return master;
		}

		public void setMaster(Parent master) {
			this.master = master;
		}

		public int getId() {
			return id;
		}
	}
}
