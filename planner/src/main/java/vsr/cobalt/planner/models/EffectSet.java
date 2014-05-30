/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import static com.google.common.collect.Sets.difference;
import static java.util.Collections.disjoint;

/**
 * A set of effects specifying properties whose values will be cleared or filled.
 *
 * @author Erik Wienhold
 */
public final class EffectSet {

  private static final Set<Property> EMPTY_PROPERTIES = Collections.emptySet();

  private static final EffectSet EMPTY = new EffectSet(EMPTY_PROPERTIES, EMPTY_PROPERTIES);

  /**
   * The set of properties to clear.
   */
  private final ImmutableSet<Property> toClear;

  /**
   * The set of properties to fill.
   */
  private final ImmutableSet<Property> toFill;

  /**
   * Create an effect set with two disjoint set of properties, one providing properties to be cleared, the other
   * providing properties to be filled.
   *
   * @param toClear a set of properties to clear
   * @param toFill  a set of properties to fill
   */
  public EffectSet(final Set<Property> toClear, final Set<Property> toFill) {
    if (!disjoint(toClear, toFill)) {
      throw new IllegalArgumentException("expecting properties to clear and fill to be disjoint sets");
    }
    this.toClear = ImmutableSet.copyOf(toClear);
    this.toFill = ImmutableSet.copyOf(toFill);
  }

  /**
   * @return the empty effect set
   */
  public static EffectSet empty() {
    return EMPTY;
  }

  /**
   * @param properties a set of properties to clear
   *
   * @return an effect set with only properties to clear
   */
  public static EffectSet clear(final Set<Property> properties) {
    return new EffectSet(properties, EMPTY_PROPERTIES);
  }

  /**
   * @see #clear(Set)
   */
  public static EffectSet clear(final Property... properties) {
    return clear(ImmutableSet.of(properties));
  }

  /**
   * @param properties a set of properties to fill
   *
   * @return an effect set with only properties to fill
   */
  public static EffectSet fill(final Set<Property> properties) {
    return new EffectSet(EMPTY_PROPERTIES, properties);
  }

  /**
   * @see #fill(Set)
   */
  public static EffectSet fill(final Property... properties) {
    return fill(ImmutableSet.of(properties));
  }

  /**
   * @return the set of properties to clear
   */
  public Set<Property> getPropertiesToClear() {
    return toClear;
  }

  /**
   * @return the set of properties to fill
   */
  public Set<Property> getPropertiesToFill() {
    return toFill;
  }

  /**
   * Create the post-conditions by applying this effect set on the given pre-conditions.
   *
   * @param preConditions the pre-conditions
   *
   * @return a proposition set specifying the derived post-conditions
   */
  public PropositionSet createPostConditions(final PropositionSet preConditions) {
    final Set<Property> cleared = new HashSet<>();
    final Set<Property> filled = new HashSet<>();
    cleared.addAll(toClear);
    filled.addAll(toFill);
    cleared.addAll(difference(preConditions.getClearedProperties(), toFill));
    filled.addAll(difference(preConditions.getFilledProperties(), toClear));
    return new PropositionSet(cleared, filled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(toClear, toFill);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof EffectSet
        && equals((EffectSet) other);
  }

  private boolean equals(final EffectSet other) {
    return Objects.equals(toClear, other.toClear)
        && Objects.equals(toFill, other.toFill);
  }

}
