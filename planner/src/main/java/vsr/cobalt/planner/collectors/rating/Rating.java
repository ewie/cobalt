/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors.rating;

/**
 * Represents a rating based on a signed integer value.
 * <p/>
 * The natural order of a rating is determined by the natural order of its value.
 *
 * @author Erik Wienhold
 */
public final class Rating implements Comparable<Rating> {

  private final int value;

  /**
   * @param value the rating's value
   */
  public Rating(final int value) {
    this.value = value;
  }

  /**
   * @return the rating's value
   */
  public int getValue() {
    return value;
  }

  @Override
  public int compareTo(final Rating other) {
    return Integer.compare(value, other.value);
  }

  @Override
  public int hashCode() {
    return value;
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
