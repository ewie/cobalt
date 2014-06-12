/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link LinkedHashMap} capped to a maximum capacity, by removing the oldest entry when a new pair will exceed the
 * capacity.
 *
 * @author Erik Wienhold
 */
public class CappedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

  private final int maxCapacity;

  /**
   * @param maxCapacity a positive maximal capacity
   */
  public CappedLinkedHashMap(final int maxCapacity) {
    if (maxCapacity < 1) {
      throw new IllegalArgumentException("expecting positive maximal capacity");
    }
    this.maxCapacity = maxCapacity;
  }

  /**
   * @return the maximal capacity
   */
  public int getMaxCapacity() {
    return maxCapacity;
  }

  @Override
  public boolean removeEldestEntry(final Map.Entry<K, V> entry) {
    return size() > maxCapacity;
  }

}
