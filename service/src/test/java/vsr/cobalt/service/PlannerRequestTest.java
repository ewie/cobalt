/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import org.testng.annotations.Test;
import vsr.cobalt.service.planner.PlannerRequest;

@Test
public class PlannerRequestTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting positive minimum depth")
    public void rejectNonPositiveMinDepth() {
      new PlannerRequest(null, -1, 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting minimum depth to be less than or equal to maximum depth")
    public void rejectMinDepthGreaterThanMaxDepth() {
      new PlannerRequest(null, 2, 1);
    }

  }

}
