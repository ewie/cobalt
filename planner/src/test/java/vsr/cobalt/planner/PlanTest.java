/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Property;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.Graph;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PlanTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting graph with single provision for each requested functionality")
    public void rejectGraphContainingLevelWithMultipleProvisionsForSameFunctionality() {
      final Functionality f = make(aFunctionality().withIdentifier("f"));

      final Action a1 = make(aMinimalAction().withFunctionality(f));

      final Action a2 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w")).withFunctionality(f));

      final FunctionalityProvision fp1 = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f)
          .withRequest(f));

      final FunctionalityProvision fp2 = make(aFunctionalityProvision()
          .withProvidingAction(a2)
          .withOffer(f)
          .withRequest(f));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withFunctionalityProvision(fp1, fp2)));

      new Plan(g);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting graph with single provision for each requested action")
    public void rejectGraphContainingLevelWithMultipleActionProvisionsForSameAction() {
      final Functionality f = make(aFunctionality().withIdentifier("f"));

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p1)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p1, p2)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withFunctionalityProvision(aFunctionalityProvision()
                  .withProvidingAction(a1)
                  .withOffer(f)
                  .withRequest(f)))
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
      final Functionality f = make(aFunctionality().withIdentifier("f"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(aMinimalProperty())));

      final FunctionalityProvision fp = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f)
          .withRequest(f));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withFunctionalityProvision(fp)));

      new Plan(g);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting graph with only satisfied actions")
    public void rejectGraphContainingActionInExtensionLevelWithoutSatisfiedRequirements() {
      final Functionality f = make(aFunctionality().withIdentifier("f"));

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p1))
          .withPre(aPropositionSet()
              .withCleared(p2)));

      final FunctionalityProvision fp = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f)
          .withRequest(f));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withFunctionalityProvision(fp))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withPrecursor(a2))));

      new Plan(g);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting graph with only non-mutex actions")
    public void rejectGraphContainingAnyActionsBeingMutex() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

      final Property p = make(aMinimalProperty());

      // a1 and a2 mutex

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f1)
          .withPre(aPropositionSet()
              .withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withFunctionality(f2)
          .withEffects(aPropositionSet()
              .withFilled(p)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p)));

      final FunctionalityProvision fp1 = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f1)
          .withRequest(f1));

      final FunctionalityProvision fp2 = make(aFunctionalityProvision()
          .withProvidingAction(a2)
          .withOffer(f2)
          .withRequest(f2));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a3));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withFunctionalityProvision(fp1, fp2))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(ap)));

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
      final Functionality f = make(aFunctionality().withIdentifier("f"));

      final Action a = make(aMinimalAction().withFunctionality(f));

      final Graph g = make(aMinimalGraph()
          .withInitialLevel(anInitialLevel()
              .withFunctionalityProvision(aFunctionalityProvision()
                  .withProvidingAction(a)
                  .withOffer(f)
                  .withRequest(f))));

      final Plan p1 = new Plan(g);
      final Plan p2 = new Plan(make(aMinimalGraph()));

      assertNotEquals(p1, p2);
    }

  }

}
