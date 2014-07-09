/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

/**
 * A functionality offered through realization.
 *
 * @author Erik Wienhold
 */
public final class RealizedFunctionality extends Offer<Functionality> {

  /**
   * @param functionality a functionality realized by an action
   * @param action        an action realizing the functionality
   */
  public RealizedFunctionality(final Functionality functionality, final Action action) {
    super(functionality, action);
    if (!action.realizes(functionality)) {
      throw new IllegalArgumentException("expecting functionality to be realized by given action");
    }
  }

  @Override
  public boolean canEqual(final Object other) {
    return other instanceof RealizedFunctionality;
  }

}
