/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class RealizedTaskTest {

  @Test
  public void extendsOffer() {
    assertSubClass(RealizedTask.class, Offer.class);
  }

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting task to be realized by given action")
    public void rejectPropertyNotPublishedByAction() {
      final Action a = make(aMinimalAction());
      final Task t = make(aMinimalTask());
      new RealizedTask(t, a);
    }

  }

  @Test
  public static class CanEqual {

    RealizedTask rt;

    @BeforeMethod
    public void setUp() {
      final Task t = make(aMinimalTask());
      final Action a = make(aMinimalAction().withTask(t));
      rt = new RealizedTask(t, a);
    }

    @Test
    public void returnTrueWhenCalledWithRealizedTask() {
      assertTrue(rt.canEqual(rt));
    }

    @Test
    public void returnFalseWhenCalledWithNonRealizedTask() {
      final Object x = new Object();
      assertFalse(rt.canEqual(x));
    }

  }

}
