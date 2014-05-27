/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.Task;
import vsr.cobalt.planner.models.Widget;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.immutableSetOf;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.testing.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.testing.makers.GoalMaker.aGoal;
import static vsr.cobalt.testing.makers.GraphMaker.aGraph;
import static vsr.cobalt.testing.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.testing.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropertyMaker.aProperty;
import static vsr.cobalt.testing.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.makers.TaskMaker.aTask;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aMinimalTaskProvision;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.makers.WidgetMaker.aWidget;

@Test
public class GraphExtenderTest {

  @Test
  public static class CreateGraph {

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot realize some goal task")
    public void throwWhenAnyTaskCannotBeRealized() throws Exception {
      final Task t1 = make(aMinimalTask().withIdentifier("t1"));
      final Task t2 = make(aMinimalTask().withIdentifier("t2"));

      final TaskProvision tp = make(aMinimalTaskProvision());

      final Goal g = make(aGoal().withTask(t1, t2));

      final Repository r = mock(Repository.class);
      when(r.realizeCompatibleTasks(t1)).thenReturn(immutableSetOf(tp));
      when(r.realizeCompatibleTasks(t2)).thenReturn(emptySet(TaskProvision.class));

      final GraphExtender gx = new GraphExtender(r);
      gx.createGraph(g);
    }

    @Test
    public void initializeWithActionsRealizingAnyGoalTasks() throws Exception {
      final Task t1 = make(aTask().withIdentifier("t1"));
      final Task t2 = make(aTask().withIdentifier("t2"));

      final Action a1 = make(aMinimalAction().withTask(t1));
      final Action a2 = make(aMinimalAction().withTask(t2));
      final Action a3 = make(aMinimalAction().withTask(t1, t2));

      final TaskProvision tp1 = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t1)
          .withRequest(t1));

      final TaskProvision tp2 = make(aTaskProvision()
          .withProvidingAction(a2)
          .withOffer(t2)
          .withRequest(t2));

      final TaskProvision tp3 = make(aTaskProvision()
          .withProvidingAction(a3)
          .withOffer(t1)
          .withRequest(t1));

      final TaskProvision tp4 = make(aTaskProvision()
          .withProvidingAction(a3)
          .withOffer(t2)
          .withRequest(t2));

      final Goal goal = make(aGoal().withTask(t1, t2));

      final Repository r = mock(Repository.class);
      when(r.realizeCompatibleTasks(t1)).thenReturn(immutableSetOf(tp1, tp3));
      when(r.realizeCompatibleTasks(t2)).thenReturn(immutableSetOf(tp2, tp4));

      final GraphExtender gx = new GraphExtender(r);
      final Graph g = gx.createGraph(goal);

