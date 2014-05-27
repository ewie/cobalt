/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

import java.util.Objects;

/**
 * A property describes a piece of internal state used by a widget.
 *
 * @author Erik Wienhold
 */
public final class Property {

  /**
   * The property name.
   */
  private final String name;

  /**
   * The property type.
   */
  private final Type type;

  /**
   * Create a new property.
   *
   * @param name the name
   * @param type the type
   */
  public Property(final String name, final Type type) {
    this.name = name;
    this.type = type;
  }

  /**
   * @return the property name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the property type
   */
  public Type getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Property
        && equals((Property) other);
  }

  private boolean equals(final Property other) {
    return Objects.equals(name, other.name)
        && Objects.equals(type, other.type);
  }

}
