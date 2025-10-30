/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.ormPanache;

import java.util.List;

import io.quarkus.panache.hibernate.common.processor.MyCustomAnnotation;
import org.hibernate.annotations.processing.Find;
import org.hibernate.annotations.processing.HQL;

public interface QuarkusBookRepository {
	@Find
	@MyCustomAnnotation
	public List<PanacheBook> findBook(String isbn);

	@HQL("WHERE isbn = :isbn")
	@MyCustomAnnotation
	public List<PanacheBook> hqlBook(String isbn);
}
