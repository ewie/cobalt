package vsr.cobalt.planner.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Actions are a widget's central components and describe the functionality realized by a widget.
 *
 * @author Erik Wienhold
 */
public final class Action {

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
  private final EffectSet effects;

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
   * Create a new action.
   *
   * @param widget              the owning widget
   * @param preConditions       the pre-conditions
   * @param effects             the effects
   * @param publishedProperties a set of published properties
   * @param realizedTasks       a set of realized tasks
   */
  public Action(final Widget widget, final PropositionSet preConditions, final EffectSet effects,
                final Set<Property> publishedProperties, final Set<Task> realizedTasks) {
    this.widget = widget;
    this.preConditions = preConditions;
    this.effects = effects;
    this.publishedProperties = ImmutableSet.copyOf(publishedProperties);
    this.realizedTasks = ImmutableSet.copyOf(realizedTasks);
    postConditions = effects.createPostConditions(preConditions);
  }

  /**
   * Create an action realizing no tasks.
   *
   * @param widget        the owning widget
   * @param preConditions the pre-conditions
   * @param effects       the effects
   */
  public Action(final Widget widget, final PropositionSet preConditions, final EffectSet effects,
                final Set<Property> publishedProperties) {
    this(widget, preConditions, effects, publishedProperties, Collections.<Task>emptySet());
  }

  /**
   * Create an action publishing no properties and realizing no tasks.
   *
   * @param widget        the owning widget
   * @param preConditions the pre-conditions
   * @param effects       the effects
   */
  public Action(final Widget widget, final PropositionSet preConditions, final EffectSet effects) {
    this(widget, preConditions, effects, Collections.<Property>emptySet());
  }

  /**
   * Create an action publishing no properties and realizing no tasks.
   *
   * @param widget        the owning widget
   * @param preConditions the pre-conditions
   */
  public Action(final Widget widget, final PropositionSet preConditions) {
    this(widget, preConditions, EffectSet.empty());
  }

  /**
   * Create an action without any pre-conditions, effects or realized tasks.
   *
   * @param widget the owning widget
   */
  public Action(final Widget widget) {
    this(widget, PropositionSet.empty());
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
  public EffectSet getEffects() {
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
   * Check if this action is enabled by itself, i.e. it has no pre-conditions.
   *
   * @return true when enabled, false otherwise
   */
  public boolean isEnabled() {
    return preConditions.isEmpty();
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
    if (isSameWidget(other)) {
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
    if (!isSameWidget(precursor)) {
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
    return Objects.hash(widget, preConditions, postConditions, publishedProperties, realizedTasks);
  }

  private boolean equals(final Action other) {
    return Objects.equals(widget, other.widget)
        && Objects.equals(preConditions, other.preConditions)
        && Objects.equals(postConditions, other.postConditions)
        && Objects.equals(publishedProperties, other.publishedProperties)
        && Objects.equals(realizedTasks, other.realizedTasks);
  }

  private boolean isSameWidget(final Action other) {
    return widget.equals(other.widget);
  }

}
