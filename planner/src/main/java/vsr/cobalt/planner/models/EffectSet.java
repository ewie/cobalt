/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import static com.google.common.collect.Sets.difference;

/**
 * A set of effects specifying properties whose values will be cleared or filled.
 *
 * @author Erik Wienhold
 */
public final class EffectSet {

  private static final EffectSet EMPTY = new EffectSet(ImmutableSet.<Property>of(), ImmutableSet.<Property>of());

  /**
   * The set of properties to clear.
   */
  private final ImmutableSet<Property> toClear;

  /**
   * The set of properties to fill.
   */
  private final ImmutableSet<Property> toFill;

  /**
   * Create an effect set with two disjoint set of properties, one providing properties to be cleared,
   * the other providing properties to be filled.
   *
   * @param toClear a set of properties to clear
   * @param toFill  a set of properties to fill
   */
  public EffectSet(final ImmutableSet<Property> toClear, final ImmutableSet<Property> toFill) {
    if (!Sets.intersection(toClear, toFill).isEmpty()) {
      throw new IllegalArgumentException("expecting properties to clear and fill to be disjoint sets");
    }
    this.toClear = toClear;
    this.toFill = toFill;
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
  public static EffectSet clear(final ImmutableSet<Property> properties) {
    return new EffectSet(properties, ImmutableSet.<Property>of());
  }

  /**
   * @see #clear(ImmutableSet)
   */
  public static EffectSet clear(final Property... properties) {
    return clear(ImmutableSet.of(properties));
  }

  /**
   * @param properties a set of properties to fill
   *
   * @return an effect set with only properties to fill
   */
  public static EffectSet fill(final ImmutableSet<Property> properties) {
    return new EffectSet(ImmutableSet.<Property>of(), properties);
  }

  /**
   * @see #fill(ImmutableSet)
   */
  public static EffectSet fill(final Property... properties) {
    return fill(ImmutableSet.of(properties));
  }

  /**
   * @return the set of properties to clear
   */
  public ImmutableSet<Property> getPropertiesToClear() {
    return toClear;
  }

  /**
   * @return the set of properties to fill
   */
  public ImmutableSet<Property> getPropertiesToFill() {
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
    final ImmutableSet.Builder<Property> cleared = ImmutableSet.builder();
    final ImmutableSet.Builder<Property> filled = ImmutableSet.builder();
    cleared.addAll(toClear);
    filled.addAll(toFill);
    cleared.addAll(difference(preConditions.getClearedProperties(), toFill));
    filled.addAll(difference(preConditions.getFilledProperties(), toClear));
    return new PropositionSet(cleared.build(), filled.build());
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
