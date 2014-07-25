/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

/**
 * Indicate the presence or absence of a property's value.
 *
 * @author Erik Wienhold
 */
public final class Proposition {

  public static final boolean CLEARED_VALUE = false;

  public static final boolean FILLED_VALUE = true;

  private final Property property;

  private final boolean isFilled;

  public Proposition(final Property property, final boolean isFilled) {
    this.property = property;
    this.isFilled = isFilled;
  }

  public static Proposition cleared(final Property property) {
    return new Proposition(property, CLEARED_VALUE);
  }

  public static Proposition filled(final Property property) {
    return new Proposition(property, FILLED_VALUE);
  }

  public Property getProperty() {
    return property;
  }

  public boolean isFilled() {
    return isFilled;
  }

  public Proposition negation() {
    return new Proposition(property, !isFilled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(property, isFilled);
  }

  @Override
  public boolean equals(final Object other) {
    return this == other
        || other instanceof Proposition
        && equals((Proposition) other);
  }

  private boolean equals(final Proposition other) {
    return property.equals(other.property)
        && isFilled == other.isFilled;
  }

}
