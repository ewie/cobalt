/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Actions are a widget's central components and describe the functionality realized by a widget.
 *
 * @author Erik Wienhold
 */
public abstract class Action {

  /**
   * The widget the action belongs to.
   */
  private final Widget widget;

  /**
   * The pre-conditions.
   */
  private final PropositionSet preConditions;

  /**
   * The effects.
   */
  private final PropositionSet effects;

  /**
   * The post-conditions resulting from the pre-conditions and effects.
   */
  private final PropositionSet postConditions;

  /**
   * A set of properties published after successful execution of this action.
   */
  private final ImmutableSet<Property> publishedProperties;

  /**
   * A set of tasks realized by this action.
   */
  private final ImmutableSet<Task> realizedTasks;

  /**
   * A set of tasks realized by this action.
   */
  private final ImmutableSet<Interaction> interactions;

  /**
   * Create a new action.
   *
   * @param widget              the owning widget
   * @param preConditions       the pre-conditions
   * @param effects             the effects
   * @param publishedProperties a set of published properties
   * @param realizedTasks       a set of realized tasks
   * @param interactions        a set of user interactions
   */
  private Action(final Widget widget, final PropositionSet preConditions, final PropositionSet effects,
                 final Set<Property> publishedProperties, final Set<Task> realizedTasks,
                 final Set<Interaction> interactions) {
    this.widget = widget;
    this.preConditions = preConditions;
    this.effects = effects;
    this.publishedProperties = ImmutableSet.copyOf(publishedProperties);
    this.realizedTasks = ImmutableSet.copyOf(realizedTasks);
    this.interactions = ImmutableSet.copyOf(interactions);
    postConditions = effects.createPostConditions(preConditions);
  }

  /**
   * Create a new action.
   *
   * @param widget              the owning widget
   * @param preConditions       the pre-conditions
   * @param effects             the effects
   * @param publishedProperties a set of published properties
   * @param realizedTasks       a set of realized tasks
   * @param interactions        a set of user interactions
   */
  public static Action create(final Widget widget, final PropositionSet preConditions, final PropositionSet effects,
                              final Set<Property> publishedProperties, final Set<Task> realizedTasks,
                              final Set<Interaction> interactions) {
    return new AtomicAction(widget, preConditions, effects, publishedProperties, realizedTasks, interactions);
  }

  /**
   * Create an atomic action without any user interactions.
   *
   * @param widget              the owning widget
   * @param preConditions       the pre-conditions
   * @param effects             the effects
   * @param publishedProperties a set of published properties
   * @param realizedTasks       a set of realized tasks
   */
  public static Action create(final Widget widget, final PropositionSet preConditions, final PropositionSet effects,
                              final Set<Property> publishedProperties, final Set<Task> realizedTasks) {
    return create(widget, preConditions, effects, publishedProperties, realizedTasks, ImmutableSet.<Interaction>of());
  }

  /**
   * Create an atomic action realizing no tasks and having no user interactions.
   *
   * @param widget              the owning widget
   * @param preConditions       the pre-conditions
   * @param effects             the effects
   * @param publishedProperties a set of published properties
   */
  public static Action create(final Widget widget, final PropositionSet preConditions, final PropositionSet effects,
                              final Set<Property> publishedProperties) {
    return create(widget, preConditions, effects, publishedProperties, ImmutableSet.<Task>of());
  }

  /**
   * Create an atomic action publishing no properties, realizing no tasks and having no user interactions.
   *
   * @param widget        the owning widget
   * @param preConditions the pre-conditions
   * @param effects       the effects
   */
  public static Action create(final Widget widget, final PropositionSet preConditions, final PropositionSet effects) {
    return create(widget, preConditions, effects, ImmutableSet.<Property>of());
  }

  /**
   * Create an atomic action without any effects, publishing no properties, realizing no tasks and having no user
   * interactions.
   *
   * @param widget        the owning widget
   * @param preConditions the pre-conditions
   */
  public static Action create(final Widget widget, final PropositionSet preConditions) {
    return create(widget, preConditions, PropositionSet.empty());
  }

  /**
   * Create an atomic action without any pre-conditions, effects, realized tasks or user interactions.
   *
   * @param widget the owning widget
   */
  public static Action create(final Widget widget) {
    return create(widget, PropositionSet.empty());
  }

  /**
   * Compose an action from a collection of actions. When called with more than one action the resulting action is the
   * composition of all actions. With just one action the result is the provided action itself.
   *
   * @param actions a collection of actions
   *
   * @return an action representing the given actions
   */
  public static Action compose(final Collection<Action> actions) {
    if (actions.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more actions");
    }
    if (actions.size() == 1) {
      return Iterables.get(actions, 0);
    } else {
      return CompositeAction.create(actions);
    }
  }

  /**
   * Equivalent to {@link #compose(Collection)}.
   *
   * @param action      a required action
   * @param moreActions zero or more additional actions
   */
  public static Action compose(final Action action, final Action... moreActions) {
    final List<Action> actions = new ArrayList<>(1 + moreActions.length);
    actions.add(action);
    Collections.addAll(actions, moreActions);
    return compose(actions);
  }

