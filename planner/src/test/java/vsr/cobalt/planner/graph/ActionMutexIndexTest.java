/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class ActionMutexIndexTest {

  @Test
  public static class IsMutex {

    public void returnFalseWithUnknownLevel() {
      final Functionality f = make(aMinimalFunctionality());

      final Action a = make(aMinimalAction()
          .withFunctionality(f));

      final Graph g = make(aMinimalGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f)
                      .withOffer(f)
                      .withProvidingAction(a))));

      final ActionMutexIndex ami = new ActionMutexIndex(g);

      assertFalse(ami.isMutex(null, null, null));
    }

    public void returnFalseWhenNotMutex() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f1));

      final Action a2 = make(aMinimalAction()
          .withFunctionality(f2));

      final Graph g = make(aMinimalGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f1)
                      .withOffer(f1)
                      .withProvidingAction(a1))
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f2)
                      .withOffer(f2)
                      .withProvidingAction(a2))));

      final ActionMutexIndex ami = new ActionMutexIndex(g);

      // assert it's commutative
      assertFalse(ami.isMutex(g.getInitialLevel(), a1, a2));
      assertFalse(ami.isMutex(g.getInitialLevel(), a2, a1));
    }

    public void returnTrueWhenActionPublishesPropertyRequiredClearedByOtherAction() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

      final Property p = make(aMinimalProperty());

      final Widget w = make(aMinimalWidget().withPublic(p));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f1)
          .withEffects(aPropositionSet()
              .withFilled(p)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f2)
          .withPre(aPropositionSet()
              .withCleared(p)));

      final Graph g = make(aMinimalGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f1)
                      .withOffer(f1)
                      .withProvidingAction(a1))
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f2)
                      .withOffer(f2)
                      .withProvidingAction(a2))));

      final ActionMutexIndex ami = new ActionMutexIndex(g);

      // assert it's commutative
      assertTrue(ami.isMutex(g.getInitialLevel(), a1, a2));
      assertTrue(ami.isMutex(g.getInitialLevel(), a2, a1));
    }

    public void returnTrueWhenAnyPairOfPreConditionsCannotBeAchievedInPreviousLevel() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      // a1 and a2 are mutex because p1 and p2 cannot be achieved simultaneously (because of a3 and a4 being mutex)
      // a3 and a4 are mutex by definition

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f1)
          .withPre(aPropositionSet()
              .withFilled(p1)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f2)
          .withPre(aPropositionSet()
              .withFilled(p2)));

      final Action a3 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Action a4 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2))
          .withPre(aPropositionSet()
              .withCleared(p1)));

      final Graph g = make(aMinimalGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f1)
                      .withOffer(f1)
                      .withProvidingAction(a1))
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f2)
                      .withOffer(f2)
                      .withProvidingAction(a2)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withProvision(aPropertyProvision()
                      .withRequest(p1)
                      .withOffer(p1)
                      .withProvidingAction(a3)))
              .withProvision(anActionProvision()
                  .withRequest(a2)
                  .withProvision(aPropertyProvision()
                      .withRequest(p2)
                      .withOffer(p2)
                      .withProvidingAction(a4)))));

      final ActionMutexIndex ami = new ActionMutexIndex(g);

      // assert it's commutative
      assertTrue(ami.isMutex(g.getInitialLevel(), a1, a2));
      assertTrue(ami.isMutex(g.getInitialLevel(), a2, a1));
    }

    public void returnFalseWhenAllPairsOfPreConditionsCanBeAchievedInPreviousLevel() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w1 = make(aMinimalWidget()
          .withIdentifier("w1")
          .withPublic(p1, p2));

      final Widget w2 = make(aMinimalWidget()
          .withIdentifier("w2")
          .withPublic(p1));

      final Widget w3 = make(aMinimalWidget()
          .withIdentifier("w3")
          .withPublic(p2));

      // a1 and a2 are not mutex because p1 and p2 can be achieved simultaneously by a3 and a5 (but not by a3 and a4)
      // a3 and a4 are mutex by definition
      // a3 and a5 are not mutex

      final Action a1 = make(aMinimalAction()
          .withWidget(w1)
          .withFunctionality(f1)
          .withPre(aPropositionSet()
              .withFilled(p1)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w1)
          .withFunctionality(f2)
          .withPre(aPropositionSet()
              .withFilled(p2)));

      final Action a3 = make(aMinimalAction()
          .withWidget(w2)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Action a4 = make(aMinimalAction()
          .withWidget(w3)
          .withEffects(aPropositionSet()
              .withFilled(p2))
          .withPre(aPropositionSet()
              .withCleared(p1)));

      final Action a5 = make(aMinimalAction()
          .withWidget(w3)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      final Graph g = make(aMinimalGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f1)
                      .withOffer(f1)
                      .withProvidingAction(a1))
              .withProvision(
                  aFunctionalityProvision()
                      .withRequest(f2)
                      .withOffer(f2)
                      .withProvidingAction(a2)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withProvision(aPropertyProvision()
                      .withRequest(p1)
                      .withOffer(p1)
                      .withProvidingAction(a3)))
              .withProvision(anActionProvision()
                  .withRequest(a2)
                  .withProvision(aPropertyProvision()
                      .withRequest(p2)
                      .withOffer(p2)
                      .withProvidingAction(a4)))
              .withProvision(anActionProvision()
                  .withRequest(a2)
                  .withProvision(aPropertyProvision()
                      .withRequest(p2)
                      .withOffer(p2)
                      .withProvidingAction(a5)))));

      final ActionMutexIndex ami = new ActionMutexIndex(g);

      // assert it's commutative
      assertFalse(ami.isMutex(g.getInitialLevel(), a1, a2));
      assertFalse(ami.isMutex(g.getInitialLevel(), a2, a1));
    }

  }

}
