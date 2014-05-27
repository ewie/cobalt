/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.makers.TaskMaker.aTask;

@Test
public class TaskTest {

  @Test
  public static class GetIdentifier {

    @Test
    public void returnIdentifier() {
      final String id = "foo";
      final Task t = aTask().withIdentifier(id).make();
      assertEquals(t.getIdentifier(), id);
    }

  }

  @Test
  public static class HashCode {

    @Test
    public void useIdentifierHashCode() {
      final Task t = aTask().withIdentifier("bar").make();
      assertEquals(t.hashCode(), t.getIdentifier().hashCode());
    }

  }

  @Test
  public static class Equals {

    @Test
    public void returnTrueWhenEqual() {
      final Task t1 = aMinimalTask().make();
      final Task t2 = aMinimalTask().make();
      assertEquals(t1, t2);
    }

    @Test
    public void returnFalseWhenIdentifierDiffers() {
      final Task t1 = aTask().withIdentifier("x").make();
      final Task t2 = aTask().withIdentifier("y").make();
      assertNotEquals(t1, t2);
    }

  }

}
