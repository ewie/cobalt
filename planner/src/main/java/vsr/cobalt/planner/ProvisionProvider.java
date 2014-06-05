/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import vsr.cobalt.planner.graph.Provision;

/**
 * @author Erik Wienhold
 */
public interface ProvisionProvider<T, P extends Provision<T>> {

  /**
   * Get provisions each providing an offer compatible with any of the given requests.
   *
   * @param requests a set of requested subjects
   *
   * @return a set of provisions
   */
  Set<P> getProvisionsFor(Set<T> requests);

}
