/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

/**
 * Base class for everything owning an identifier.
 *
 * @author Erik Wienhold
 */
public abstract class Identifiable {

  private final Identifier identifier;

  Identifiable(final Identifier identifier) {
    this.identifier = identifier;
  }

  /**
   * @return the identifier
   */
  public Identifier getIdentifier() {
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
