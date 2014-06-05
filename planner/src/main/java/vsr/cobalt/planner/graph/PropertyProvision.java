/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;

/**
 * A property provision specifies how a requested property can be satisfied via a providing action offering a matching
 * property through publication.
 *
 * @author Erik Wienhold
 */
public final class PropertyProvision extends Provision<Property> {

  /**
   * Create a new property provision.
   *
   * @param request a requested property
   * @param offer   a published property
   */
  public PropertyProvision(final Property request, final PublishedProperty offer) {
    super(request, offer);
  }

  /**
   * Create a new property provision using the offered property as requested property.
   *
   * @param offer a published property
   */
  public PropertyProvision(final PublishedProperty offer) {
    super(offer);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof PropertyProvision;
  }

}
