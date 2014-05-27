/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

/**
 * Base class for everything owning an identifier.
 *
 * @author Erik Wienhold
 */
abstract class Identifiable {

  private final String identifier;

  Identifiable(final String identifier) {
    this.identifier = identifier;
  }

  /**
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }

  protected abstract boolean canEqual(Object other);

  @Override
  public int hashCode() {
    return identifier.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Identifiable
        && equals((Identifiable) other);
  }

  private boolean equals(final Identifiable other) {
    return other.canEqual(this)
        && identifier.equals(other.identifier);
  }

}
