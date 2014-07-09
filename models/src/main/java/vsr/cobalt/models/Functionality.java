/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

/**
 * Represents a concept of uniquely identified functionality.
 *
 * @author Erik Wienhold
 */
public final class Functionality extends Identifiable {

  /**
   * Create a new functionality.
   *
   * @param identifier a functionality identifier
   */
  public Functionality(final Identifier identifier) {
    super(identifier);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof Functionality;
  }

}
