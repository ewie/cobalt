/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.models.Property;

/**
 * Provides property provisions for a set of requested properties.
 *
 * @author Erik Wienhold
 */
public interface PropertyProvisionProvider {

  /**
   * Get property provisions each offering a property compatible with any of the given properties.
   *
   * @param properties a set of requested properties
   *
   * @return a set of property provisions
   */
  Set<PropertyProvision> getProvisionsFor(Set<Property> properties);

}
