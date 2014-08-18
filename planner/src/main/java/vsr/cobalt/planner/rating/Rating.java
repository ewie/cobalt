/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.rating;

/**
 * Represents a rating based on a double-precision value.
 * <p/>
 * The natural order of ratings is determined by the value's natural order.
 *
 * @author Erik Wienhold
 */
public final class Rating implements Comparable<Rating> {

  private final double value;

  /**
   * @param value the rating's value
   */
  public Rating(final double value) {
    this.value = value;
  }

  /**
   * @return the rating's value
   */
  public double getValue() {
    return value;
  }

  @Override
  public int compareTo(final Rating other) {
    return Double.compare(value, other.value);
  }

  @Override
  public int hashCode() {
    return Double.valueOf(value).hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Rating
        && equals((Rating) other);
  }

  private boolean equals(final Rating other) {
    return value == other.value;
  }

}
