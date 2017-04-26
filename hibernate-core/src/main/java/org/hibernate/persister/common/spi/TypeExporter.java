/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.persister.common.spi;


import org.hibernate.type.spi.Type;

/**
 * todo (6.0) : get rid of
 *
 * @author Steve Ebersole
 */
public interface TypeExporter<T> {
	Type<T> getOrmType();
}
