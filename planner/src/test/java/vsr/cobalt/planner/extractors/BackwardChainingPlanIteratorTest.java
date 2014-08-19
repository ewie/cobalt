/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extractors;

import java.util.Set;

import com.google.common.collect.Sets;
import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Property;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.Graph;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class BackwardChainingPlanIteratorTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting minDepth >= 1")
    public void rejectMinDepthWhenLessThanOne() {
      final Graph g = make(aMinimalGraph());
      new BackwardChainingPlanIterator(g, 0, 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting minDepth <= maxDepth")
    public void rejectMinDepthWhenGreaterThanMaxDepth() {
      final Graph g = make(aMinimalGraph());
      new BackwardChainingPlanIterator(g, 2, 1);
    }

    public void defaultToMinDepthEqualsOne() {
      final Graph g = make(aMinimalGraph());
      final BackwardChainingPlanIterator pi = new BackwardChainingPlanIterator(g);
      assertEquals(pi.getMinDepth(), 1);
    }

    public void defaultToMaxDepthEqualsGraphDepth() {
      final Graph g = make(aMinimalGraph());
      final BackwardChainingPlanIterator pi = new BackwardChainingPlanIterator(g, 1);
      assertEquals(pi.getMaxDepth(), g.getDepth());
    }

  }

  @Test
  public static class Iteration {

    @Test
    public void considerAllFunctionalityProvisions() {
      final Functionality f1 = make(aMinimalFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aMinimalFunctionality().withIdentifier("f2"));

      final Action a1 = make(aMinimalAction().withFunctionality(f1));

      final Action a2 = make(aMinimalAction().withFunctionality(f2));

      final FunctionalityProvision fp1 = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f1)
          .withRequest(f1));

      final FunctionalityProvision fp2 = make(aFunctionalityProvision()
          .withProvidingAction(a2)
          .withOffer(f2)
          .withRequest(f1));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(fp1, fp2)));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(fp1)));

      final Graph g2 = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(fp2)));

      final BackwardChainingPlanIterator pi = new BackwardChainingPlanIterator(g);

      final Set<Plan> xps = setOf(new Plan(g1), new Plan(g2));
      final Set<Plan> ps = Sets.newHashSet(pi);

      assertEquals(ps, xps);
    }

    @Test
    public void handlePlansWithExtensionLevels() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1))
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p2)));

      final FunctionalityProvision fp = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f)
          .withRequest(f));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a2));

      final ActionProvision ap2 = make(anActionProvision()
          .withRequest(a2)
          .withPrecursor(a3));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel().withProvision(fp))
          .withExtensionLevel(anExtensionLevel().withProvision(ap1))
          .withExtensionLevel(anExtensionLevel().withProvision(ap2)));

      final BackwardChainingPlanIterator pi = new BackwardChainingPlanIterator(g);

      final Set<Plan> xps = setOf(new Plan(g));
      final Set<Plan> ps = Sets.newHashSet(pi);

      assertEquals(ps, xps);
    }

    @Test
    public void ignoreUnreachableActions() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final Property p3 = make(aMinimalProperty().withName("p3"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1))
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1))
          .withPre(aPropositionSet().withCleared(p3)));

      final Action a4 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p2)));

      final FunctionalityProvision fp = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f)
          .withRequest(f));

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
          .withInitialLevel(anInitialLevel().withProvision(fp))
          .withExtensionLevel(anExtensionLevel().withProvision(ap1, ap2))
          .withExtensionLevel(anExtensionLevel().withProvision(ap3)));

      final Graph g2 = make(aGraph()
          .withInitialLevel(anInitialLevel().withProvision(fp))
          .withExtensionLevel(anExtensionLevel().withProvision(ap1))
          .withExtensionLevel(anExtensionLevel().withProvision(ap3)));

      final BackwardChainingPlanIterator pi = new BackwardChainingPlanIterator(g1);

      final Set<Plan> xps = setOf(new Plan(g2));
      final Set<Plan> ps = Sets.newHashSet(pi);

      assertEquals(ps, xps);
    }

    @Test
    public void ignorePlansWithActionsBeingMutex() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final Property p3 = make(aMinimalProperty().withName("p3"));

      // a1 and a2 not mutex
      // a1 and a3 mutex by definition
      // a4, a5, a6 not mutex

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f1)
          .withEffects(aPropositionSet().withFilled(p3))
          .withPre(aPropositionSet().withFilled(p1)));

      final Action a2 = make(aMinimalAction()
          .withFunctionality(f2)
          .withPre(aPropositionSet().withFilled(p2)));

      final Action a3 = make(aMinimalAction()
          .withFunctionality(f2)
          .withPre(aPropositionSet().withCleared(p3)));

      final Action a4 = make(aMinimalAction()
          .withEffects(aPropositionSet().withFilled(p1)));

      final Action a5 = make(aMinimalAction()
          .withEffects(aPropositionSet().withFilled(p2)));

      final Action a6 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p3)));

      final FunctionalityProvision fp1 = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f1)
          .withRequest(f1));

      final FunctionalityProvision fp2 = make(aFunctionalityProvision()
          .withProvidingAction(a2)
          .withOffer(f2)
          .withRequest(f2));

      final FunctionalityProvision fp3 = make(aFunctionalityProvision()
          .withProvidingAction(a3)
          .withOffer(f2)
          .withRequest(f2));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(a1)
          .withProvision(aPropertyProvision()
              .withRequest(p1)
              .withOffer(p1)
              .withProvidingAction(a4)));

      final ActionProvision ap2 = make(anActionProvision()
          .withRequest(a2)
          .withProvision(aPropertyProvision()
              .withRequest(p2)
              .withOffer(p2)
              .withProvidingAction(a5)));

      final ActionProvision ap3 = make(anActionProvision()
          .withRequest(a3)
          .withPrecursor(a6));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel().withProvision(fp1, fp2, fp3))
          .withExtensionLevel(anExtensionLevel().withProvision(ap1, ap2, ap3)));

      final Graph g2 = make(aGraph()
          .withInitialLevel(anInitialLevel().withProvision(fp1, fp2))
          .withExtensionLevel(anExtensionLevel().withProvision(ap1, ap2)));

      final BackwardChainingPlanIterator pi = new BackwardChainingPlanIterator(g1);

      final Set<Plan> xps = setOf(new Plan(g2));
      final Set<Plan> ps = Sets.newHashSet(pi);

      assertEquals(ps, xps);
    }

    @Test
    public void ignorePlansNotReachingMinDepth() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty().withName("p"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction().withFunctionality(f));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final FunctionalityProvision fp1 = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f)
          .withRequest(f));

      final FunctionalityProvision fp2 = make(aFunctionalityProvision()
          .withProvidingAction(a2)
          .withOffer(f)
          .withRequest(f));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a3));

      final Graph g1 = make(aGraph()
          .withInitialLevel(anInitialLevel().withProvision(fp1, fp2))
          .withExtensionLevel(anExtensionLevel().withProvision(ap)));

      final Graph g2 = make(aGraph()
          .withInitialLevel(anInitialLevel().withProvision(fp1))
          .withExtensionLevel(anExtensionLevel().withProvision(ap)));

      final BackwardChainingPlanIterator pi = new BackwardChainingPlanIterator(g1, 2, 2);

      final Set<Plan> xps = setOf(new Plan(g2));
      final Set<Plan> ps = Sets.newHashSet(pi);

      assertEquals(ps, xps);
    }

    @Test
    public void ignorePlansExceedingMaxDepth() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty().withName("p"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final FunctionalityProvision fp = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f)
          .withRequest(f));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a2));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel().withProvision(fp))
          .withExtensionLevel(anExtensionLevel().withProvision(ap)));

      final BackwardChainingPlanIterator pi = new BackwardChainingPlanIterator(g, 1, 1);

      assertFalse(pi.hasNext());
    }

  }

}
