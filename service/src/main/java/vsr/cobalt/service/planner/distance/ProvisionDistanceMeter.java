/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner.distance;

import vsr.cobalt.planner.graph.Provision;

/**
 * @author Erik Wienhold
 */
public interface ProvisionDistanceMeter<T extends Provision<?>> {

  double measureDistance(T provision);

}
