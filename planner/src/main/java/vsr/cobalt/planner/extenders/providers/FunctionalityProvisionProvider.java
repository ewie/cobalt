/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders.providers;

import vsr.cobalt.models.Functionality;
import vsr.cobalt.planner.graph.FunctionalityProvision;

/**
 * Provides functionality provisions for a set of requested functionalities.
 *
 * @author Erik Wienhold
 */
public interface FunctionalityProvisionProvider extends ProvisionProvider<Functionality, FunctionalityProvision> {
}
