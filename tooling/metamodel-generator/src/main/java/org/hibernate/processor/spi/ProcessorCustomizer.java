/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.spi;

// TODO javadoc, etc. This is just a POC.
public interface ProcessorCustomizer {

	void customize(Context context);

	interface Context {
		void addInheritedAnnotations(String ... annotations);
	}

}
