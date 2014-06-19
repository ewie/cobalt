/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import org.testng.annotations.Test;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.service.planner.PlannerRequest;

import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class PlannerRequestTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting some goal mashup")
    public void rejectNullAsGoalMashup() {
      new PlannerRequest(null, -1, 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting positive minimum depth")
    public void rejectNonPositiveMinDepth() {
      final Mashup m = new Mashup(setOf(make(aMinimalTask())));
      new PlannerRequest(m, -1, 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting minimum depth to be less than or equal to maximum depth")
    public void rejectMinDepthGreaterThanMaxDepth() {
      final Mashup m = new Mashup(setOf(make(aMinimalTask())));
      new PlannerRequest(m, 2, 1);
    }

  }

}
