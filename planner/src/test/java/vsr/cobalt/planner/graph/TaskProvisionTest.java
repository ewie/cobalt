/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import org.testng.annotations.BeforeMethod;
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
  public void extendsProvision() {
    assertSubClass(TaskProvision.class, Provision.class);
  }

  @Test
  public static class New {

    @Test
    public void useOfferedSubjectAsRequestWhenCreatedFromOffer() {
      final Task t = make(aMinimalTask());
      final Action a = make(aMinimalAction().withTask(t));
      final RealizedTask rt = new RealizedTask(t, a);
      final TaskProvision tp = new TaskProvision(rt);
      assertSame(tp.getRequest(), rt.getSubject());
    }

  }

  @Test
  public static class CanEqual {

    private TaskProvision tp;

    @BeforeMethod
    public void setUp() {
      final Task t = make(aMinimalTask());
      final Action a = make(aMinimalAction().withTask(t));
      final RealizedTask rt = new RealizedTask(t, a);
      tp = new TaskProvision(t, rt);
    }

    @Test
    public void returnTrueWhenCalledWithTaskProvision() {
      assertTrue(tp.canEqual(tp));
    }

    @Test
    public void returnFalseWhenCalledWithNonTaskProvision() {
      final Object x = new Object();
      assertFalse(tp.canEqual(x));
    }

  }

}
