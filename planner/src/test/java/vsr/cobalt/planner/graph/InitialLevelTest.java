/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Task;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.InitialLevelMaker.aMinimalInitialLevel;
import static vsr.cobalt.testing.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.makers.TaskMaker.aTask;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aMinimalTaskProvision;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aTaskProvision;

@Test
public class InitialLevelTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting one or more task provisions")
    public void rejectEmptySetOfTaskProvisions() {
      new InitialLevel(emptySet(TaskProvision.class));
    }

    @Test
    public void preventModificationOfTaskProvisions() {
      final TaskProvision tp = make(aMinimalTaskProvision());
      final Set<TaskProvision> tps = setOf(tp);
      final InitialLevel il = new InitialLevel(tps);
      tps.add(null);
      assertNotEquals(il.getTaskProvisions(), tps);
    }

  }

  @Test
  public static class GetTaskProvisions {

    @Test
    public void returnAllTaskProvisions() {
      final TaskProvision tp = make(aMinimalTaskProvision());
      final InitialLevel s = make(anInitialLevel().withTaskProvision(tp));
      assertEquals(s.getTaskProvisions(), setOf(tp));
    }

  }

  @Test
  public static class GetRequestedTasks {

    @Test
    public void returnAllRequestedTasks() {
      final Task t1 = make(aMinimalTask().withIdentifier("t1"));
      final Task t2 = make(aMinimalTask().withIdentifier("t2"));

      final Action a1 = make(aMinimalAction().withTask(t1));
      final Action a2 = make(aMinimalAction().withTask(t2));

      final TaskProvision tp1 = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t1)
          .withRequest(t1));

      final TaskProvision tp2 = make(aTaskProvision()
          .withProvidingAction(a2)
          .withOffer(t2)
          .withRequest(t2));

      final InitialLevel s = make(anInitialLevel().withTaskProvision(tp1, tp2));

      assertEquals(s.getRequestedTasks(), setOf(t1, t2));
    }

  }

  @Test
  public static class GetTaskProvisionsByRequestedTask {

    @Test
    public void returnEmptySetWhenThereIsNoTaskProvisionForTheGivenTask() {
      final InitialLevel s = make(aMinimalInitialLevel());
      final Task t = make(aTask().withIdentifier("x"));
      assertEmpty(s.getTaskProvisionsByRequestedTask(t));
    }

    @Test
    public void returnAllTaskProvisionsHavingTheRequiredTask() {
      final Task t1 = make(aTask().withIdentifier("t1"));

      final TaskProvision tp1 = make(aTaskProvision()
          .withProvidingAction(aMinimalAction().withTask(t1))
          .withOffer(t1)
          .withRequest(t1));

      final TaskProvision tp2 = make(aMinimalTaskProvision());

      final InitialLevel s = make(anInitialLevel().withTaskProvision(tp1, tp2));

      assertEquals(s.getTaskProvisionsByRequestedTask(t1), setOf(tp1));
    }

  }

  @Test
  public static class GetRequiredActions {

    @Test
    public void returnTheUnionOfAllRequiredActions() {
      final Task t1 = make(aTask().withIdentifier("t1"));
      final Task t2 = make(aTask().withIdentifier("t2"));

      final Action a1 = make(aMinimalAction().withTask(t1));
      final Action a2 = make(aMinimalAction().withTask(t2));

      final InitialLevel s = make(anInitialLevel()
          .withTaskProvision(aTaskProvision()
              .withProvidingAction(a1)
              .withOffer(t1))
          .withTaskProvision(aTaskProvision()
              .withProvidingAction(a2)
              .withOffer(t2)));

      assertEquals(s.getRequiredActions(), setOf(a1, a2));
    }

  }

  @Test
  public static class Equality {

    @Test
    public void useHashCodeOfTaskProvisionSet() {
      final InitialLevel il = new InitialLevel(setOf(make(aMinimalTaskProvision())));
      assertEquals(il.hashCode(), il.getTaskProvisions().hashCode());
    }

    @Test
    public void returnTrueWhenTaskProvisionSetsAreEqual() {
      final InitialLevel il1 = new InitialLevel(setOf(make(aMinimalTaskProvision())));
      final InitialLevel il2 = new InitialLevel(setOf(make(aMinimalTaskProvision())));
      assertEquals(il1, il2);
    }

    @Test
    public void returnFalseWhenComparedWithNonInitialLevel() {
      final InitialLevel il = new InitialLevel(setOf(make(aMinimalTaskProvision())));
      final Object x = new Object();
      assertNotEquals(il, x);
    }

    @Test
    public void returnFalseWhenTaskProvisionSetsDiffer() {
      final Task t = make(aMinimalTask().withIdentifier("t"));

      final Action a = make(aMinimalAction().withTask(t));

      final TaskProvision tp1 = make(aTaskProvision()
          .withProvidingAction(a)
          .withRequest(t)
          .withOffer(t));

      final TaskProvision tp2 = make(aMinimalTaskProvision());

      final InitialLevel il1 = new InitialLevel(setOf(tp1));
      final InitialLevel il2 = new InitialLevel(setOf(tp2));

      assertNotEquals(il1, il2);
    }

  }

}
