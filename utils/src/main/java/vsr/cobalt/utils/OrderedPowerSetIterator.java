/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Iterates over each subset of a fixed set, excluding the empty set. The subsets are ordered by their cardinality
 * starting with any single element subset.
 * <p/>
 * Algorithm:
 * <pre>
 * Input set S
 * Output list P
 *
 * Q := queue ({}, S)
 * while not empty Q
 *   (s, t) := dequeue Q
 *   foreach e in t
 *     u := union s {e}
 *     unless excluded u
 *       P := append P u
 *       Q := enqueue Q (u, S \ u)
 * return P
 * </pre>
 *
 * @author Erik Wienhold
 */
public class OrderedPowerSetIterator<E> extends AbstractIterator<Set<E>> {

  private final ArrayDeque<Pair<E>> queue = new ArrayDeque<>();

  private final Set<Set<E>> excluded = new HashSet<>();

  private ExpansionIterator expansions;

  /**
   * @param set a set
   */
  public OrderedPowerSetIterator(final Set<E> set) {
    final TailIterator<E> it = new TailIterator<>(ImmutableList.copyOf(set));
    expansions = new ExpansionIterator(Collections.<E>emptySet(), it);
  }

  /**
   * Exclude all supersets of a given set in future calls to {@link #next()}.
   *
   * @param set a set whose supersets should be excluded
   */
  public void excludeSuperSetsOf(final Set<E> set) {
    excluded.add(ImmutableSet.copyOf(set));
  }

  @Override
  protected Set<E> computeNext() {
    final ExpansionIterator it = getExpansions();
    if (it == null) {
      return endOfData();
    } else {
      return it.next();
    }
  }

  /**
   * Check if a set is excluded by {@link #excludeSuperSetsOf(Set)}.
   *
   * @param set the set to check
   *
   * @return whether the set should be excluded or not
   */
  private boolean isExcluded(final Set<E> set) {
    for (final Set<E> subset : excluded) {
      if (set.containsAll(subset)) {
        return true;
      }
    }
    return false;
  }

  private ExpansionIterator getExpansions() {
    while (!expansions.hasNext()) {
      if (queue.isEmpty()) {
        expansions = null;
        break;
      }
      final Pair<E> p = queue.remove();
      expansions = new ExpansionIterator(p.set, p.tail);
    }
    return expansions;
  }

  private class ExpansionIterator extends AbstractIterator<Set<E>> {

    private final Set<E> set;

    private final TailIterator<E> tail;

    public ExpansionIterator(final Set<E> set, final TailIterator<E> tail) {
      this.set = set;
      this.tail = tail;
    }

    @Override
    protected Set<E> computeNext() {
      while (tail.hasNext()) {
        final Set<E> set = createSet(tail.next());
        if (!isExcluded(set)) {
          final Pair<E> pair = new Pair<>(set, tail.getRemainingIterator());
          queue.add(pair);
          return set;
        }
      }
      return endOfData();
    }

    private Set<E> createSet(final E e) {
      final Set<E> set = new HashSet<>(1 + this.set.size());
      set.addAll(this.set);
      set.add(e);
      return set;
    }

  }

  private static class Pair<E> {

    public final Set<E> set;

    public final TailIterator<E> tail;

    public Pair(final Set<E> set, final TailIterator<E> tail) {
      this.set = set;
      this.tail = tail;
    }

  }

  private static class TailIterator<E> extends UnmodifiableIterator<E> {

    private final List<E> list;

    private int index;

    public TailIterator(final List<E> list, final int index) {
      this.list = list;
      this.index = index;
    }

    public TailIterator(final List<E> list) {
      this(list, 0);
    }

    public TailIterator<E> getRemainingIterator() {
      return new TailIterator<>(list, index);
    }

    @Override
    public boolean hasNext() {
      return index < list.size();
    }

    @Override
    public E next() {
      return list.get(index++);
    }

  }

}
