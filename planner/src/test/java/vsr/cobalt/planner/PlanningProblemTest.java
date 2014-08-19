/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Objects;

import org.testng.annotations.Test;
import vsr.cobalt.models.Mashup;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.MashupMaker.aMinimalMashup;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PlanningProblemTest {

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "expecting positive minimum depth")
  public void rejectNonPositiveMinDepth() {
    new PlanningProblem(null, 0, 0);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "expecting minimum depth to be less than or equal to maximum depth")
  public void rejectMinDepthGreaterThanMaxDepth() {
    new PlanningProblem(null, 2, 1);
  }

  @Test
  public void defaultMinDepth() {
    final PlanningProblem p = new PlanningProblem(null);
    assertEquals(p.getMinDepth(), 1);
  }

  @Test
  public void defaultMaxDepth() {
    final PlanningProblem p = new PlanningProblem(null);
    assertEquals(p.getMaxDepth(), Integer.MAX_VALUE);
  }

  @Test
  public static class Equality {

    @Test
    public void hashCodeFromMashupAndDepthRange() {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem p = new PlanningProblem(m, 1, 2);
      assertEquals(p.hashCode(), Objects.hash(m, 1, 2));
    }

    @Test
    public void notEqualToObjectsOfDifferentType() {
      final PlanningProblem p = new PlanningProblem(null, 1, 1);
      final Object x = new Object();
      assertNotEquals(p, x);
    }

    @Test
    public void notEqualWhenMashupDiffers() {
      final Mashup m1 = make(aMinimalMashup()
          .withFunctionality(aMinimalFunctionality()
              .withIdentifier("f1")));
      final Mashup m2 = make(aMinimalMashup()
          .withFunctionality(aMinimalFunctionality()
              .withIdentifier("f2")));

      final PlanningProblem p1 = new PlanningProblem(m1, 1, 1);
      final PlanningProblem p2 = new PlanningProblem(m2, 1, 1);

      assertNotEquals(p1, p2);
    }

    @Test
    public void notEqualWhenMinDepthDiffers() {
      final Mashup m = make(aMinimalMashup());

      final PlanningProblem p1 = new PlanningProblem(m, 1, 2);
      final PlanningProblem p2 = new PlanningProblem(m, 2, 2);

      assertNotEquals(p1, p2);
    }

    @Test
    public void notEqualWhenMaxDepthDiffers() {
      final Mashup m = make(aMinimalMashup());

      final PlanningProblem p1 = new PlanningProblem(m, 1, 1);
      final PlanningProblem p2 = new PlanningProblem(m, 1, 2);

      assertNotEquals(p1, p2);
    }

    @Test
    public void equalWhenMashupAndDepthRangeEqual() {
      final Mashup m = make(aMinimalMashup());

      final PlanningProblem p1 = new PlanningProblem(m, 1, 2);
      final PlanningProblem p2 = new PlanningProblem(m, 1, 2);

      assertEquals(p1, p2);
    }

  }

}
