/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;

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
   * @param request the requested property
   * @param offer   the provided property matching the requested property
   * @param action  the action publishing the provided property
   */
  public PropertyProvision(final Property request, final Property offer, final Action action) {
    super(request, offer, action);
    if (!action.publishes(offer)) {
      throw new IllegalArgumentException("expecting the offered property to be published by providing action");
    }
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof PropertyProvision;
  }

}
