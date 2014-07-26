/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors.rating;

import java.util.Objects;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.graph.Graph;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class RatedPlanTest {

  @Test
  public void compareByRating() {
    final Plan p = new Plan(make(aMinimalGraph()));
    final Rating r1 = new Rating(1);
    final Rating r2 = new Rating(2);
    final RatedPlan rp1 = new RatedPlan(p, r1);
    final RatedPlan rp2 = new RatedPlan(p, r2);
    assertEquals(rp1.compareTo(rp2), r1.compareTo(r2));
  }

  @Test
  public void calculateHashCodeFromPlanAndRating() {
    final Plan p = new Plan(make(aMinimalGraph()));
    final Rating r = new Rating(1);
    final RatedPlan rp = new RatedPlan(p, r);
    assertEquals(rp.hashCode(), Objects.hash(p, r));
  }

  @Test
  public void equalsWhenPlansAndRatingsEqual() {
    final Plan p1 = new Plan(make(aMinimalGraph()));
    final Plan p2 = new Plan(make(aMinimalGraph()));
    final Rating r1 = new Rating(1);
    final Rating r2 = new Rating(1);
    final RatedPlan rp1 = new RatedPlan(p1, r1);
    final RatedPlan rp2 = new RatedPlan(p2, r2);
    assertEquals(rp1, rp2);
  }

  @Test
  public void notEqualsWhenPlansDiffer() {
    final Plan p1 = planWithFunctionality("f1");
    final Plan p2 = planWithFunctionality("f2");
    final Rating r1 = new Rating(1);
    final Rating r2 = new Rating(1);
    final RatedPlan rp1 = new RatedPlan(p1, r1);
    final RatedPlan rp2 = new RatedPlan(p2, r2);
    assertNotEquals(rp1, rp2);
  }

  @Test
  public void notEqualsWhenRatingsDiffer() {
    final Plan p = new Plan(make(aMinimalGraph()));
    final Rating r1 = new Rating(1);
    final Rating r2 = new Rating(2);
    final RatedPlan rp1 = new RatedPlan(p, r1);
    final RatedPlan rp2 = new RatedPlan(p, r2);
    assertNotEquals(rp1, rp2);
  }

  @Test
  public void notEqualsToObjectOfDifferentClass() {
    final Plan p = new Plan(make(aMinimalGraph()));
    final Rating r = new Rating(1);
    final RatedPlan rp = new RatedPlan(p, r);
    final Object x = new Object();
    assertNotEquals(rp, x);
  }

  private static Plan planWithFunctionality(final String identifier) {
    final Functionality f = make(aMinimalFunctionality().withIdentifier(identifier));
    final Action a = make(aMinimalAction().withFunctionality(f));
    final Graph g = make(aMinimalGraph()
        .withInitialLevel(anInitialLevel()
            .withProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a))));
    return new Plan(g);
  }

}
