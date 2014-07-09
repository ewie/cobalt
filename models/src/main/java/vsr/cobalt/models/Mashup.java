/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Describes a desired mashup.
 *
 * @author Erik Wienhold
 */
public final class Mashup {

  /**
   * A non-empty set of functionalities to be realized by the mashup.
   */
  private final Set<Functionality> functionalities;

  /**
   * Create a new mashup.
   *
   * @param functionalities a non-empty set of functionalities to be realized by the mashup
   */
  public Mashup(final Set<Functionality> functionalities) {
    if (functionalities.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more functionalities");
    }
    this.functionalities = ImmutableSet.copyOf(functionalities);
  }

  /**
   * @return the non-empty set of functionalities to be realized by this mashup
   */
  public Set<Functionality> getFunctionalities() {
    return functionalities;
  }

  @Override
  public int hashCode() {
    return functionalities.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Mashup
        && equals((Mashup) other);
  }

  private boolean equals(final Mashup other) {
    return functionalities.equals(other.functionalities);
  }

}
