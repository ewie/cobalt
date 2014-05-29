/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.Task;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.testing.makers.EffectSetMaker.anEffectSet;
import static vsr.cobalt.testing.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.testing.makers.GraphMaker.aGraph;
import static vsr.cobalt.testing.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.testing.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.makers.TaskMaker.aTask;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.makers.WidgetMaker.aWidget;

@Test
public class PlanTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting graph with single provision for each requested task")
    public void rejectGraphContainingLevelWithMultipleProvisionsForSameTask() {
      final Task t = make(aTask().withIdentifier("t"));

      final Action a1 = make(aMinimalAction().withTask(t));

      final Action a2 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w")).withTask(t));

      final TaskProvision tp1 = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t)
          .withRequest(t));

      final TaskProvision tp2 = make(aTaskProvision()
          .withProvidingAction(a2)
          .withOffer(t)
          .withRequest(t));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(tp1, tp2)));

      new Plan(g);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting graph with single provision for each requested action")
    public void rejectGraphContainingLevelWithMultipleActionProvisionsForSameAction() {
      final Task t = make(aTask().withIdentifier("t"));

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(anEffectSet()
              .withToClear(p1)));

      final Action a3 = make(aMinimalAction()
          .withEffects(anEffectSet()
              .withToClear(p1, p2)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a1)
                  .withOffer(t)
                  .withRequest(t)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withPrecursor(a2))
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withPrecursor(a3))));

      new Plan(g);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting graph with only satisfied actions")
    public void rejectGraphContainingActionInInitialLevelWithoutSatisfiedRequirements() {
      final Task t = make(aTask().withIdentifier("t"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withCleared(aMinimalProperty())));

      final TaskProvision tp = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t)
          .withRequest(t));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(tp)));

      new Plan(g);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting graph with only satisfied actions")
    public void rejectGraphContainingActionInExtensionLevelWithoutSatisfiedRequirements() {
      final Task t = make(aTask().withIdentifier("t"));

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(anEffectSet()
              .withToClear(p1))
          .withPre(aPropositionSet()
              .withCleared(p2)));

      final TaskProvision tp = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t)
          .withRequest(t));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(tp))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withPrecursor(a2))));

      new Plan(g);
    }

  }

  @Test
  public static class Getters {

    @Test
    public void getGraph() {
      final Graph g = make(aMinimalGraph());
      final Plan p = new Plan(g);
      assertSame(p.getGraph(), g);
    }

  }

  @Test
  public static class Equality {

    @Test
    public void useHashCodeOfGraph() {
      final Graph g = make(aMinimalGraph());
      final Plan p = new Plan(g);
      assertEquals(p.hashCode(), g.hashCode());
    }

    @Test
    public void returnTrueWhenGraphsEqual() {
      final Plan p1 = new Plan(make(aMinimalGraph()));
      final Plan p2 = new Plan(make(aMinimalGraph()));
      assertEquals(p1, p2);
    }

    @Test
    public void returnFalseWhenComparedWithNonPlan() {
      final Plan p = new Plan(make(aMinimalGraph()));
      final Object x = new Object();
      assertNotEquals(p, x);
    }

    @Test
    public void returnFalseWhenGraphsDiffer() {
      final Task t = make(aTask().withIdentifier("t"));

      final Action a = make(aMinimalAction().withTask(t));

      final Graph g = make(aMinimalGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a)
                  .withOffer(t)
                  .withRequest(t))));

      final Plan p1 = new Plan(g);
      final Plan p2 = new Plan(make(aMinimalGraph()));

      assertNotEquals(p1, p2);
    }

  }

}
