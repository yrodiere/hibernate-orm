/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.annotations;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

public class SpecifiedResultTypeMismatchIT extends BaseCoreFunctionalTestCase {

	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				Company.class
		};
	}

	@Test
	public void reproducer() {
		Class<?> entityClass = Company.class;

		try ( StatelessSession ss = sessionFactory().unwrap( SessionFactory.class ).withStatelessOptions().openStatelessSession() ) {
			CriteriaBuilder builder = sessionFactory().getCriteriaBuilder();
			CriteriaQuery<?> criteria = builder.createQuery( entityClass );
			Root<?> root = criteria.from( entityClass );

			criteria.orderBy( builder.asc( root.get( "id" ) ) );

			EntityType<?> model = root.getModel();
			Class<?> javaType = model.getIdType().getJavaType();

			@SuppressWarnings("rawtypes")
			SingularAttribute singularAttribute = model.getId( javaType );
			criteria.select( root.get( singularAttribute ) );

			Query<?> query = ss.createQuery( criteria );
		}
	}

	@Entity
	public static class Company {

		@Id
		@GeneratedValue
		private int id;

		public Company() {
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Company [id=" + id + "]";
		}
	}
}
