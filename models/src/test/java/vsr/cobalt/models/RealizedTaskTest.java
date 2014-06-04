/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class RealizedTaskTest {

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
  public static class Getters {

    private Task t;

    private Action a;

    private RealizedTask at;

    @BeforeMethod
    public void setUp() {
      t = make(aMinimalTask());
      a = make(aMinimalAction().withTask(t));
      at = new RealizedTask(t, a);
    }

    @Test
    public void getProperty() {
      assertSame(at.getTask(), t);
    }

    @Test
    public void getAction() {
      assertSame(at.getAction(), a);
    }

  }

  @Test
  public static class Equality {

    private Action a;

    private Task t;

    @BeforeMethod
    public void setUp() {
      t = make(aMinimalTask());
      a = make(aMinimalAction().withTask(t));
    }

    @Test
    public void generateHashCodeFromTaskAndAction() {
      final RealizedTask at = new RealizedTask(t, a);
      assertEquals(at.hashCode(), Objects.hash(t, a));
    }

    @Test
    public void notEqualToObjectOfDifferentClass() {
      final RealizedTask at = new RealizedTask(t, a);
      final Object x = new Object();
      assertNotEquals(at, x);
    }

    @Test
    public void notEqualWhenTaskDiffers() {
      final Task t2 = make(aMinimalTask().withIdentifier("t2"));
      final Action a2 = make(aMinimalAction().withTask(t2));
      final RealizedTask at1 = new RealizedTask(t, a);
      final RealizedTask at2 = new RealizedTask(t2, a2);
      assertNotEquals(at1, at2);
    }

    @Test
    public void notEqualWhenActionDiffers() {
      final Task t2 = make(aMinimalTask().withIdentifier("t2"));
      final Action a2 = make(aMinimalAction().withTask(t, t2));
      final RealizedTask at1 = new RealizedTask(t, a);
      final RealizedTask at2 = new RealizedTask(t, a2);
      assertNotEquals(at1, at2);
    }

    @Test
    public void equalWhenTasksAndActionsEqual() {
      final RealizedTask at1 = new RealizedTask(t, a);
      final RealizedTask at2 = new RealizedTask(t, a);
      assertEquals(at1, at2);
    }

  }

}
