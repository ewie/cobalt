/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Set;

import vsr.cobalt.planner.models.Action;

/**
 * A graph level.
 *
 * @author Erik Wienhold
 */
public interface Level {

  /**
   * @return a set of actions required in this level
   */
  Set<Action> getRequiredActions();

}
