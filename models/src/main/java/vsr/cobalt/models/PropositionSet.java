/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A set of propositions specifying the absence or presence of values for certain properties. Ensures that a
 * proposition cannot be used along with its negation, otherwise we would have zombie cats.
 *
 * @author Erik Wienhold
 */
public final class PropositionSet extends AbstractSet<Proposition> {

  private static final Set<Proposition> EMPTY_PROPOSITIONS = Collections.emptySet();

  private static final Set<Property> EMPTY_PROPERTIES = Collections.emptySet();

  private static final PropositionSet EMPTY = new PropositionSet(EMPTY_PROPOSITIONS);

  private final ImmutableSet<Proposition> propositions;

  /**
   * Create a proposition set from a set of propositions.
   *
   * @param propositions a set of propositions
   */
  public PropositionSet(final Set<Proposition> propositions) {
    if (anyMutexPropositions(propositions)) {
      throw new IllegalArgumentException("expecting no proposition to also appear negated");
    }
    this.propositions = ImmutableSet.copyOf(propositions);
  }

  /**
   * Create a proposition set from two sets of cleared and filled properties.
   *
   * @param cleared a set of cleared properties
   * @param filled  a set of filled properties
   */
  public PropositionSet(final Set<Property> cleared, final Set<Property> filled) {
    this(Sets.union(createPropositions(cleared, Proposition.CLEARED_VALUE), createPropositions(filled, Proposition.FILLED_VALUE)));
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
    return propositions.contains(Proposition.cleared(property));
  }

  /**
   * Check if a property is specified as filled.
   *
   * @param property the property to check
   *
   * @return true when the property has some value, false otherwise
   */
  public boolean isFilled(final Property property) {
    return propositions.contains(Proposition.filled(property));
  }

  /**
   * @return the propositions
   */
  public Set<Proposition> getPropositions() {
    return propositions;
  }

  /**
   * @return the set of cleared properties
   */
  public Set<Property> getClearedProperties() {
    final Set<Property> cleared = new HashSet<>();
    for (final Proposition p : propositions) {
      if (!p.isFilled()) {
        cleared.add(p.getProperty());
      }
    }
    return cleared;
  }

  /**
   * @return the set of filled properties
   */
  public Set<Property> getFilledProperties() {
    final Set<Property> filled = new HashSet<>();
    for (final Proposition p : propositions) {
      if (p.isFilled()) {
        filled.add(p.getProperty());
      }
    }
    return filled;
  }

  /**
   * Create the post-conditions by applying this proposition set as effects on the given pre-conditions.
   *
   * @param preConditions the pre-conditions
   *
   * @return a proposition set specifying the derived post-conditions
   */
  public PropositionSet createPostConditions(final PropositionSet preConditions) {
    final Set<Proposition> postConditions = Sets.newHashSet(propositions);
    for (final Proposition p : preConditions.propositions) {
      if (!postConditions.contains(p) && !postConditions.contains(p.negation())) {
        postConditions.add(p);
      }
    }
    return new PropositionSet(postConditions);
  }

  @Override
  public int size() {
    return propositions.size();
  }

  @Override
  public Iterator<Proposition> iterator() {
    return propositions.iterator();
  }

  @Override
  public int hashCode() {
    return propositions.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof PropositionSet
        && equals((PropositionSet) other);
  }

  private boolean equals(final PropositionSet other) {
    return Objects.equals(propositions, other.propositions);
  }

  private static Set<Proposition> createPropositions(final Set<Property> properties, final boolean valueState) {
    final Set<Proposition> propositions = new HashSet<>(properties.size());
    for (final Property p : properties) {
      propositions.add(new Proposition(p, valueState));
    }
    return propositions;
  }

  private static boolean anyMutexPropositions(final Set<Proposition> propositions) {
    for (final Proposition p : propositions) {
      if (propositions.contains(p.negation())) {
        return true;
      }
    }
    return false;
  }

}