      assertEquals(g.getInitialLevel().getTaskProvisions(), setOf(tp1, tp2, tp3, tp4));
    }

  }

  @Test
  public static class ExtendPlanningGraph {

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "graph has no unsatisfied actions")
    public void rejectGraphWhenAlreadySatisfied() throws Exception {
      final Graph g = make(aMinimalGraph());

      final Repository r = mock(Repository.class);
      when(r.findPrecursors(any(Action.class))).thenReturn(emptySet(Action.class));

      final GraphExtender gx = new GraphExtender(r);
      gx.extendGraph(g);
    }

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot enable any action required by the graph")
    public void throwWhenNoActionCanBeEnabled() throws Exception {
      final Task t = make(aTask().withIdentifier("t"));

      final Property p = make(aMinimalProperty());

      final Action a = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withCleared(p)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a)
                  .withOffer(t)
                  .withRequest(t))));

      final Repository r = mock(Repository.class);
      when(r.findPrecursors(any(Action.class))).thenReturn(emptySet(Action.class));

      final GraphExtender gx = new GraphExtender(r);
      gx.extendGraph(g);
    }

    @Test
    public void extendWithEnablingActions() throws Exception {
      final Task t1 = make(aTask().withIdentifier("t1"));
      final Task t2 = make(aTask().withIdentifier("t2"));

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      // some initial actions
      final Action a1 = make(aMinimalAction()
          .withTask(t1)
          .withPre(aPropositionSet()
              .withFilled(p1)));

      final Action a2 = make(aMinimalAction()
          .withTask(t2)
          .withPre(aPropositionSet()
              .withFilled(p2)));

      // actions for the first extension
      final Action a3 = make(aMinimalAction()
          .withPost(aPropositionSet()
              .withFilled(p1)));

      final Action a4 = make(aMinimalAction()
          .withPost(aPropositionSet()
              .withFilled(p2)));

      final Action a5 = make(aMinimalAction()
          .withPost(aPropositionSet()
              .withFilled(p2)));

      // create the initial graph
      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a1)
                  .withOffer(t1)
                  .withRequest(t1))
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a2)
                  .withOffer(t2)
                  .withRequest(t2))));

      // mock a repository returning the appropriate actions
      final Repository r = mock(Repository.class);
      when(r.findPrecursors(a1)).thenReturn(immutableSetOf(a3));
      when(r.findPrecursors(a2)).thenReturn(immutableSetOf(a4, a5));

      final GraphExtender gx = new GraphExtender(r);

      final Graph g2 = gx.extendGraph(g1);

      // the actual extension level
      final ExtensionLevel xl = g2.getLastExtensionLevel();

      // the desired extension level
      final ExtensionLevel xxl = new ExtensionLevel(
          immutableSetOf(
              make(anActionProvision()
                  .withPrecursor(a3)
                  .withRequest(a1)),
              make(anActionProvision()
                  .withPrecursor(a4)
                  .withRequest(a2)),
              make(anActionProvision()
                  .withPrecursor(a5)
                  .withRequest(a2))
          )
      );

      assertEquals(xl, xxl);
    }

    @Test
    public void extendWithActionsProvidingRequiredProperties() throws Exception {
      final Task t = make(aTask().withIdentifier("t"));

      final Widget w = make(aWidget().withIdentifier("w"));

      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));
      final Property p3 = make(aProperty().withName("p3"));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withTask(t)
          .withPre(aPropositionSet()
              .withCleared(p3)
              .withFilled(p1)));

      // the action which can be precursor of a1
      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withPost(aPropositionSet().withCleared(p3)));

      // the action publishing a compatible property required by a1
      final Action a3 = make(aMinimalAction().withPub(p2));

      // create the initial graph
      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a1)
                  .withOffer(t)
                  .withRequest(t))));

      final PropertyProvision pp = make(aPropertyProvision()
          .withProvidingAction(a3)
          .withOffer(p2)
          .withRequest(p1));

      final Repository r = mock(Repository.class);
      when(r.findPrecursors(a1)).thenReturn(immutableSetOf(a2));
      when(r.provideCompatibleProperties(p1)).thenReturn(immutableSetOf(pp));

      final GraphExtender gx = new GraphExtender(r);

      final Graph g2 = gx.extendGraph(g1);

      // the actual extension level
      final ExtensionLevel xl = g2.getLastExtensionLevel();

      // the desired extension level
      final ExtensionLevel xxl = new ExtensionLevel(
          immutableSetOf(
              make(anActionProvision()
                  .withPrecursor(a2)
                  .withRequest(a1)
                  .withProvision(pp))
          )
      );

      assertEquals(xl, xxl);
    }

    @Test
    public void ignoreActionsWhoseRequiredPropertiesCannotBeProvided() throws Exception {
      final Task t = make(aTask().withIdentifier("t"));

      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withFilled(p1)));

      final Action a2 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withCleared(p2)));

      // an action which can be source of a1 and a2
      final Action a3 = make(aMinimalAction()
          .withPost(aPropositionSet()
              .withCleared(p2)));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a1)
                  .withOffer(t)
                  .withRequest(t))
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a2)
                  .withOffer(t)
                  .withRequest(t))));

      final Repository r = mock(Repository.class);
      when(r.findPrecursors(a1)).thenReturn(immutableSetOf(a3));
      when(r.findPrecursors(a2)).thenReturn(immutableSetOf(a3));
      when(r.provideCompatibleProperties(p1)).thenReturn(emptySet(PropertyProvision.class));

      final GraphExtender gx = new GraphExtender(r);

      final Graph g2 = gx.extendGraph(g1);

      // the actual extension level
      final ExtensionLevel xl = g2.getLastExtensionLevel();

      // the desired extension level
      final ExtensionLevel xxl = new ExtensionLevel(
          immutableSetOf(
              make(anActionProvision()
                  .withPrecursor(a3)
                  .withRequest(a2))
          )
      );

      assertEquals(xl, xxl);
    }

    @Test
    public void createPrecursorLessActionProvisionWhenRepositoryReturnsNoPrecursors() throws Exception {
      final Task t = make(aTask().withIdentifier("t"));

      final Property p = make(aProperty().withName("p"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withFilled(p)));

      final Action a2 = make(aMinimalAction().withPub(p));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a1)
                  .withOffer(t)
                  .withRequest(t))));

      final PropertyProvision pp = make(aPropertyProvision()
          .withProvidingAction(a2)
          .withOffer(p)
          .withRequest(p));

      final Repository r = mock(Repository.class);
      when(r.findPrecursors(a1)).thenReturn(emptySet(Action.class));
      when(r.provideCompatibleProperties(p)).thenReturn(immutableSetOf(pp));

      final GraphExtender gx = new GraphExtender(r);

      final Graph g2 = gx.extendGraph(g1);

      // the actual extension level
      final ExtensionLevel xl = g2.getLastExtensionLevel();

      // the desired extension level
      final ExtensionLevel xxl = new ExtensionLevel(
          immutableSetOf(
              make(anActionProvision()
                  .withRequest(a1)
                  .withProvision(pp))
          )
      );

      assertEquals(xl, xxl);
    }

    @Test
    public void createSeperateActionProvisionsForEachPropertyProvisionCombination() throws Exception {
      final Task t = make(aTask().withIdentifier("t"));

      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withFilled(p1, p2)));

      final Action a2 = make(aMinimalAction().withPub(p1));

      final Action a3 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w"))
          .withPub(p1));

      final Action a4 = make(aMinimalAction().withPub(p2));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a1)
                  .withOffer(t)
                  .withRequest(t))));

      final PropertyProvision pp1 = make(aPropertyProvision()
          .withProvidingAction(a2)
          .withOffer(p1)
          .withRequest(p1));

      final PropertyProvision pp2 = make(aPropertyProvision()
          .withProvidingAction(a3)
          .withOffer(p1)
          .withRequest(p1));

      final PropertyProvision pp3 = make(aPropertyProvision()
          .withProvidingAction(a4)
          .withOffer(p2)
          .withRequest(p2));

      final Repository r = mock(Repository.class);
      when(r.findPrecursors(a1)).thenReturn(emptySet(Action.class));
      when(r.provideCompatibleProperties(p1)).thenReturn(immutableSetOf(pp1, pp2));
      when(r.provideCompatibleProperties(p2)).thenReturn(immutableSetOf(pp3));

      final GraphExtender gx = new GraphExtender(r);

      final Graph g2 = gx.extendGraph(g1);

      // the actual extension level
      final ExtensionLevel xl = g2.getLastExtensionLevel();

      // the desired extension level
      final ExtensionLevel xxl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp1, pp3))
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp2, pp3)));

      assertEquals(xl, xxl);
    }

  }

}
