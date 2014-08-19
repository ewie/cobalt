/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders.providers;

import vsr.cobalt.models.Property;
import vsr.cobalt.planner.graph.PropertyProvision;

/**
 * Provides property provisions for a set of requested properties.
 *
 * @author Erik Wienhold
 */
public interface PropertyProvisionProvider extends ProvisionProvider<Property, PropertyProvision> {
}
