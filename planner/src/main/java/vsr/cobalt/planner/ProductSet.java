/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import com.google.common.collect.ImmutableSet;

/**
 * The product set (cross product) of a set of sets.
 *
 * @author Erik Wienhold
 */
class ProductSet<E> implements Iterable<ImmutableSet<E>> {

  private final ImmutableSet<ImmutableSet<E>> sets;

  /**
   * Create a product set using a set of sets.
   *
   * @param sets a set of sets
   */
  public ProductSet(final ImmutableSet<ImmutableSet<E>> sets) {
    for (final ImmutableSet<E> set : sets) {
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
