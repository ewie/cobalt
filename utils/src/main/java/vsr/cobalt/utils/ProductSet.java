/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.Collections;
import java.util.Set;

/**
 * The product set (cross product) of a set of sets.
 *
 * @author Erik Wienhold
 */
public class ProductSet<E> implements Iterable<Set<E>> {

  @SuppressWarnings("unchecked")
  private static final ProductSet<?> EMPTY = new ProductSet<>(Collections.EMPTY_SET);

  private final Set<? extends Set<E>> sets;

  /**
   * Create a product set using a set of sets.
   *
   * @param sets a set of sets
   */
  public ProductSet(final Set<? extends Set<E>> sets) {
    this.sets = sets;
  }

  @SuppressWarnings("unchecked")
  public static <E> ProductSet<E> empty() {
    return (ProductSet<E>) EMPTY;
  }

  @Override
  public ProductSetIterator<E> iterator() {
    return new ProductSetIterator<>(sets);
  }

}
