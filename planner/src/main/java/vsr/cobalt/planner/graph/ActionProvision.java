/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;

/**
 * Specifies how a requested action with non-empty pre-conditions may be enabled through an optional precursor action
 * and additional actions providing required properties.
 *
 * @author Erik Wienhold
 */
public final class ActionProvision {

  /**
   * The action to enable.
   */
  private final Action requestedAction;

  /**
   * An optional action which is executed before {@link #requestedAction} within the same widget instance to satisfy
   * certain pre-conditions of {@link #requestedAction}.
   */
  private final Action precursorAction;

  /**
   * A set of property provisions for properties not satisfies by {@link #precursorAction} when given.
   */
  private final ImmutableSet<PropertyProvision> propertyProvisions;

  /**
   * Create a provision with a requested action and an optional precursor action according to {@link
   * Action#canBePrecursorOf(Action) }. A set of zero or more property provisions specify how properties, not provided
   * by the precursor, can be attained through other actions.
   *
   * @param requestedAction    an action to enable
   * @param precursorAction    an action acting as the precursor for {@code action}
   * @param propertyProvisions a set of property provisions
   */
  private ActionProvision(final Action requestedAction, final Action precursorAction,
                          final ImmutableSet<PropertyProvision> propertyProvisions) {
    if (precursorAction == null) {
      assertWithoutPrecursor(requestedAction, propertyProvisions);
    } else {
      assertWithPrecursor(requestedAction, precursorAction, propertyProvisions);
    }
    assertProvisions(propertyProvisions);
    this.requestedAction = requestedAction;
    this.precursorAction = precursorAction;
    this.propertyProvisions = propertyProvisions;
  }

  /**
   * Create a provision with a requested action and a precursor action according to {@link
   * Action#canBePrecursorOf(Action) }. A set of zero or more property provisions specify how properties, not provided
   * by the precursor, can be attained through other actions.
   *
   * @param requestedAction    an action to enable
   * @param precursorAction    a precursor action
   * @param propertyProvisions a set of property provisions f
   *
   * @return a new action provision
   */
  public static ActionProvision createWithPrecursor(final Action requestedAction, final Action precursorAction,
                                                    final ImmutableSet<PropertyProvision> propertyProvisions) {
    return new ActionProvision(requestedAction, precursorAction, propertyProvisions);
  }

  /**
   * Create a provision which does not require any property provisions.
   *
   * @param requestedAction an action to enable
   * @param precursorAction a precursor action
   *
   * @return a new action provision
   *
   * @see #createWithPrecursor(Action, Action, ImmutableSet)
   */
  public static ActionProvision createWithPrecursor(final Action requestedAction, final Action precursorAction) {
    return createWithPrecursor(requestedAction, precursorAction, ImmutableSet.<PropertyProvision>of());
  }

  /**
   * Create a provision with an action requiring no precursor action according to {@link Action#requiresPrecursor()}. A
   * non-empty set of property provisions specify how properties can be attained through other actions.
   *
   * @param requestedAction an action to enable
   * @param provisions      a non-empty set of property provisions
   *
   * @return a new action provision
   */
  public static ActionProvision createWithoutPrecursor(final Action requestedAction,
                                                       final ImmutableSet<PropertyProvision> provisions) {
    return new ActionProvision(requestedAction, null, provisions);
  }

  /**
   * @return the action requested to be enabled
   */
  public Action getRequestedAction() {
    return requestedAction;
  }

  /**
   * An action which is required by {@link #getRequestedAction()} according to {@link Action#canBePrecursorOf(Action)}.
   *
   * @return the precursor action or null when not specified
   */
  public Action getPrecursorAction() {
    return precursorAction;
  }

  /**
   * A set of zero or more property provisions specifying how properties, required by the action, can be provided.
   *
   * @return the set of zero or more property provisions
   */
  public ImmutableSet<PropertyProvision> getPropertyProvisions() {
    return propertyProvisions;
  }

  /**
   * Get all actions required to enable {@link #getRequestedAction()}.
   * <p/>
   * This includes the providing actions, as well as the optional precursor action.
   *
   * @return the set of required actions
   *
   * @see #getProvidingActions()
   */
  public ImmutableSet<Action> getRequiredActions() {
    final ImmutableSet.Builder<Action> b = ImmutableSet.builder();
    if (precursorAction != null) {
      b.add(precursorAction);
    }
    b.addAll(getProvidingActions());
    return b.build();
  }

  /**
   * Get all actions which provide a property via a property provision.
   *
   * @return the set of providing actions
   */
  public ImmutableSet<Action> getProvidingActions() {
    final ImmutableSet.Builder<Action> as = ImmutableSet.builder();
    for (final PropertyProvision pp : propertyProvisions) {
      as.add(pp.getProvidingAction());
    }
    return as.build();
  }

  /**
   * Get all properties requested by the requested action.
   *
   * @return the set of requested properties
   */
  public ImmutableSet<Property> getRequestedProperties() {
    final ImmutableSet.Builder<Property> ps = ImmutableSet.builder();
    for (final PropertyProvision pp : propertyProvisions) {
      ps.add(pp.getRequest());
    }
    return ps.build();
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestedAction, getPrecursorAction(), getPropertyProvisions());
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof ActionProvision
        && equals((ActionProvision) other);
  }

  private boolean equals(final ActionProvision other) {
    return Objects.equals(precursorAction, other.precursorAction)
        && Objects.equals(requestedAction, other.requestedAction)
        && Objects.equals(getPropertyProvisions(), other.getPropertyProvisions());
  }

  private static void assertWithoutPrecursor(final Action requestedAction, final ImmutableSet<PropertyProvision>
      provisions) {
    if (requestedAction.requiresPrecursor()) {
      throw new IllegalArgumentException("requested action requires a precursor");
    }

    if (provisions.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more property provisions");
    }

    final Set<Property> ps = requestedAction.getPreConditions().getFilledProperties();

    for (final PropertyProvision pp : provisions) {
      if (!ps.contains(pp.getRequest())) {
        throw new IllegalArgumentException(
            "expecting all property provisions to use a property required filled by requested action");
      }
    }
  }

  private static void assertWithPrecursor(final Action requestedAction, final Action precursorAction,
                                          final ImmutableSet<PropertyProvision> provisions) {
    if (!precursorAction.canBePrecursorOf(requestedAction)) {
      throw new IllegalArgumentException("expecting a satisfying precursor to the requested action");
    }

    final ImmutableSet<Property> required = requestedAction.getPreConditions().getFilledProperties();
    final ImmutableSet<Property> unsatisfied = requestedAction.getFilledPropertiesNotSatisfiedByPrecursor
        (precursorAction);

    if (unsatisfied.size() == required.size()) {
      // the precursor does not satisfy any property required filled
      if (!requestedAction.requiresPrecursor()) {
        throw new IllegalArgumentException("expecting the precursor to be required by requested action");
      }
    }

    // check that each unsatisfied property is satisfied via a property provision
    for (final PropertyProvision pp : provisions) {
      if (!unsatisfied.contains(pp.getRequest())) {
        throw new IllegalArgumentException("expecting all property provisions to use a property required filled by " +
            "requested action and not already provided by precursor");
      }
    }
  }

  private static void assertProvisions(final ImmutableSet<PropertyProvision> provisions) {
    final Set<Property> ps = Sets.newHashSet();
    for (final PropertyProvision pp : provisions) {
      final Property p = pp.getRequest();
      if (ps.contains(p)) {
        throw new IllegalArgumentException(
            "expecting each requested property to be provided by only one property provision");
      }
      ps.add(p);
    }
  }

}
