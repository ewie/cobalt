/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.rating;

import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.Level;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.EffectSetMaker.anEffectSet;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.planner.graph.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class TraversingPlanRaterTest {

  private Plan plan;

  private Graph graph;

  private PlanRaterVisitor visitor;

  private TraversingPlanRater rater;

  private InOrder order;

  @BeforeMethod
  public void setUp() {
    final Task t1 = make(aTask().withIdentifier("t1"));
    final Task t2 = make(aTask().withIdentifier("t2"));

    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));
    final Property p3 = make(aMinimalProperty().withName("p3"));
    final Property p4 = make(aMinimalProperty().withName("p4"));

    final Action a1 = make(aMinimalAction().withTask(t1));

    final Action a2 = make(aMinimalAction()
        .withTask(t2)
        .withPre(aPropositionSet()
            .withCleared(p1)
            .withFilled(p3, p4)));

    final Action a3 = make(aMinimalAction()
        .withEffects(anEffectSet()
            .withToClear(p1))
        .withPre(aPropositionSet()
            .withCleared(p2)));

    final Action a4 = make(aMinimalAction()
        .withEffects(anEffectSet()
            .withToClear(p2)));

    final Action a5 = make(aMinimalAction()
        .withPub(p3, p4));

    final TaskProvision tp1 = make(aTaskProvision()
        .withRequest(t1)
        .withOffer(t1)
        .withProvidingAction(a1));

    final TaskProvision tp2 = make(aTaskProvision()
        .withRequest(t2)
        .withOffer(t2)
        .withProvidingAction(a2));

    final PropertyProvision pp1 = make(aPropertyProvision()
        .withRequest(p3)
        .withOffer(p3)
        .withProvidingAction(a5));

    final PropertyProvision pp2 = make(aPropertyProvision()
        .withRequest(p4)
        .withOffer(p4)
        .withProvidingAction(a5));

    final ActionProvision ap1 = make(anActionProvision()
        .withRequest(a2)
        .withPrecursor(a3)
        .withProvision(pp1, pp2));

    final ActionProvision ap2 = make(anActionProvision()
        .withRequest(a3)
        .withPrecursor(a4));

    final ExtensionLevel xl1 = make(anExtensionLevel()
        .withProvision(ap1));

    final ExtensionLevel xl2 = make(anExtensionLevel()
        .withProvision(ap2));

    final InitialLevel il = make(anInitialLevel()
        .withTaskProvision(tp1, tp2));

    graph = make(aGraph()
        .withInitialLevel(il)
        .withExtensionLevel(xl1, xl2));

    plan = new Plan(graph);

    visitor = mock(PlanRaterVisitor.class);
    rater = new TraversingPlanRater(visitor);
    order = inOrder(visitor);
  }

  @Test
  public void visitGraph() {
    rater.rate(plan);
    verify(visitor).rateGraph(graph);
  }

  @Test
  public void visitInitialLevelAfterGraph() {
    rater.rate(plan);
    order.verify(visitor).rateGraph(graph);
    order.verify(visitor).rateLevel(graph.getInitialLevel());
  }

  @Test
  public void visitTaskProvisionsAfterInitialLevel() {
    rater.rate(plan);
    final InitialLevel il = graph.getInitialLevel();
    order.verify(visitor).rateLevel(il);
    for (final TaskProvision tp : il.getTaskProvisions()) {
      order.verify(visitor).rateTaskProvision(tp);
    }
  }

  @Test
  public void visitRequiredActionsAfterInitialLevel() {
    rater.rate(plan);
    final InitialLevel il = graph.getInitialLevel();
    order.verify(visitor).rateLevel(il);
    for (final Action a : il.getRequiredActions()) {
      order.verify(visitor).rateAction(a);
    }
  }

  @Test
  public void visitExtensionLevels() {
    rater.rate(plan);
    order.verify(visitor).rateLevel(graph.getInitialLevel());
    for (int i = 0; i < graph.getExtensionDepth(); i += 1) {
      order.verify(visitor).rateLevel(graph.getExtensionLevel(i));
    }
  }

  @Test
  public void visitActionProvisionsAfterEachExtensionLevel() {
    rater.rate(plan);

    final ExtensionLevel xl = graph.getExtensionLevel(0);

    order.verify(visitor).rateLevel(xl);
    for (final ActionProvision ap : xl.getActionProvisions()) {
      order.verify(visitor).rateActionProvision(ap);
    }
  }

  @Test
  public void visitPropertyProvisionsAfterEachActionProvision() {
    rater.rate(plan);

    final ExtensionLevel xl = graph.getExtensionLevel(0);

    for (final ActionProvision ap : xl.getActionProvisions()) {
      order.verify(visitor).rateActionProvision(ap);
      for (final PropertyProvision pp : ap.getPropertyProvisions()) {
        order.verify(visitor).ratePropertyProvision(pp);
      }
    }
  }

  @Test
  public void visitRequiredActionsAfterAllActionProvisions() {
    rater.rate(plan);

    final ExtensionLevel xl = graph.getExtensionLevel(0);

    for (final ActionProvision ap : xl.getActionProvisions()) {
      order.verify(visitor).rateActionProvision(ap);
    }
    for (final Action a : xl.getRequiredActions()) {
      order.verify(visitor).rateAction(a);
    }
  }

  @Test
  public void sumAllRatings() {
    when(visitor.rateAction(any(Action.class))).thenReturn(1);
    when(visitor.rateActionProvision(any(ActionProvision.class))).thenReturn(1);
    when(visitor.rateGraph(any(Graph.class))).thenReturn(1);
    when(visitor.rateLevel(any(Level.class))).thenReturn(1);
    when(visitor.ratePropertyProvision(any(PropertyProvision.class))).thenReturn(1);
    when(visitor.rateTaskProvision(any(TaskProvision.class))).thenReturn(1);

    final Rating r = rater.rate(plan);

    assertEquals(r.getValue(), 15);
  }

  @Test
  public void returnNullWhenGraphRatingIsNull() {
    when(visitor.rateGraph(any(Graph.class))).thenReturn(null);
    assertNull(rater.rate(plan));
  }

  @Test
  public void returnNullWhenLevelRatingIsNull() {
    when(visitor.rateLevel(any(Level.class))).thenReturn(null);
    assertNull(rater.rate(plan));
  }

  @Test
  public void returnNullWhenTaskProvisionRatingIsNull() {
    when(visitor.rateTaskProvision(any(TaskProvision.class))).thenReturn(null);
    assertNull(rater.rate(plan));
  }

  @Test
  public void returnNullWhenActionRatingIsNull() {
    when(visitor.rateAction(any(Action.class))).thenReturn(null);
    assertNull(rater.rate(plan));
  }

  @Test
  public void returnNullWhenActionProvisionRatingIsNull() {
    when(visitor.rateActionProvision(any(ActionProvision.class))).thenReturn(null);
    assertNull(rater.rate(plan));
  }

  @Test
  public void returnNullWhenPropertyProvisionRatingIsNull() {
    when(visitor.ratePropertyProvision(any(PropertyProvision.class))).thenReturn(null);
    assertNull(rater.rate(plan));
  }

}
