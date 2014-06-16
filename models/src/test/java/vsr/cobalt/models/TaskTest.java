/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class TaskTest {

  @Test
  public void isAnIdentifiable() {
    assertSubClass(Task.class, Identifiable.class);
  }

  @Test
  public static class CanEqual {

    @Test
    public void returnTrueWhenCalledWitTask() {
      final Task t1 = make(aMinimalTask());
      final Task t2 = make(aMinimalTask());
      assertTrue(t1.canEqual(t2));
    }

    @Test
    public void returnFalseWhenCalledWithNonTask() {
      final Task t = make(aMinimalTask());
      final Object x = new Object();
      assertFalse(t.canEqual(x));
    }

  }

}
