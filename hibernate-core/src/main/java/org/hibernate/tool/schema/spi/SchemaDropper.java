/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.Incubating;
import org.hibernate.boot.Metadata;

/**
 * Service delegate for handling schema dropping.
 *
 * @author Steve Ebersole
 */
@Incubating
public interface SchemaDropper {
	/**
	 * Perform a schema drop from the indicated source(s) to the indicated target(s).
	 *
	 * @param metadata Represents the schema to be dropped.
	 * @param options Options for executing the drop
	 * @param sourceDescriptor description of the source(s) of drop commands
	 * @param targetDescriptor description of the target(s) for the drop commands
	 */
	void doDrop(SchemaActionMetadata metadata, ExecutionOptions options, SourceDescriptor sourceDescriptor, TargetDescriptor targetDescriptor);

	/**
	 * Perform a schema drop from the indicated source(s) to the indicated target(s).
	 *
	 * @param metadata Represents the schema to be dropped.
	 * @param options Options for executing the drop
	 * @param sourceDescriptor description of the source(s) of drop commands
	 * @param targetDescriptor description of the target(s) for the drop commands
	 * @deprecated Use {@link #doDrop(SchemaActionMetadata, ExecutionOptions, SourceDescriptor, TargetDescriptor)} instead.
	 */
	@Deprecated
	default void doDrop(Metadata metadata, ExecutionOptions options, SourceDescriptor sourceDescriptor, TargetDescriptor targetDescriptor) {
		doDrop( metadata.forSchemaToolLegacy(), options, sourceDescriptor, targetDescriptor );
	}

	/**
	 * Build a delayed Runnable for performing schema dropping.  This implicitly
	 * targets the underlying data-store.
	 *
	 * @param metadata The metadata to drop
	 * @param options The drop options
	 * @param sourceDescriptor For access to the {@link SourceDescriptor#getScriptSourceInput()}
	 *
	 * @return The Runnable
	 */
	DelayedDropAction buildDelayedAction(SchemaActionMetadata metadata, ExecutionOptions options, SourceDescriptor sourceDescriptor);

	/**
	 * Build a delayed Runnable for performing schema dropping.  This implicitly
	 * targets the underlying data-store.
	 *
	 * @param metadata The metadata to drop
	 * @param options The drop options
	 * @param sourceDescriptor For access to the {@link SourceDescriptor#getScriptSourceInput()}
	 *
	 * @return The Runnable
	 * @deprecated
	 */
	@Deprecated
	default DelayedDropAction buildDelayedAction(Metadata metadata, ExecutionOptions options, SourceDescriptor sourceDescriptor) {
		return buildDelayedAction( metadata.forSchemaToolLegacy(), options, sourceDescriptor );
	}
}
