/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.distance;

import vsr.cobalt.planner.Repository;
import vsr.cobalt.planner.graph.FunctionalityProvision;

/**
 * @author Erik Wienhold
 */
public class FunctionalityProvisionDistanceMeter implements ProvisionDistanceMeter<FunctionalityProvision> {

  private final Repository repository;

  public FunctionalityProvisionDistanceMeter(final Repository repository) {
    this.repository = repository;
  }

  @Override
  public double measureDistance(final FunctionalityProvision provision) {
    return repository.getDistance(provision.getRequest(), provision.getOffer().getSubject());
  }

}
