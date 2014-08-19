/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders.providers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PropositionSet;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Widget;
import vsr.cobalt.utils.OrderedPowerSetIterator;

/**
 * A precursor action provider which uses the precursor actions from another provider, to form extended precursor
 * actions. The extended precursor actions satisfy as many pre-conditions of a requested action as possible,
 * beyond the minimal set of pre-conditions for which a precursor action is required.
 *
 * @author Erik Wienhold
 */
public class ComposingExtendedPrecursorActionProvider implements PrecursorActionProvider {

  private final Repository repository;

  private final PrecursorActionProvider precursorActionProvider;

  /**
   * @param repository              a repository returning a widget's actions
   * @param precursorActionProvider a precursor action provider
   */
  public ComposingExtendedPrecursorActionProvider(final Repository repository,
                                                  final PrecursorActionProvider precursorActionProvider) {
    this.repository = repository;
    this.precursorActionProvider = precursorActionProvider;
  }

  @Override
  public Set<Action> getPrecursorActionsFor(final Action action) {
    final Set<Action> precursors = getPrecursors(action);
    final Set<Action> extendedPrecursors = new HashSet<>(precursors);

    final Set<Action> fillingActions = selectFillingActions(action, getWidgetActions(action.getWidget()));
    for (final Action precursor : precursors) {
      extendedPrecursors.addAll(createExtendedPrecursors(precursor, fillingActions));
    }

    return extendedPrecursors;
  }

  private Set<Action> getWidgetActions(final Widget widget) {
    return repository.getWidgetActions(widget);
  }

  private Set<Action> getPrecursors(final Action action) {
    return precursorActionProvider.getPrecursorActionsFor(action);
  }

  /**
   * Create a set of extended precursor actions by composing an existing precursor with combinations from a set of
   * actions.
   *
   * @param precursor an existing precursor action
   * @param actions   a set of actions to form combinations
   *
   * @return a set of extended precursor actions, an empty set when no action is composable with the existing precursor
   */
  private static Set<Action> createExtendedPrecursors(final Action precursor, final Set<Action> actions) {
    final Set<Action> precursors = new HashSet<>();
    final OrderedPowerSetIterator<Action> it = new OrderedPowerSetIterator<>(actions);

    while (it.hasNext()) {
      final Set<Action> as = it.next();
      final Set<Action> combination = combine(precursor, as);
      if (Action.isComposable(combination)) {
        // We can safely add the composition as precursor, as no action must interfere with one another.
        precursors.add(Action.compose(combination));
      } else {
        it.excludeSuperSetsOf(as);
      }
    }

    return precursors;
  }

  /**
   * Select actions which fill properties required filled by a requested action.
   *
   * @param action  a requested action
   * @param actions a set of actions from the same widget
   *
   * @return a set of filling actions
   */
  private static Set<Action> selectFillingActions(final Action action, final Set<Action> actions) {
    final Set<Action> filling = new HashSet<>();
    for (final Action a : actions) {
      if (isFillingAction(a, action)) {
        filling.add(a);
      }
    }
    // create maintenance actions for all properties required filled
    for (final Property p : action.getPreConditions().getFilledProperties()) {
      filling.add(createMaintenanceAction(action.getWidget(), p));
    }
    return filling;
  }

  /**
   * Test if an action fills any property required filled by an action.
   *
   * @param candidate a candidate action to test
   * @param action    an action requiring filled properties
   *
   * @return true when the candidate fills required properties, false otherwise
   */
  private static boolean isFillingAction(final Action candidate, final Action action) {
    return !Collections.disjoint(candidate.getPostConditions().getFilledProperties(),
        action.getPreConditions().getFilledProperties());
  }

  /**
   * Create a maintenance action for a filled property.
   *
   * @param widget   a widget
   * @param property a property
   *
   * @return a maintenance action
   */
  private static Action createMaintenanceAction(final Widget widget, final Property property) {
    return Action.create(widget, PropositionSet.filled(property));
  }

  private static Set<Action> combine(final Action action, final Set<Action> actions) {
    final Set<Action> as = new HashSet<>(actions);
    as.add(action);
    return as;
  }

}
