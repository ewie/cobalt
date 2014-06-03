/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.Task;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.testing.makers.EffectSetMaker.anEffectSet;
import static vsr.cobalt.testing.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.testing.makers.GraphMaker.aGraph;
import static vsr.cobalt.testing.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aTaskProvision;

@Test
public class PathWalkingCyclicDependencyDetectorTest {

  @Test
  public void detectCycleWhenBothActionsAreTheSame() {
    final Task t = make(aMinimalTask());

    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));

    final Action a1 = make(aMinimalAction()
        .withTask(t)
        .withPre(aPropositionSet()
            .withCleared(p1)));

    final Action a2 = Action.compose(a1,
        make(aMinimalAction()
            .withPre(aPropositionSet()
                .withCleared(p2))));

    final Graph g = make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t)
                .withOffer(t)
                .withProvidingAction(a1))));

    final PathWalkingCyclicDependencyDetector cdd = new PathWalkingCyclicDependencyDetector();

    assertTrue(cdd.createsCyclicDependencyVia(a2, a1, g));
  }

  @Test
  public void detectCycleWhenPathFromDependentActionToTestedActionViaPrecursorExists() {
    final Task t = make(aMinimalTask());

    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));

    final Action a1 = make(aMinimalAction()
        .withTask(t)
        .withPre(aPropositionSet()
            .withCleared(p1)));

    final Action a2 = make(aMinimalAction()
        .withEffects(anEffectSet()
            .withToClear(p1)));

    final Action a3 = Action.compose(a1,
        make(aMinimalAction()
            .withPre(aPropositionSet()
                .withCleared(p2))));

    final Graph g = make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t)
                .withOffer(t)
                .withProvidingAction(a1)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withPrecursor(a2))));

    final PathWalkingCyclicDependencyDetector cdd = new PathWalkingCyclicDependencyDetector();

    assertTrue(cdd.createsCyclicDependencyVia(a3, a2, g));
  }

  @Test
  public void detectCycleWhenPathFromDependentActionToTestedActionViaPropertyProvisionExists() {
    final Task t = make(aMinimalTask());

    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));

    final Action a1 = make(aMinimalAction()
        .withTask(t)
        .withPre(aPropositionSet()
            .withFilled(p1)));

    final Action a2 = make(aMinimalAction().withPub(p1));

    final Action a3 = Action.compose(a1,
        make(aMinimalAction()
            .withPre(aPropositionSet()
                .withCleared(p2))));

    final Graph g = make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t)
                .withOffer(t)
                .withProvidingAction(a1)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withProvision(aPropertyProvision()
                    .withRequest(p1)
                    .withOffer(p1)
                    .withProvidingAction(a2)))));

    final PathWalkingCyclicDependencyDetector cdd = new PathWalkingCyclicDependencyDetector();

    assertTrue(cdd.createsCyclicDependencyVia(a3, a2, g));
  }

  @Test
  public void doNotDetectCycleWhenNoPathFromDependentActionToTestedActionExists() {
    final Task t = make(aMinimalTask());

    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));
    final Property p3 = make(aMinimalProperty().withName("p3"));

    final Action a1 = make(aMinimalAction()
        .withTask(t)
        .withPre(aPropositionSet()
            .withCleared(p1)));

    final Action a2 = make(aMinimalAction()
        .withEffects(anEffectSet()
            .withToClear(p1)));

    final Action a3 = make(aMinimalAction()
        .withEffects(anEffectSet()
            .withToClear(p1))
        .withPre(aPropositionSet()
            .withCleared(p2)));

    final Action a4 = make(aMinimalAction()
        .withEffects(anEffectSet()
            .withToClear(p2))
        .withPre(aPropositionSet()
            .withCleared(p3)));

    final Action a5 = Action.compose(a2,
        make(aMinimalAction()
            .withEffects(anEffectSet()
                .withToClear(p3))));

    final Graph g = make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t)
                .withOffer(t)
                .withProvidingAction(a1)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withPrecursor(a2))
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withPrecursor(a3)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a3)
                .withPrecursor(a4))));

    final PathWalkingCyclicDependencyDetector cdd = new PathWalkingCyclicDependencyDetector();

    assertFalse(cdd.createsCyclicDependencyVia(a5, a4, g));
  }

}
