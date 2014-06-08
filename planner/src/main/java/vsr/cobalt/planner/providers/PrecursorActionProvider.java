/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

import java.util.Set;

import vsr.cobalt.models.Action;

/**
 * Provides precursor actions for a requested action.
 *
 * @author Erik Wienhold
 */
public interface PrecursorActionProvider {

  /**
   * Get precursor actions for a requested action.
   *
   * @param action a requested action
   *
   * @return a set of precursor actions
   */
  Set<Action> getPrecursorActionsFor(Action action);

}
