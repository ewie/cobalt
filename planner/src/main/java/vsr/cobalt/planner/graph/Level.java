/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import com.google.common.collect.ImmutableSet;
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
  public ImmutableSet<Action> getRequiredActions();

}
