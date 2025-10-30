/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package io.quarkus.panache.hibernate.common.processor;

import org.hibernate.processor.spi.ProcessorCustomizer;

public class PanacheProcessorCustomizer implements ProcessorCustomizer {
	@Override
	public void customize(Context context) {
		context.addInheritedAnnotations( "io.quarkus.panache.hibernate.common.processor.MyCustomAnnotation" );
	}
}
