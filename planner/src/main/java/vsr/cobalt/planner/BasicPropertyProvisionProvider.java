/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.models.Property;
import vsr.cobalt.planner.graph.PropertyProvision;

/**
 * A property provision provider which simply delegates every property request to a repository and collects all found
 * provisions.
 *
 * @author Erik Wienhold
 */
public class BasicPropertyProvisionProvider implements PropertyProvisionProvider {

  private final Repository repository;

  /**
   * @param repository the repository to delegate to
   */
  public BasicPropertyProvisionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  public Set<PropertyProvision> getProvisionsFor(final Set<Property> properties) {
    final Set<PropertyProvision> pps = new HashSet<>();
    for (final Property p : properties) {
      pps.addAll(repository.findCompatibleProperties(p));
    }
    return pps;
  }

}
