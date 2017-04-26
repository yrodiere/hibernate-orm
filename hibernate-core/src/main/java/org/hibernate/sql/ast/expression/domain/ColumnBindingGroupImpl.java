/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */

package org.hibernate.sql.ast.expression.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.sql.ast.from.ColumnReference;

/**
 * @author Steve Ebersole
 */
public class ColumnBindingGroupImpl implements ColumnBindingGroup {
	private List<ColumnReference> columnBindings;

	public ColumnBindingGroupImpl() {
	}

	public void addColumnBinding(ColumnReference columnBinding) {
		if ( columnBindings == null ) {
			columnBindings = new ArrayList<>();
		}
		columnBindings.add( columnBinding );
	}

	public List<ColumnReference> getColumnBindings() {
		return columnBindings == null ? Collections.emptyList() : Collections.unmodifiableList( columnBindings );
	}
}
