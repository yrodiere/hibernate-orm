/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.graph.spi;

import java.util.List;
import javax.persistence.AttributeNode;

/**
 * Integration version of the GraphNode contract
 *
 * @author <a href="mailto:stliu@hibernate.org">Strong Liu</a>
 */
public interface GraphNodeImplementor {
	List<AttributeNodeImplementor<?>> attributeImplementorNodes();
	List<AttributeNode<?>> attributeNodes();
	boolean containsAttribute(String name);
}
