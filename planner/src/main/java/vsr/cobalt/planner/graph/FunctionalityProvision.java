/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.RealizedFunctionality;

/**
 * A functionality provision specifies how a requested functionality can be satisfied via a providing action realizing
 * a
 * matching functionality.
 *
 * @author Erik Wienhold
 */
public final class FunctionalityProvision extends Provision<Functionality> {

  /**
   * Create a new functionality provision.
   *
   * @param request a requested functionality
   * @param offer   a realized functionality
   */
  public FunctionalityProvision(final Functionality request, final RealizedFunctionality offer) {
    super(request, offer);
  }

  /**
   * Create a new functionality provision using the offered functionality as requested functionality.
   *
   * @param offer a realized functionality
   */
  public FunctionalityProvision(final RealizedFunctionality offer) {
    super(offer);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof FunctionalityProvision;
  }

}
