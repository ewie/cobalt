/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Type;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.collectors.rating.Rating;
import vsr.cobalt.service.planner.DefaultPlanRater;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.InteractionMaker.aMinimalInteraction;
import static vsr.cobalt.models.makers.InteractionMaker.anInteraction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.models.makers.TypeMaker.aType;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.planner.graph.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class DefaultPlanRaterTest {

  @Test
  public void returnNullWhenAnyPropertyProvisionUsesPropertiesOfDifferingName() {
    final Task t = make(aMinimalTask());

    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));

    final Action a1 = make(aMinimalAction()
        .withTask(t)
        .withPre(aPropositionSet()
            .withFilled(p1)));

    final Action a2 = make(aMinimalAction().withPub(p2));

    final Plan p = new Plan(make(aGraph()
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
                    .withOffer(p2)
                    .withProvidingAction(a2))))));

    final Repository r = mock(Repository.class);

    final DefaultPlanRater pr = new DefaultPlanRater(r);
    assertNull(pr.rate(p));
  }

  @Test
  public void returnNullWhenAnyActionHasNoInteractions() {
    final Task t = make(aMinimalTask());

    final Action a = make(aMinimalAction().withTask(t));

    final Plan p = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t)
                .withOffer(t)
                .withProvidingAction(a)))));

    final Repository r = mock(Repository.class);

    final DefaultPlanRater pr = new DefaultPlanRater(r);
    assertNull(pr.rate(p));
  }

  @Test
  public void favorTaskProvisionsWithMoreConcreteTask() {
    final Task t1 = make(aTask().withIdentifier("t1"));
    final Task t2 = make(aTask().withIdentifier("t2"));

    final Action a1 = make(aMinimalAction()
        .withTask(t1)
        .withInteraction(aMinimalInteraction()));

    final Action a2 = make(aMinimalAction()
        .withTask(t2)
        .withInteraction(aMinimalInteraction()));

    final Plan p1 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t1)
                .withOffer(t1)
                .withProvidingAction(a1)))));

    final Plan p2 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t1)
                .withOffer(t2)
                .withProvidingAction(a2)))));

    final Repository r = mock(Repository.class);
    when(r.getDistance(t1, t1)).thenReturn(0);
    when(r.getDistance(t1, t2)).thenReturn(1);

    final DefaultPlanRater pr = new DefaultPlanRater(r);

    final Rating r1 = pr.rate(p1);
    final Rating r2 = pr.rate(p2);

    assertEquals(r1.compareTo(r2), -1);
  }

  @Test
  public void favorPropertyProvisionsWithMoreConcreteType() {
    final Task t = make(aMinimalTask());

    final Type y1 = make(aType().withIdentifier("y1"));
    final Type y2 = make(aType().withIdentifier("y2"));

    final Property p1 = make(aMinimalProperty().withType(y1));
    final Property p2 = make(aMinimalProperty().withType(y2));

    final Action a1 = make(aMinimalAction()
        .withTask(t)
        .withInteraction(aMinimalInteraction())
        .withPre(aPropositionSet()
            .withFilled(p1)));

    final Action a2 = make(aMinimalAction()
        .withPub(p1)
        .withInteraction(aMinimalInteraction()));

    final Action a3 = make(aMinimalAction()
        .withPub(p2)
        .withInteraction(aMinimalInteraction()));

    final Plan pl1 = new Plan(make(aGraph()
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
                    .withProvidingAction(a2))))));

    final Plan pl2 = new Plan(make(aGraph()
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
    final Task t = make(aMinimalTask());

    final Interaction i1 = make(anInteraction().withInstruction("i1"));
    final Interaction i2 = make(anInteraction().withInstruction("i2"));
    final Interaction i3 = make(anInteraction().withInstruction("i3"));

    final Action a1 = make(aMinimalAction()
        .withTask(t)
        .withInteraction(i1));

    final Action a2 = make(aMinimalAction()
        .withTask(t)
        .withInteraction(i2, i3));

    final Plan p1 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t)
                .withOffer(t)
                .withProvidingAction(a1)))));

    final Plan p2 = new Plan(make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withTaskProvision(aTaskProvision()
                .withRequest(t)
                .withOffer(t)
                .withProvidingAction(a2)))));

    final Repository r = mock(Repository.class);

    final DefaultPlanRater pr = new DefaultPlanRater(r);

    final Rating r1 = pr.rate(p1);
    final Rating r2 = pr.rate(p2);

    assertEquals(r1.compareTo(r2), -1);
  }

}
