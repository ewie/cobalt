/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Widget;

/**
 * Provides all precursor actions among the widget actions returned by a repository.
 *
 * @author Erik Wienhold
 */
public class BasicPrecursorActionProvider implements PrecursorActionProvider {

  private final Repository repository;

  /**
   * @param repository a repository providing a widget's actions
   */
  public BasicPrecursorActionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  public Set<Action> getPrecursorActionsFor(final Action action) {
    final Set<Action> precursors = new HashSet<>();
    for (final Action a : getWidgetActions(action.getWidget())) {
      if (a.canBePrecursorOf(action)) {
        precursors.add(a);
      }
    }
    return precursors;
  }

  private Set<Action> getWidgetActions(final Widget widget) {
    return repository.getWidgetActions(widget);
  }

}
