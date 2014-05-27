/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;
import vsr.cobalt.planner.Goal;
import vsr.cobalt.planner.models.Task;

import static org.testng.Assert.assertSame;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.immutableSetOf;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;

@Test
public class GoalTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting one or more tasks")
    public void rejectEmptySetOfTasks() {
      new Goal(emptySet(Task.class));
    }

  }

  @Test
  public static class GetTasks {

    @Test
    public void returnTheTasks() {
      final Task t = make(aMinimalTask());
      final ImmutableSet<Task> ts = immutableSetOf(t);
      final Goal g = new Goal(ts);
      assertSame(g.getTasks(), ts);
    }

  }

}
