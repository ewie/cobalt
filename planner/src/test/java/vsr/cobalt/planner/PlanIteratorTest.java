/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.utils.ProbingIterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.EffectSetMaker.anEffectSet;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class PlanIteratorTest {

  @Test
  public void extendsBufferedIterator() {
    assertSubClass(PlanIterator.class, ProbingIterator.class);
  }

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting minDepth >= 1")
    public void rejectMinDepthWhenLessThanOne() {
      final Graph g = make(aMinimalGraph());
      new PlanIterator(g, 0, 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting minDepth <= maxDepth")
    public void rejectMinDepthWhenGreaterThanMaxDepth() {
      final Graph g = make(aMinimalGraph());
      new PlanIterator(g, 2, 1);
    }

    public void defaultToMinDepthEqualsOne() {
      final Graph g = make(aMinimalGraph());
      final PlanIterator pi = new PlanIterator(g);
      assertEquals(pi.getMinDepth(), 1);
    }

    public void defaultToMaxDepthEqualsGraphDepth() {
      final Graph g = make(aMinimalGraph());
      final PlanIterator pi = new PlanIterator(g, 1);
      assertEquals(pi.getMaxDepth(), g.getDepth());
    }

  }

  @Test
  public static class Getters {

    @Test
    public void getMinDepth() {
      final Graph g = make(aMinimalGraph());
      final PlanIterator pi = new PlanIterator(g, 1, 1);
      assertEquals(pi.getMinDepth(), 1);
    }

    @Test
    public void getMaxDepth() {
      final Graph g = make(aMinimalGraph());
      final PlanIterator pi = new PlanIterator(g, 1, 1);
      assertEquals(pi.getMaxDepth(), 1);
    }

  }

  @Test
  public static class ProbeNextValue {

    @Test
    public void returnNullWhenThereAreNoMorePlans() {
      final Graph g = make(aMinimalGraph());

      final PlanIterator pi = new PlanIterator(g);

      pi.probeNextValue();
      assertNull(pi.probeNextValue());
    }

    @Test
    public void considerAllTaskProvisions() {
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
          .withRequest(t1));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(tp1, tp2)));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(tp1)));

      final Graph g2 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(tp2)));

      final PlanIterator pi = new PlanIterator(g);

      final Set<Plan> xps = setOf(new Plan(g1), new Plan(g2));
      final Set<Plan> ps = setOf();

      ps.add(pi.probeNextValue());
      ps.add(pi.probeNextValue());

      assertNull(pi.probeNextValue());
      assertEquals(ps, xps);
    }

    @Test
    public void handlePlansWithExtensionLevels() {
      final Task t = make(aMinimalTask());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(anEffectSet().withToClear(p1))
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = make(aMinimalAction()
          .withEffects(anEffectSet().withToClear(p2)));

      final TaskProvision tp = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t)
          .withRequest(t));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a2));

      final ActionProvision ap2 = make(anActionProvision()
          .withRequest(a2)
          .withPrecursor(a3));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel().withTaskProvision(tp))
          .withExtensionLevel(anExtensionLevel().withProvision(ap1))
          .withExtensionLevel(anExtensionLevel().withProvision(ap2)));

      final PlanIterator pi = new PlanIterator(g);

      final Set<Plan> xps = setOf(new Plan(g));
      final Set<Plan> ps = setOf();

      ps.add(pi.probeNextValue());

      assertNull(pi.probeNextValue());
      assertEquals(ps, xps);
    }

    @Test
    public void ignoreUnreachableActions() {
      final Task t = make(aMinimalTask());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final Property p3 = make(aMinimalProperty().withName("p3"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(anEffectSet().withToClear(p1))
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = make(aMinimalAction()
          .withEffects(anEffectSet().withToClear(p1))
          .withPre(aPropositionSet().withCleared(p3)));

      final Action a4 = make(aMinimalAction()
          .withEffects(anEffectSet().withToClear(p2)));

      final TaskProvision tp = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t)
          .withRequest(t));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a2));

      final ActionProvision ap2 = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a3));

      final ActionProvision ap3 = make(anActionProvision()
          .withRequest(a2)
          .withPrecursor(a4));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel().withTaskProvision(tp))
          .withExtensionLevel(anExtensionLevel().withProvision(ap1, ap2))
          .withExtensionLevel(anExtensionLevel().withProvision(ap3)));

      final Graph g2 = make(aGraph()
          .withInitialLevel(anInitialLevel().withTaskProvision(tp))
          .withExtensionLevel(anExtensionLevel().withProvision(ap1))
          .withExtensionLevel(anExtensionLevel().withProvision(ap3)));

      final PlanIterator pi = new PlanIterator(g1);

      final Set<Plan> xps = setOf(new Plan(g2));
      final Set<Plan> ps = setOf();

      ps.add(pi.probeNextValue());

      assertNull(pi.probeNextValue());
      assertEquals(ps, xps);
    }

    @Test
    public void ignorePlansNotReachingMinDepth() {
      final Task t = make(aMinimalTask());

      final Property p = make(aMinimalProperty().withName("p"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction().withTask(t));

      final Action a3 = make(aMinimalAction()
          .withEffects(anEffectSet().withToClear(p)));

      final TaskProvision tp1 = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t)
          .withRequest(t));

      final TaskProvision tp2 = make(aTaskProvision()
          .withProvidingAction(a2)
          .withOffer(t)
          .withRequest(t));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a3));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel().withTaskProvision(tp1, tp2))
          .withExtensionLevel(anExtensionLevel().withProvision(ap)));

      final Graph g2 = make(aGraph()
          .withInitialLevel(anInitialLevel().withTaskProvision(tp1))
          .withExtensionLevel(anExtensionLevel().withProvision(ap)));

      final PlanIterator pi = new PlanIterator(g1, 2, 2);

      final Set<Plan> xps = setOf(new Plan(g2));
      final Set<Plan> ps = setOf();

      ps.add(pi.probeNextValue());

      assertNull(pi.probeNextValue());
      assertEquals(ps, xps);
    }

    @Test
    public void ignorePlansExceedingMaxDepth() {
      final Task t = make(aMinimalTask());

      final Property p = make(aMinimalProperty().withName("p"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(anEffectSet().withToClear(p)));

      final TaskProvision tp = make(aTaskProvision()
          .withProvidingAction(a1)
          .withOffer(t)
          .withRequest(t));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a2));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel().withTaskProvision(tp))
          .withExtensionLevel(anExtensionLevel().withProvision(ap)));

      final PlanIterator pi = new PlanIterator(g, 1, 1);

      assertNull(pi.probeNextValue());
    }

  }

}
