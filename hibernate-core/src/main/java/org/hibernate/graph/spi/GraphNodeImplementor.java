/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.graph.spi;

import java.util.List;
import javax.persistence.AttributeNode;

import org.hibernate.graph.GraphNode;

/**
 * Integration version of the GraphNode contract
 *
 * @author Steve Ebersole
 * @author Strong Liu <stliu@hibernate.org>
 */
public interface GraphNodeImplementor<J> extends GraphNode<J> {

	@Override
	GraphNodeImplementor<J> makeCopy(boolean mutable);

	/**
	 * @deprecated This only makes sense on graphs or subgraphs; other nodes (attribute nodes) will return an empty list.
	 * Use org.hibernate.graph.spi.{@link GraphImplementor#getAttributeNodeImplementors()} instead.
	 */
	@Deprecated
	List<AttributeNodeImplementor<?>> attributeImplementorNodes();

	/**
	 * @deprecated This only makes sense on graphs or subgraphs; other nodes (attribute nodes) will return an empty list.
	 * Use {@link GraphImplementor#getGraphAttributeNodes()} instead.
	 */
	@Deprecated
	List<AttributeNode<?>> attributeNodes();

	/**
	 * @deprecated This only makes sense on graphs or subgraphs; other nodes (attribute nodes) will return false.
	 * Use {@link GraphImplementor#findAttributeNode(String)} instead.
	 */
	@Deprecated
	boolean containsAttribute(String name);
}
