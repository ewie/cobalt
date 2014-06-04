/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.PropertyProvision;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.EffectSetMaker.anEffectSet;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.planner.graph.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class ActionReachabilityIndexTest {

  @Test
  public static class IsReachable {

    @Test
    public void returnTrueWhenActionHasEmptyPreConditions() {
      final Task t = make(aMinimalTask());

      final Action a = make(aMinimalAction().withTask(t));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a)
                  .withOffer(t)
                  .withRequest(t))));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      assertTrue(index.isReachable(a, g.getInitialLevel()));
    }

    @Test
    public void returnFalseWhenActionHasUnsatisfiedPreConditions() {
      final Task t = make(aMinimalTask());
      final Property p = make(aMinimalProperty());

      final Action a = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet()
              .withFilled(p)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a)
                  .withOffer(t)
                  .withRequest(t))));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      assertFalse(index.isReachable(a, g.getInitialLevel()));
    }

    @Test
    public void returnFalseWhenNoActionProvisionCanBeEnabled() {
      final Task t = make(aMinimalTask());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet().withFilled(p1)));

      // a non-enabled precursor action for a1
      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1))
          .withEffects(anEffectSet().withToFill(p1)));

      // a non-enabled precursor action for a1
      final Action a3 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p2))
          .withEffects(anEffectSet().withToFill(p1)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(a1)
                  .withOffer(t)
                  .withRequest(t)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withPrecursor(a2)
                  .withRequest(a1))
              .withProvision(anActionProvision()
                  .withPrecursor(a3)
                  .withRequest(a1))));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      assertFalse(index.isReachable(a1, g.getInitialLevel()));
    }

    @Test
    public void enableActionProvisionWhenAllRequiredActionsCanBeEnabled() {
      final Task t = make(aMinimalTask());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action request = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet().withFilled(p1, p2)));

      // an enabled precursor action for request
      final Action precursor = make(aMinimalAction()
          .withEffects(anEffectSet().withToFill(p1)));

      // an enabled providing action for request
      final Action provider = make(aMinimalAction().withPub(p2));

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
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(request)
                  .withOffer(t)
                  .withRequest(t)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(ap)));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      // assert that the provision is enabled by checking that the request is enabled
      assertTrue(index.isReachable(request, g.getInitialLevel()));
    }

    @Test
    public void doNotEnableActionProvisionWhenPrecursorCannotBeEnabled() {
      final Task t = make(aMinimalTask());

      final Property p = make(aMinimalProperty().withName("p"));

      final Action request = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet().withFilled(p)));

      // a non-enabled precursor action for request
      final Action precursor = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p))
          .withEffects(anEffectSet().withToFill(p)));

      // cannot be enabled because of the precursor action
      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withPrecursor(precursor));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(request)
                  .withOffer(t)
                  .withRequest(t)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(ap)));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      // assert that the provision is not enabled by checking that the request is not enabled
      assertFalse(index.isReachable(request, g.getInitialLevel()));
    }

    @Test
    public void doNotEnableActionProvisionWhenAnyProvidingActionCannotBeEnabled() {
      final Task t = make(aMinimalTask());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action request = make(aMinimalAction()
          .withTask(t)
          .withPre(aPropositionSet().withFilled(p1, p2)));

      // an enabled precursor action for request
      final Action provider1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1))
          .withPub(p1));

      // a non-enabled precursor action for request
      final Action provider2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p2))
          .withPub(p2));

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
              .withTaskProvision(aTaskProvision()
                  .withProvidingAction(request)
                  .withOffer(t)
                  .withRequest(t)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(ap)));

      final ActionReachabilityIndex index = new ActionReachabilityIndex(g);

      // assert that the provision is not enabled by checking that the request is not enabled
      assertFalse(index.isReachable(request, g.getInitialLevel()));
    }

  }

}
