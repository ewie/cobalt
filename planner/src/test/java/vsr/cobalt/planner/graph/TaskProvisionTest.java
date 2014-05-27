/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import org.testng.annotations.Test;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Task;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.testing.Utilities.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;

@Test
public class TaskProvisionTest {

  @Test
  public void extendsIdentifiable() {
    assertSubClass(TaskProvision.class, Provision.class);
  }

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting the offered task to be realized by providing action")
    public void rejectOfferedTaskWhenNotRealizedByGivenAction() {
      final Action a = make(aMinimalAction());
      final Task t = make(aMinimalTask());
      new TaskProvision(null, t, a);
    }

  }

  @Test
  public static class CanEqual {

    @Test
    public void returnTrueWhenCalledWithTaskProvision() {
      final Task t = make(aMinimalTask());
      final Action a = make(aMinimalAction().withTask(t));
      final TaskProvision tp1 = new TaskProvision(null, t, a);
      final TaskProvision tp2 = new TaskProvision(null, t, a);
      assertTrue(tp1.canEqual(tp2));
    }

    @Test
    public void returnFalseWhenCalledWithNonTaskProvision() {
      final Task t = make(aMinimalTask());
      final Action a = make(aMinimalAction().withTask(t));
      final TaskProvision tp = new TaskProvision(null, t, a);
      final Object x = new Object();
      assertFalse(tp.canEqual(x));
    }

  }

}