  /**
   * Tests if a collection of actions can be composed into a single action.
   * <p/>
   * Actions can be composed when they belong to the same widget and pair of actions is mutually exclusive.
   * Two distinct actions A and B are mutually exclusive when they have
   * <ul>
   * <li>competing needs, i.e. a pre-condition of A is mutex with a pre-condition of B</li>
   * <li>inconsistent effects, i.e. a post-condition of A is mutex with a post-condition of B</li>
   * <li>interference, i.e. a post-condition of A is mutex with a pre-condition of B or vice versa</li>
   * </ul>
   *
   * @param actions a collection of actions
   *
   * @return true when composable, false otherwise
   */
  public static boolean isComposable(final Collection<Action> actions) {
    if (actions.isEmpty()) {
      return false;
    }
    final Action a1 = Iterables.get(actions, 0);
    for (final Action x : actions) {
      if (!x.belongsToSameWidget(a1)) {
        return false;
      }
      for (final Action y : actions) {
        if (!x.equals(y) && isMutex1(x, y)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Test if this actions represents a given action, i.e. it realizes the same interface as the given action.
   *
   * @param action an action
   *
   * @return true when this action represents the other action, false otherwise
   */
  public boolean represents(final Action action) {
    return equals(action);
  }

  /**
   * @return the widget this action belongs to
   */
  public Widget getWidget() {
    return widget;
  }

  /**
   * @return the pre-conditions
   */
  public PropositionSet getPreConditions() {
    return preConditions;
  }

  /**
   * @return the post-conditions
   */
  public PropositionSet getEffects() {
    return effects;
  }

  /**
   * @return the post-conditions
   */
  public PropositionSet getPostConditions() {
    return postConditions;
  }

  /**
   * @return a set of zero or more published properties
   */
  public Set<Property> getPublishedProperties() {
    return publishedProperties;
  }

  /**
   * @return a set of zero or more realized tasks
   */
  public Set<Task> getRealizedTasks() {
    return realizedTasks;
  }

  /**
   * @return a set of zero or more interactions
   */
  public Set<Interaction> getInteractions() {
    return interactions;
  }

  /**
   * Check if this action is enabled by itself, i.e. it has no pre-conditions.
   *
   * @return true when enabled, false otherwise
   */
  public boolean isEnabled() {
    return preConditions.isEmpty();
  }

  /**
   * Check if this action is a maintenance actions, i.e. it has no effects, published properties or realized tasks.
   *
   * @return true when maintenance action, false otherwise
   */
  public boolean isMaintenance() {
    return publishedProperties.isEmpty()
        && realizedTasks.isEmpty()
        && interactions.isEmpty()
        && preConditions.equals(postConditions);
  }

  /**
   * Check if this action published a given property.
   *
   * @param property the property to check
   *
   * @return true when the given property gets published, false otherwise
   */
  public boolean publishes(final Property property) {
    return publishedProperties.contains(property);
  }

  /**
   * Check if this action realizes a given task.
   *
   * @param task the task to check
   *
   * @return true when the given task is realized, false otherwise
   */
  public boolean realizes(final Task task) {
    return realizedTasks.contains(task);
  }

  /**
   * Check if this action can be the precursor of the given action, i.e. the precursor, when executed, satisfies all
   * pre-conditions of the other action, which can only be satisfied from within the same widget instance.
   *
   * @param other an action of the same widget which should be executed immediately after this action
   *
   * @return true when this action can be the precursor, false otherwise
   */
  public boolean canBePrecursorOf(final Action other) {
    if (belongsToSameWidget(other)) {
      final Set<Property> ps = other.preConditions.getClearedProperties();
      for (final Property p : ps) {
        if (!postConditions.isCleared(p)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Check if this action requires a precursor according to {@link #canBePrecursorOf(Action)}.
   *
   * @return true when a precursor is required, false otherwise
   */
  public boolean requiresPrecursor() {
    return !preConditions.getClearedProperties().isEmpty();
  }

  /**
   * Get all properties requiring a value for which the given action cannot provide a value.
   *
   * @param precursor an action to provide property values
   *
   * @return a set of properties whose values cannot be provided
   */
  public Set<Property> getFilledPropertiesNotSatisfiedByPrecursor(final Action precursor) {
    if (!belongsToSameWidget(precursor)) {
      throw new IllegalArgumentException("expecting an action of the same widget");
    }
    final Set<Property> ps = new HashSet<>();
    for (final Property p : preConditions.getFilledProperties()) {
      if (!precursor.postConditions.isFilled(p)) {
        ps.add(p);
      }
    }
    return ps;
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Action
        && equals((Action) other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(widget, preConditions, postConditions, publishedProperties, realizedTasks, interactions);
  }

  private boolean equals(final Action other) {
    return Objects.equals(widget, other.widget)
        && Objects.equals(preConditions, other.preConditions)
        && Objects.equals(postConditions, other.postConditions)
        && Objects.equals(publishedProperties, other.publishedProperties)
        && Objects.equals(realizedTasks, other.realizedTasks)
        && Objects.equals(interactions, other.interactions);
  }

  private boolean belongsToSameWidget(final Action other) {
    return widget.equals(other.widget);
  }

  /**
   * Tests one "half" of a possible mutex relation between two actions assuming they can be mutex.
   * <p/>
   * When the method returns true the actions are mutex. When the method returns false the actions are not guaranteed
   * to
   * be non-mutex. In the latter case check again but with the two actions swapped.
   *
   * @param x an request
   * @param y another request
   *
   * @return true
   */
  private static boolean isMutex1(final Action x, final Action y) {
    return haveCompetingNeeds1(x, y)
        || haveInconsistentEffect1(x, y)
        || haveInterference1(x, y);

  }

  private static boolean haveCompetingNeeds1(final Action x, final Action y) {
    return !Collections.disjoint(
        x.getPreConditions().getClearedProperties(),
        y.getPreConditions().getFilledProperties());
  }

  private static boolean haveInconsistentEffect1(final Action x, final Action y) {
    return !Collections.disjoint(
        x.getPostConditions().getClearedProperties(),
        y.getPostConditions().getFilledProperties());
  }

  private static boolean haveInterference1(final Action x, final Action y) {
    return !Collections.disjoint(
        x.getPreConditions().getClearedProperties(),
        y.getPostConditions().getFilledProperties());
  }

  private static class AtomicAction extends Action {

    private AtomicAction(final Widget widget, final PropositionSet preConditions, final PropositionSet effects,
                         final Set<Property> publishedProperties, final Set<Task> realizedTasks,
                         final Set<Interaction> interactions) {
      super(widget, preConditions, effects, publishedProperties, realizedTasks, interactions);
    }

  }

  private static class CompositeAction extends Action {

    /**
     * The actions constituting this composite actions.
     */
    private final ImmutableSet<Action> actions;

    private CompositeAction(final Builder builder) {
      super(builder.getWidget(),
          builder.getPreConditions(),
          builder.getEffects(),
          builder.getPublishedProperties(),
          builder.getRealizedTasks(),
          builder.getInteractions());
      actions = builder.getActions();
    }

    public static CompositeAction create(final Collection<Action> actions) {
      if (actions.size() < 2) {
        throw new IllegalArgumentException("expecting two or more actions");
      }
      if (!isComposable(actions)) {
        throw new IllegalArgumentException("expecting composable actions");
      }
      final Builder b = new Builder();
      for (final Action a : actions) {
        b.add(a);
      }
      return new CompositeAction(b);
    }

    @Override
    public boolean represents(final Action action) {
      if (super.represents(action)) {
        return true;
      }
      for (final Action a : actions) {
        if (a.represents(action)) {
          return true;
        }
      }
      return false;
    }

    private static class Builder {

      private final ImmutableSet.Builder<Action> actions = ImmutableSet.builder();

      private final ImmutableSet.Builder<Property> cleared = ImmutableSet.builder();

      private final ImmutableSet.Builder<Property> filled = ImmutableSet.builder();

      private final ImmutableSet.Builder<Property> toClear = ImmutableSet.builder();

      private final ImmutableSet.Builder<Property> toFill = ImmutableSet.builder();

      private final ImmutableSet.Builder<Property> properties = ImmutableSet.builder();

      private final ImmutableSet.Builder<Task> tasks = ImmutableSet.builder();

      private final ImmutableSet.Builder<Interaction> interactions = ImmutableSet.builder();

      private Widget widget;

      public void add(final Action action) {
        if (widget == null) {
          widget = action.getWidget();
        }

        actions.add(action);

        properties.addAll(action.getPublishedProperties());
        tasks.addAll(action.getRealizedTasks());
        interactions.addAll(action.getInteractions());
        addPreConditions(action.getPreConditions());
        addEffects(action.getEffects());
      }

      public ImmutableSet<Action> getActions() {
        return actions.build();
      }

      public Widget getWidget() {
        return widget;
      }

      public PropositionSet getPreConditions() {
        return new PropositionSet(cleared.build(), filled.build());
      }

      public PropositionSet getEffects() {
        return new PropositionSet(toClear.build(), toFill.build());
      }

      public ImmutableSet<Property> getPublishedProperties() {
        return properties.build();
      }

      public ImmutableSet<Task> getRealizedTasks() {
        return tasks.build();
      }

      public ImmutableSet<Interaction> getInteractions() {
        return interactions.build();
      }

      private void addPreConditions(final PropositionSet pre) {
        cleared.addAll(pre.getClearedProperties());
        filled.addAll(pre.getFilledProperties());
      }

      private void addEffects(final PropositionSet effects) {
        toClear.addAll(effects.getClearedProperties());
        toFill.addAll(effects.getFilledProperties());
      }

    }

  }

}
