/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

/**
 * A property offered through publication.
 *
 * @author Erik Wienhold
 */
public final class PublishedProperty extends Offer<Property> {

  /**
   * @param property a property published by an action
   * @param action   the publishing action
   */
  public PublishedProperty(final Property property, final Action action) {
    super(property, action);
    if (!action.publishes(property)) {
      throw new IllegalArgumentException("expecting property to be published by given action");
    }
  }

  @Override
  public boolean canEqual(final Object other) {
    return other instanceof PublishedProperty;
  }

}
