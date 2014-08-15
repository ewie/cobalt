/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.rating;

import java.util.Objects;

import vsr.cobalt.planner.Plan;

/**
 * Associates a plan with a rating.
 *
 * @author Erik Wienhold
 */
public final class RatedPlan implements Comparable<RatedPlan> {

  private final Plan plan;

  private final Rating rating;

  /**
   * @param plan   a plan
   * @param rating the plan's rating
   */
  public RatedPlan(final Plan plan, final Rating rating) {
    this.plan = plan;
    this.rating = rating;
  }

  /**
   * @return the plan
   */
  public Plan getPlan() {
    return plan;
  }

  /**
   * @return the plan's rating
   */
  public Rating getRating() {
    return rating;
  }

  @Override
  public int compareTo(final RatedPlan other) {
    return rating.compareTo(other.rating);
  }

  @Override
  public int hashCode() {
    return Objects.hash(plan, rating);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof RatedPlan
        && equals((RatedPlan) other);
  }

  private boolean equals(final RatedPlan other) {
    return plan.equals(other.plan)
        && rating.equals(other.rating);
  }

}
