/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders;

import vsr.cobalt.models.Action;
import vsr.cobalt.planner.graph.Graph;

/**
 * Detects actions that would cause a cyclic dependency when used to extend a graph.
 *
 * @author Erik Wienhold
 */
public interface CyclicDependencyDetector {

  /**
   * Test if an action creates a cyclic dependency via some other action.
   *
   * @param support   an action supporting some dependent action
   * @param dependent an action depending on the supporting action
   * @param graph     a graph containing the dependent action
   *
   * @return true when there's a cyclic dependency, false otherwise
   */
  boolean createsCyclicDependencyVia(Action support, Action dependent, Graph graph);

}
