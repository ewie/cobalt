/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

/**
 * A pair of a property and an action publishing the property.
 *
 * @author Erik Wienhold
 */
public class PublishedProperty {

  private final Property property;

  private final Action action;

  /**
   * @param property a property published by an action
   * @param action   the publishing action
   */
  public PublishedProperty(final Property property, final Action action) {
    if (!action.publishes(property)) {
      throw new IllegalArgumentException("expecting property to be published by given action");
    }
    this.property = property;
    this.action = action;
  }

  public Property getProperty() {
    return property;
  }

  public Action getAction() {
    return action;
  }

  @Override
  public int hashCode() {
    return Objects.hash(property, action);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof PublishedProperty
        && equals((PublishedProperty) other);
  }

  private boolean equals(final PublishedProperty other) {
    return Objects.equals(property, other.property)
        && Objects.equals(action, other.action);
  }

}
