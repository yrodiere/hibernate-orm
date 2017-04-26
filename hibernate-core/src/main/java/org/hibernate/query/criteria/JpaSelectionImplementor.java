/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.criteria;

import org.hibernate.query.sqm.produce.spi.criteria.select.JpaSelection;

/**
 * Hibernate ORM specialization of the JPA {@link javax.persistence.criteria.Selection}
 * contract.
 *
 * @author Steve Ebersole
 */
public interface JpaSelectionImplementor<X> extends JpaTupleElementImplementor<X>, JpaSelection<X> {
}
