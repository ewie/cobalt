/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;

/**
 * A graph level representing the initial required actions determined by functionality provisions.
 *
 * @author Erik Wienhold
 */
public final class InitialLevel implements Level {

  /**
   * A non-empty set of functionality provisions.
   */
  private final ImmutableSet<FunctionalityProvision> functionalityProvisions;

  /**
   * Create an initial level from a set of functionality provisions.
   *
   * @param functionalityProvisions a non-empty set of functionality provisions
   */
  public InitialLevel(final Set<FunctionalityProvision> functionalityProvisions) {
    if (functionalityProvisions.isEmpty()) {
      throw new IllegalArgumentException("expecting one or more functionality provisions");
    }
    this.functionalityProvisions = ImmutableSet.copyOf(functionalityProvisions);
  }

  /**
   * @return the set of functionality provisions
   */
  public Set<FunctionalityProvision> getFunctionalityProvisions() {
    return functionalityProvisions;
  }

  public Set<Functionality> getRequestedFunctionalities() {
    final Set<Functionality> fs = new HashSet<>();
    for (final FunctionalityProvision fp : functionalityProvisions) {
      fs.add(fp.getRequest());
    }
    return fs;
  }

  /**
   * Get all functionality provisions satisfying the requested functionality
   *
   * @param functionality the requested functionality
   *
   * @return a set of functionality provisions with the requested functionality
   */
  public Set<FunctionalityProvision> getFunctionalityProvisionsByRequestedFunctionality(final Functionality
                                                                                            functionality) {
    final Set<FunctionalityProvision> fps = new HashSet<>();
    for (final FunctionalityProvision fp : functionalityProvisions) {
      if (fp.getRequest().equals(functionality)) {
        fps.add(fp);
      }
    }
    return fps;
  }

  /**
   * @return the set of required actions provided by all functionality provisions
   */
  @Override
  public Set<Action> getRequiredActions() {
    final Set<Action> as = new HashSet<>();
    for (final FunctionalityProvision fp : functionalityProvisions) {
      as.add(fp.getProvidingAction());
    }
    return as;
  }

  @Override
  public int hashCode() {
    return functionalityProvisions.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof InitialLevel
        && equals((InitialLevel) other);
  }

  private boolean equals(final InitialLevel other) {
    return Objects.equals(functionalityProvisions, other.functionalityProvisions);
  }

}
