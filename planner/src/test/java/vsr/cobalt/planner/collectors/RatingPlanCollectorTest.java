/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors;

import org.testng.annotations.Test;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.rating.PlanRater;
import vsr.cobalt.planner.rating.RatedPlan;
import vsr.cobalt.planner.rating.Rating;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class RatingPlanCollectorTest {

  @Test
  public void extendsQueueingPlanCollector() {
    assertSubClass(RatingPlanCollector.class, QueueingPlanCollector.class);
  }

  @Test
  public void offerRatedPlan() {
    final Plan p = new Plan(make(aMinimalGraph()));

    final Rating r = new Rating(42);

    final PlanRater pr = mock(PlanRater.class);
    when(pr.rate(p)).thenReturn(r);

    final RatingPlanCollector rc = new RatingPlanCollector(pr);
    rc.collect(p);

    assertEquals(rc.peek(), new RatedPlan(p, r));
  }

  @Test
  public void ignorePlanWhenRaterReturnsNull() {
    final Plan p = new Plan(make(aMinimalGraph()));

    final PlanRater pr = mock(PlanRater.class);
    when(pr.rate(p)).thenReturn(null);

    final RatingPlanCollector rc = new RatingPlanCollector(pr);
    rc.collect(p);

    assertEmpty(rc);
  }

  @Test
  public void enqueuePlansAccordingToRating() {
    final Plan p1 = new Plan(make(aMinimalGraph()));
    final Plan p2 = new Plan(make(aMinimalGraph()));

    final Rating r1 = new Rating(2);
    final Rating r2 = new Rating(1);

    final PlanRater pr = mock(PlanRater.class);
    when(pr.rate(same(p1))).thenReturn(r1);
    when(pr.rate(same(p2))).thenReturn(r2);

    final RatingPlanCollector rc = new RatingPlanCollector(pr);
    rc.collect(p1);
    rc.collect(p2);

    assertEquals(rc.peek(), new RatedPlan(p2, r2));
  }

}
