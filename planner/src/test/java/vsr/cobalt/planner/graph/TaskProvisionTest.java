/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Task;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

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

    @Test
    public void createFromRealizedTask() {
      final Task r = make(aMinimalTask().withIdentifier("t1"));
      final Task t = make(aMinimalTask().withIdentifier("t2"));
      final Action a = make(aMinimalAction().withTask(t));
      final RealizedTask rt = new RealizedTask(t, a);
      final TaskProvision tp = new TaskProvision(r, rt);
      assertSame(tp.getRequest(), r);
      assertSame(tp.getOffer(), rt.getTask());
      assertSame(tp.getProvidingAction(), rt.getAction());
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
