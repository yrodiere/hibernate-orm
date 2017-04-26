/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.persister.common;

import org.hibernate.internal.util.StringHelper;
import org.hibernate.query.spi.NavigablePath;

/**
 * A representation of the static "Navigable" path relative to some "root entity".
 *
 * @see NavigablePath
 *
 * @author Steve Ebersole
 */
public class NavigableRole {
	public static final String IDENTIFIER_MAPPER_PROPERTY = "_identifierMapper";

	private final NavigableRole parent;
	private final String navigableName;
	private final String fullPath;

	public NavigableRole(NavigableRole parent, String navigableName) {
		this.parent = parent;
		this.navigableName = navigableName;

		// the _identifierMapper is a "hidden" property on entities with composite keys.
		// concatenating it will prevent the path from correctly being used to look up
		// various things such as criteria paths and fetch profile association paths
		if ( IDENTIFIER_MAPPER_PROPERTY.equals( navigableName ) ) {
			this.fullPath = parent != null ? parent.getFullPath() : "";
		}
		else {
			final String prefix;
			if ( parent != null ) {
				final String resolvedParent = parent.getFullPath();
				if ( StringHelper.isEmpty( resolvedParent ) ) {
					prefix = "";
				}
				else {
					prefix = resolvedParent + '.';
				}
			}
			else {
				prefix = "";
			}

			this.fullPath = prefix + navigableName;
		}
	}

	public NavigableRole(String navigableName) {
		this( null, navigableName );
	}

	public NavigableRole() {
		this( "" );
	}

	public NavigableRole append(String property) {
		return new NavigableRole( this, property );
	}

	public NavigableRole getParent() {
		return parent;
	}

	public String getNavigableName() {
		return navigableName;
	}

	public String getFullPath() {
		return fullPath;
	}

	public boolean isRoot() {
		return parent == null && StringHelper.isEmpty( navigableName );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + fullPath + ']';
	}
}
