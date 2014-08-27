/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.distance;

import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.graph.PropertyProvision;

/**
 * @author Erik Wienhold
 */
public class PropertyProvisionDistanceMeter implements ProvisionDistanceMeter<PropertyProvision> {

  private final Repository repository;

  public PropertyProvisionDistanceMeter(final Repository repository) {
    this.repository = repository;
  }

  @Override
  public double measureDistance(final PropertyProvision provision) {
    return repository.getDistance(provision.getRequest().getType(),
        provision.getOffer().getSubject().getType());
  }

}
