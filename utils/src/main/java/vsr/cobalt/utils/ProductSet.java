/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.Set;

/**
 * The product set (cross product) of a set of sets.
 *
 * @author Erik Wienhold
 */
public class ProductSet<E> implements Iterable<Set<E>> {

  private final Set<? extends Set<E>> sets;

  /**
   * Create a product set using a set of sets.
   *
   * @param sets a set of sets
   */
  public ProductSet(final Set<? extends Set<E>> sets) {
    for (final Set<E> set : sets) {
      if (set.isEmpty()) {
        throw new IllegalArgumentException("expecting a set of non-empty sets");
      }
    }
    this.sets = sets;
  }

  @Override
  public ProductSetIterator<E> iterator() {
    return new ProductSetIterator<>(sets);
  }

}
