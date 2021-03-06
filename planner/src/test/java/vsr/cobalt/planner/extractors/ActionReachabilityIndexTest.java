/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extractors;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.PropertyProvision;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class ActionReachabilityIndexTest {

  @Test
  public static class IsReachable {

    @Test
    public void returnTrueWhenActionHasEmptyPreConditions() {
      final Functionality f = make(aMinimalFunctionality());

      final Action a = make(aMinimalAction().withFunctionality(f));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withProvidingAction(a)
                  .withOffer(f)
                  .withRequest(f))));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      assertTrue(index.isReachable(g.getInitialLevel(), a));
    }

    @Test
    public void returnFalseWhenActionHasUnsatisfiedPreConditions() {
      final Functionality f = make(aMinimalFunctionality());
      final Property p = make(aMinimalProperty());

      final Action a = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withFilled(p)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withProvidingAction(a)
                  .withOffer(f)
                  .withRequest(f))));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      assertFalse(index.isReachable(g.getInitialLevel(), a));
    }

    @Test
    public void returnFalseWhenNoActionProvisionCanBeEnabled() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withFilled(p1)));

      // a non-enabled precursor action for a1
      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1))
          .withEffects(aPropositionSet().withFilled(p1)));

      // a non-enabled precursor action for a1
      final Action a3 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p2))
          .withEffects(aPropositionSet().withFilled(p1)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withProvidingAction(a1)
                  .withOffer(f)
                  .withRequest(f)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withPrecursor(a2)
                  .withRequest(a1))
              .withProvision(anActionProvision()
                  .withPrecursor(a3)
                  .withRequest(a1))));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      assertFalse(index.isReachable(g.getInitialLevel(), a1));
    }

    @Test
    public void enableActionProvisionWhenAllRequiredActionsCanBeEnabled() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet().withFilled(p1, p2)));

      // an enabled precursor action for request
      final Action precursor = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet().withFilled(p1)));

      // an enabled providing action for request
      final Action provider = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      // a property provision with enabled providing action
      final PropertyProvision pp = make(aPropertyProvision()
          .withProvidingAction(provider)
          .withOffer(p2)
          .withRequest(p2));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withPrecursor(precursor)
          .withProvision(pp));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withProvidingAction(request)
                  .withOffer(f)
                  .withRequest(f)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(ap)));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      // assert that the provision is enabled by checking that the request is enabled
      assertTrue(index.isReachable(g.getInitialLevel(), request));
    }

    @Test
    public void doNotEnableActionProvisionWhenPrecursorCannotBeEnabled() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty().withName("p"));

      final Action request = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withFilled(p)));

      // a non-enabled precursor action for request
      final Action precursor = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p))
          .withEffects(aPropositionSet().withFilled(p)));

      // cannot be enabled because of the precursor action
      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withPrecursor(precursor));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withProvidingAction(request)
                  .withOffer(f)
                  .withRequest(f)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(ap)));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      // assert that the provision is not enabled by checking that the request is not enabled
      assertFalse(index.isReachable(g.getInitialLevel(), request));
    }

    @Test
    public void doNotEnableActionProvisionWhenAnyProvidingActionCannotBeEnabled() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet().withFilled(p1, p2)));

      // an enabled precursor action for request
      final Action provider1 = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet().withCleared(p1))
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      // a non-enabled precursor action for request
      final Action provider2 = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet().withCleared(p2))
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      // a property provision with enabled action provider
      final PropertyProvision pp1 = make(aPropertyProvision()
          .withProvidingAction(provider1)
          .withOffer(p1)
          .withRequest(p1));

      // a property provision with non-enabled action provider
      final PropertyProvision pp2 = make(aPropertyProvision()
          .withProvidingAction(provider2)
          .withOffer(p2)
          .withRequest(p2));

      // cannot be enabled because not all providing actions are enabled
      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withProvision(pp1, pp2));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withProvidingAction(request)
                  .withOffer(f)
                  .withRequest(f)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(ap)));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      // assert that the provision is not enabled by checking that the request is not enabled
      assertFalse(index.isReachable(g.getInitialLevel(), request));
    }

  }

}
