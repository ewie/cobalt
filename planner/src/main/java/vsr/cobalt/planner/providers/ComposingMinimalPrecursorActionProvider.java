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
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PropositionSet;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Widget;
import vsr.cobalt.utils.OrderedPowerSetIterator;

/**
 * A provider for precursor actions, which composes actions when possible to create precursor actions not explicitly
 * provided by the repository. The provided precursor actions are minimal, i.e. they satisfy only the minimal set of
 * requirements of a requested action.
 *
 * @author Erik Wienhold
 */
public class ComposingMinimalPrecursorActionProvider implements PrecursorActionProvider {

  private final Repository repository;

  /**
   * @param repository a repository to provide actions
   */
  public ComposingMinimalPrecursorActionProvider(final Repository repository) {
    this.repository = repository;
  }

  @Override
  public Set<Action> getPrecursorActionsFor(final Action action) {
    return createPrecursors(action,
        selectPartialPrecursors(action,
            getWidgetActions(action.getWidget())));
  }

  private Set<Action> getWidgetActions(final Widget widget) {
    return repository.getWidgetActions(widget);
  }

  /**
   * Select actions which are partial precursors for a requested action from all widget actions and maintenance
   * actions.
   *
   * @param action  a requested action
   * @param actions a set of actions from the same widget
   *
   * @return a set of partial precursors
   */
  private static Set<Action> selectPartialPrecursors(final Action action, final Set<Action> actions) {
    final Set<Action> partials = new HashSet<>();
    for (final Action a : actions) {
      if (isPartialPrecursor(a, action)) {
        partials.add(a);
      }
    }
    // create maintenance actions for all properties required cleared
    for (final Property p : action.getPreConditions().getClearedProperties()) {
      partials.add(createMaintenanceAction(action.getWidget(), p));
    }
    return partials;
  }

  /**
   * Create a maintenance action for a cleared property.
   *
   * @param widget   a widget
   * @param property a property
   *
   * @return a maintenance action
   */
  private static Action createMaintenanceAction(final Widget widget, final Property property) {
    return Action.create(widget, PropositionSet.cleared(property));
  }

  /**
   * Create precursor actions from a set of partial precursors.
   *
   * @param action   an action requiring a precursor
   * @param partials a set of partial precursors
   *
   * @return a set of precursor actions
   */
  private static Set<Action> createPrecursors(final Action action, final Set<Action> partials) {
    final Set<Action> precursors = new HashSet<>();

    // iterate over all combinations of partial precursors
    final OrderedPowerSetIterator<Action> it = new OrderedPowerSetIterator<>(partials);

    while (it.hasNext()) {
      final Set<Action> combination = it.next();
      if (Action.isComposable(combination)) {
        final Action ca = Action.compose(combination);
        if (!ca.isMaintenance() && ca.canBePrecursorOf(action)) {
          precursors.add(ca);
          // supersets of a sufficient combination can be skipped because they would not satisfy anything not already
          // satisfied
          it.excludeSuperSetsOf(combination);
        }
      } else {
        // any superset will not be composable either, therefore we can skip those combinations
        it.excludeSuperSetsOf(combination);
      }
    }

    return precursors;
  }

  /**
   * Test if an action is a partial precursor for another action, i.e. it satisfies a subset of requirements for which
   * a precursor action is required.
   *
   * @param candidate an action to test
   * @param action    an action requiring a precursor
   *
   * @return true when
   */
  private static boolean isPartialPrecursor(final Action candidate, final Action action) {
    final PropositionSet post = candidate.getPostConditions();
    final PropositionSet pre = action.getPreConditions();
    for (final Property p : pre.getClearedProperties()) {
      // a partial precursor must clear at least one property required cleared
      if (post.isCleared(p)) {
        return true;
      }
      // cannot be a partial precursor when it fills a property required cleared
      if (post.isFilled(p)) {
        return false;
      }
    }
    return false;
  }

}
