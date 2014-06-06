/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Set;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class MashupTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting one or more tasks")
    public void rejectEmptySetOfTasks() {
      new Mashup(emptySet(Task.class));
    }

    @Test
    public void preventModificationOfTaskProvisions() {
      final Task t = make(aMinimalTask());
      final Set<Task> ts = setOf(t);
      final Mashup m = new Mashup(ts);
      ts.add(null);
      assertNotEquals(m.getTasks(), ts);
    }

  }

  @Test
  public static class GetTasks {

    @Test
    public void returnTheTasks() {
      final Task t = make(aMinimalTask());
      final Set<Task> ts = setOf(t);
      final Mashup m = new Mashup(ts);
      assertEquals(m.getTasks(), ts);
    }

  }

}
