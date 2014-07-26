/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Type;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.collectors.rating.Rating;
import vsr.cobalt.service.planner.DefaultPlanRater;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.InteractionMaker.aMinimalInteraction;
import static vsr.cobalt.models.makers.InteractionMaker.anInteraction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.TypeMaker.aType;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class DefaultPlanRaterTest {

  @Test
  public void returnNullWhenAnyPropertyProvisionUsesPropertiesOfDifferingName() {
    final Functionality f = make(aMinimalFunctionality());

    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));

    final Action a1 = make(aMinimalAction()
        .withFunctionality(f)
        .withPre(aPropositionSet()
            .withFilled(p1)));

    final Action a2 = make(aMinimalAction()
        .withEffects(aPropositionSet()
        .withFilled(p2)));

    final Plan p = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a1)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withProvision(aPropertyProvision()
                    .withRequest(p1)
                    .withOffer(p2)
                    .withProvidingAction(a2))))));

    final Repository r = mock(Repository.class);

    final DefaultPlanRater pr = new DefaultPlanRater(r);
    assertNull(pr.rate(p));
  }

  @Test
  public void allowInitialLevelActionsWithoutInteractions() {
    final Functionality f = make(aMinimalFunctionality());

    final Action a = make(aMinimalAction().withFunctionality(f));

    final Plan p = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a)))));

    final Repository r = mock(Repository.class);

    final DefaultPlanRater pr = new DefaultPlanRater(r);
    assertNotNull(pr.rate(p));
  }

  @Test
  public void returnNullWhenAnyExtensionLevelActionHasNoInteractions() {
    final Functionality f = make(aMinimalFunctionality());

    final Property p = make(aMinimalProperty());

    final Action a1 = make(aMinimalAction()
        .withFunctionality(f)
        .withPre(aPropositionSet()
            .withCleared(p)));

    final Action a2 = make(aMinimalAction()
        .withEffects(aPropositionSet()
            .withCleared(p)));

    final Plan pl = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a1)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withPrecursor(a2)))));

    final Repository r = mock(Repository.class);

    final DefaultPlanRater pr = new DefaultPlanRater(r);
    assertNull(pr.rate(pl));
  }

  @Test
  public void favorFunctionalityProvisionsWithMoreConcreteFunctionality() {
    final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
    final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

    final Action a1 = make(aMinimalAction()
        .withFunctionality(f1)
        .withInteraction(aMinimalInteraction()));

    final Action a2 = make(aMinimalAction()
        .withFunctionality(f2)
        .withInteraction(aMinimalInteraction()));

    final Plan p1 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f1)
                .withOffer(f1)
                .withProvidingAction(a1)))));

    final Plan p2 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f1)
                .withOffer(f2)
                .withProvidingAction(a2)))));

    final Repository r = mock(Repository.class);
    when(r.getDistance(f1, f1)).thenReturn(0);
    when(r.getDistance(f1, f2)).thenReturn(1);

    final DefaultPlanRater pr = new DefaultPlanRater(r);

    final Rating r1 = pr.rate(p1);
    final Rating r2 = pr.rate(p2);

    assertEquals(r1.compareTo(r2), -1);
  }

  @Test
  public void favorPropertyProvisionsWithMoreConcreteType() {
    final Functionality f = make(aMinimalFunctionality());

    final Type y1 = make(aType().withIdentifier("y1"));
    final Type y2 = make(aType().withIdentifier("y2"));

    final Property p1 = make(aMinimalProperty().withType(y1));
    final Property p2 = make(aMinimalProperty().withType(y2));

    final Action a1 = make(aMinimalAction()
        .withFunctionality(f)
        .withInteraction(aMinimalInteraction())
        .withPre(aPropositionSet()
            .withFilled(p1)));

    final Action a2 = make(aMinimalAction()
        .withEffects(aPropositionSet()
            .withFilled(p1))
        .withInteraction(aMinimalInteraction()));

    final Action a3 = make(aMinimalAction()
        .withEffects(aPropositionSet()
            .withFilled(p2))
        .withInteraction(aMinimalInteraction()));

    final Plan pl1 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a1)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withProvision(aPropertyProvision()
                    .withRequest(p1)
                    .withOffer(p1)
                    .withProvidingAction(a2))))));

    final Plan pl2 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a1)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withProvision(aPropertyProvision()
                    .withRequest(p1)
                    .withOffer(p2)
                    .withProvidingAction(a3))))));

    final Repository r = mock(Repository.class);
    when(r.getDistance(y1, y1)).thenReturn(0);
    when(r.getDistance(y1, y2)).thenReturn(1);

    final DefaultPlanRater pr = new DefaultPlanRater(r);

    final Rating r1 = pr.rate(pl1);
    final Rating r2 = pr.rate(pl2);

    assertEquals(r1.compareTo(r2), -1);
  }

  @Test
  public void favorLessInteractions() {
    final Functionality f = make(aMinimalFunctionality());

    final Interaction i1 = make(anInteraction().withInstruction("i1"));
    final Interaction i2 = make(anInteraction().withInstruction("i2"));
    final Interaction i3 = make(anInteraction().withInstruction("i3"));

    final Action a1 = make(aMinimalAction()
        .withFunctionality(f)
        .withInteraction(i1));

    final Action a2 = make(aMinimalAction()
        .withFunctionality(f)
        .withInteraction(i2, i3));

    final Plan p1 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a1)))));

    final Plan p2 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a2)))));

    final Repository r = mock(Repository.class);

    final DefaultPlanRater pr = new DefaultPlanRater(r);

    final Rating r1 = pr.rate(p1);
    final Rating r2 = pr.rate(p2);

    assertEquals(r1.compareTo(r2), -1);
  }

  @Test
  public void favorLessActions() {
    final Functionality f1 = make(aMinimalFunctionality().withIdentifier("f1"));
    final Functionality f2 = make(aMinimalFunctionality().withIdentifier("f2"));

    final Action a1 = make(aMinimalAction()
        .withFunctionality(f1));

    final Action a2 = make(aMinimalAction()
        .withFunctionality(f2));

    final Action a3 = Action.compose(a1, a2);

    final Plan p1 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f1)
                .withOffer(f1)
                .withProvidingAction(a1))
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f2)
                .withOffer(f2)
                .withProvidingAction(a2)))));

    final Plan p2 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f1)
                .withOffer(f1)
                .withProvidingAction(a3))
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f2)
                .withOffer(f2)
                .withProvidingAction(a3)))));

    final Repository r = mock(Repository.class);

    final DefaultPlanRater pr = new DefaultPlanRater(r);

    final Rating r1 = pr.rate(p1);
    final Rating r2 = pr.rate(p2);

    assertEquals(r1.compareTo(r2), 1);
  }

}
