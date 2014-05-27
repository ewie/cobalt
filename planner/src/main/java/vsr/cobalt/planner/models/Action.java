package vsr.cobalt.planner.models;

import java.util.Objects;

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
   * The post-conditions.
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
   * @param postConditions      the post-conditions
   * @param publishedProperties the set of published properties
   * @param realizedTasks       the set of realized tasks
   */
  public Action(final Widget widget, final PropositionSet preConditions, final PropositionSet postConditions,
                final ImmutableSet<Property> publishedProperties, final ImmutableSet<Task> realizedTasks) {
    this.widget = widget;
    this.preConditions = preConditions;
    this.postConditions = postConditions;
    this.publishedProperties = publishedProperties;
    this.realizedTasks = realizedTasks;
  }

  /**
   * Create an action realizing no tasks.
   *
   * @param widget         the owning widget
   * @param preConditions  the pre-conditions
   * @param postConditions the post-conditions
   */
  public Action(final Widget widget, final PropositionSet preConditions, final PropositionSet postConditions,
                final ImmutableSet<Property> publishedProperties) {
    this(widget, preConditions, postConditions, publishedProperties, ImmutableSet.<Task>of());
  }

  /**
   * Create an action publishing no properties and realizing no tasks.
   *
   * @param widget         the owning widget
   * @param preConditions  the pre-conditions
   * @param postConditions the post-conditions
   */
  public Action(final Widget widget, final PropositionSet preConditions, final PropositionSet postConditions) {
    this(widget, preConditions, postConditions, ImmutableSet.<Property>of());
  }

  /**
   * Create an action without any pre- or post-conditions and no realized tasks.
   *
   * @param widget the owning widget
   */
  public Action(final Widget widget) {
    this(widget, PropositionSet.empty(), PropositionSet.empty());
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
  public PropositionSet getPostConditions() {
    return postConditions;
  }

  /**
   * @return a set of zero or more published properties
   */
  public ImmutableSet<Property> getPublishedProperties() {
    return publishedProperties;
  }

  /**
   * @return a set of zero or more realized tasks
   */
  public ImmutableSet<Task> getRealizedTasks() {
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
      final ImmutableSet<Property> ps = other.preConditions.getClearedProperties();
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
  public ImmutableSet<Property> getFilledPropertiesNotSatisfiedByPrecursor(final Action precursor) {
    if (!isSameWidget(precursor)) {
      throw new IllegalArgumentException("expecting an action of the same widget");
    }
    final ImmutableSet.Builder<Property> ps = ImmutableSet.builder();
    for (final Property p : preConditions.getFilledProperties()) {
      if (!precursor.postConditions.isFilled(p)) {
        ps.add(p);
      }
    }
    return ps.build();
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
