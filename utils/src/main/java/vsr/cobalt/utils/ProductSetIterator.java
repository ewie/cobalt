/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;

/**
 * Iterator over each product set of a set of sets.
 * <p/>
 * The iteration is realized by handling each product set as a unique number. Each position corresponds to one of the
 * input sets and thus has a base equal to the respective set cardinality. By counting through all those numbers we can
 * produce each product set.
 * <pre>
 * Example:
 *   A := {0,1}
 *   | B := {2,3,5}
 *   | | C := {6}
 *   | | |
 * {{0,2,6},
 *  {0,3,6},
 *  {0,5,6},
 *  {1,2,6},
 *  {1,3,6},
 *  {1,5,6}} =: A x B x C
 * </pre>
 */
public class ProductSetIterator<E> extends AbstractIterator<Set<E>> {

  /**
   * The counters created from each set.
   */
  private final ImmutableList<Counter<E>> counters;

  /**
   * Indicate when iteration is done.
   */
  private boolean done;

  public ProductSetIterator(final Set<? extends Set<E>> sets) {
    for (final Set<E> set : sets) {
      if (set.isEmpty()) {
        throw new IllegalArgumentException("expecting a set of non-empty sets");
      }
    }
    counters = createCounters(sets);
    // when there are no sets we are already done
    done = sets.isEmpty();
  }

  @Override
  protected Set<E> computeNext() {
    if (done) {
      return endOfData();
    }

    final Set<E> values = new HashSet<>(counters.size());

    done = true;

    for (final Counter<E> counter : counters) {
      values.add(counter.getValue());
      done = done && counter.increment();
    }

    return values;
  }

  private static <E> ImmutableList<Counter<E>> createCounters(final Set<? extends Set<E>> sets) {
    final ImmutableList.Builder<Counter<E>> counters = ImmutableList.builder();
    for (final Set<E> set : sets) {
      counters.add(new Counter<>(set));
    }
    return counters.build();
  }

  /**
   * Counts through a set of values.
   *
   * @param <E>
   */
  private static class Counter<E> {

    /**
     * A sequence of values.
     */
    private final ImmutableList<E> values;

    /**
     * The index of the current value.
     */
    private int index;

    public Counter(final Set<E> values) {
      // convert to a sequence to get random access
      this.values = ImmutableList.copyOf(values);
    }

    /**
     * @return the current value
     */
    public E getValue() {
      return values.get(index);
    }

    /**
     * Increment the counter and wrap around when necessary.
     *
     * @return true when the counter overflowed
     */
    public boolean increment() {
      index = (index + 1) % values.size();
      return index == 0;
    }

  }

}
