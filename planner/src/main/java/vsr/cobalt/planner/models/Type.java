/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

/**
 * A type represents a concept of uniquely identified value space.
 *
 * @author Erik Wienhold
 */
public final class Type extends Identifiable {

  /**
   * Create a new type.
   *
   * @param identifier a type identifier
   */
  public Type(final String identifier) {
    super(identifier);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof Type;
  }

}
