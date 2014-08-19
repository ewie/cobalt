/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import org.testng.annotations.Test;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.planner.PlanningProblem;
import vsr.cobalt.service.planner.PlannerRequest;

import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.MashupMaker.aMinimalMashup;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PlannerRequestTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting some goal mashup")
    public void rejectNullAsGoalMashup() {
      new PlannerRequest(null, -1, 0);
    }

    @Test
    public void createPlanningProblem() {
      final Mashup m = make(aMinimalMashup());
      final PlannerRequest r = new PlannerRequest(m, 1, 2);
      assertEquals(r.getPlanningProblem(), new PlanningProblem(m, 1, 2));
    }

  }

}
