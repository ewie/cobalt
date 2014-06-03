/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import static java.util.Collections.disjoint;

/**
 * A set of propositions specifying the absence or presence of values for specific properties.
 *
 * @author Erik Wienhold
 */
public final class PropositionSet {

  private static final Set<Property> EMPTY_PROPERTIES = Collections.emptySet();

  private static final PropositionSet EMPTY = new PropositionSet(EMPTY_PROPERTIES, EMPTY_PROPERTIES);

  /**
   * The set of properties without a value.
   */
  private final ImmutableSet<Property> cleared;

  /**
   * The set of properties with some value.
   */
  private final ImmutableSet<Property> filled;

  /**
   * Create a proposition set with two disjoint set of properties, specifying either the absence or the presence of
   * each
   * property's value.
   *
   * @param cleared a set of properties with no value
   * @param filled  a set of properties with some value
   */
  public PropositionSet(final Set<Property> cleared, final Set<Property> filled) {
    if (!disjoint(cleared, filled)) {
      throw new IllegalArgumentException("expecting cleared and filled properties to be disjoint sets");
    }
    this.cleared = ImmutableSet.copyOf(cleared);
    this.filled = ImmutableSet.copyOf(filled);
  }

  /**
   * @return the empty proposition set
   */
  public static PropositionSet empty() {
    return EMPTY;
  }

  /**
   * Create a proposition set with only cleared properties.
   *
   * @param properties a set of properties
   *
   * @return a proposition set with only cleared properties
   */
  public static PropositionSet cleared(final Set<Property> properties) {
    return new PropositionSet(properties, EMPTY_PROPERTIES);
  }

  /**
   * @see #cleared(Set)
   */
  public static PropositionSet cleared(final Property... properties) {
    return cleared(ImmutableSet.copyOf(properties));
  }

  /**
   * Create a proposition set with only filled properties.
   *
   * @param properties a set of properties
   *
   * @return a proposition set with only filled properties
   */
  public static PropositionSet filled(final Set<Property> properties) {
    return new PropositionSet(EMPTY_PROPERTIES, properties);
  }

  /**
   * @see #filled(Set)
   */
  public static PropositionSet filled(final Property... properties) {
    return filled(ImmutableSet.copyOf(properties));
  }

  /**
   * Check if a property is specified as cleared.
   *
   * @param property the property to check
   *
   * @return true when the property has no value, false otherwise
   */
  public boolean isCleared(final Property property) {
    return cleared.contains(property);
  }

  /**
   * Check if a property is specified as filled.
   *
   * @param property the property to check
   *
   * @return true when the property has some value, false otherwise
   */
  public boolean isFilled(final Property property) {
    return filled.contains(property);
  }

  /**
   * @return the set of cleared properties
   */
  public Set<Property> getClearedProperties() {
    return cleared;
  }

  /**
   * @return the set of filled properties
   */
  public Set<Property> getFilledProperties() {
    return filled;
  }

  /**
   * @return true when no properties are specified
   */
  public boolean isEmpty() {
    return cleared.isEmpty() && filled.isEmpty();
  }

  @Override
  public int hashCode() {
    return Objects.hash(cleared, filled);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof PropositionSet
        && equals((PropositionSet) other);
  }

  private boolean equals(final PropositionSet other) {
    return Objects.equals(cleared, other.cleared)
        && Objects.equals(filled, other.filled);
  }

}
