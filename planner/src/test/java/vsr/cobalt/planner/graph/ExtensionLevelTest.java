/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropertyMaker.aProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.aMinimalActionProvision;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ExtensionLevelTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting one or more provisions")
    public void rejectEmptySet() {
      new ExtensionLevel(emptySet(ActionProvision.class));
    }

    @Test
    public void preventModificationOfActionProvisions() {
      final ActionProvision ap = make(aMinimalActionProvision());
      final Set<ActionProvision> aps = setOf(ap);
      final ExtensionLevel xl = new ExtensionLevel(aps);
      aps.add(null);
      assertNotEquals(xl.getActionProvisions(), aps);
    }

  }

  @Test
  public static class GetActionProvisions {

    @Test
    public void returnAllActionProvisions() {
      final ActionProvision ap = make(aMinimalActionProvision());
      final ExtensionLevel extensionLevel = make(anExtensionLevel().withProvision(ap));
      assertEquals(extensionLevel.getActionProvisions(), setOf(ap));
    }

  }

  @Test
  public static class GetRequiredActions {

    @Test
    public void includeAllPrecursorActions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1)));

      final Action precursor1 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1)));

      final Action precursor2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1, p2)));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(request)
          .withPrecursor(precursor1));

      final ActionProvision ap2 = make(anActionProvision()
          .withRequest(request)
          .withPrecursor(precursor2));

      final ExtensionLevel extensionLevel = make(anExtensionLevel().withProvision(ap1, ap2));

      assertEquals(extensionLevel.getRequiredActions(), setOf(precursor1, precursor2));
    }

    @Test
    public void includeAllProvidingActions() {
      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p2));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      final Action precursor = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet().withCleared(p1)));

      final Action provider = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      final ActionProvision ap = make(anActionProvision()
          .withPrecursor(precursor)
          .withRequest(request)
          .withProvision(aPropertyProvision()
              .withProvidingAction(provider)
              .withRequest(p2)
              .withOffer(p2)));

      final ExtensionLevel extensionLevel = make(anExtensionLevel().withProvision(ap));

      assertEquals(extensionLevel.getRequiredActions(), setOf(precursor, provider));
    }

    @Test
    public void handlePrecursorLessActionProvisions() {
      final Property p = make(aMinimalProperty());

      final Widget w = make(aMinimalWidget().withPublic(p));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet().withFilled(p)));

      final Action provider = make(aMinimalAction()
          .withWidget(w)
          .withWidget(aMinimalWidget().withPublic(p))
          .withEffects(aPropositionSet()
              .withFilled(p)));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withProvision(aPropertyProvision()
              .withProvidingAction(provider)
              .withRequest(p)
              .withOffer(p)));

      final ExtensionLevel extensionLevel = make(anExtensionLevel().withProvision(ap));

      assertEquals(extensionLevel.getRequiredActions(), setOf(provider));
    }

  }

  @Test
  public static class GetRequestedActions {

    @Test
    public void returnAllRequestedActions() {
      final Property p1 = make(aProperty().withName("p1"));
      final Property p2 = make(aProperty().withName("p2"));

      final Action request1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1)));

      final Action request2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p2)));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1, p2)));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(request1)
          .withPrecursor(precursor));

      final ActionProvision ap2 = make(anActionProvision()
          .withRequest(request2)
          .withPrecursor(precursor));

      final ExtensionLevel extensionLevel = make(anExtensionLevel()
          .withProvision(ap1, ap2));

      assertEquals(extensionLevel.getRequestedActions(), setOf(ap1.getRequestedAction(), ap2.getRequestedAction()));
    }

  }

  @Test
  public static class CanExtendOn {

    @Test
    public void returnTrueWhenAllRequestedActionsAreSubsetOfOtherPrecursorActions() {
      final Property p = make(aMinimalProperty());

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(request)
              .withPrecursor(precursor)));

      final Level l = mock(Level.class);
      when(l.getRequiredActions()).thenReturn(setOf(request));

      assertTrue(xl.canExtendOn(l));
    }

    @Test
    public void returnFalseWhenAnyRequestedActionIsNotMemberOfOtherPrecursorActions() {
      final Property p = make(aMinimalProperty());

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(request)
              .withPrecursor(precursor)));

      final Level l = mock(Level.class);
      when(l.getRequiredActions()).thenReturn(setOf(make(aMinimalAction())));

      assertFalse(xl.canExtendOn(l));
    }

  }

  @Test
  public static class GetActionProvisionsByRequestedAction {

    @Test
    public void returnAllActionProvisionsHavingTheGivenRequestedAction() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1)));

      final Action precursor1 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1)));

      final Action precursor2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1, p2)));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(request)
          .withPrecursor(precursor1));

      final ActionProvision ap2 = make(anActionProvision()
          .withRequest(request)
          .withPrecursor(precursor2));

      final ExtensionLevel extensionLevel = make(anExtensionLevel().withProvision(ap1, ap2));

      assertEquals(extensionLevel.getActionProvisionsByRequestedAction(request), setOf(ap1, ap2));
    }

    @Test
    public void excludeActionProvisionsNotHavingTheGivenRequestedAction() {
      final Property p = make(aMinimalProperty().withName("p"));

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final ActionProvision ap = make(anActionProvision()
          .withPrecursor(precursor)
          .withRequest(request));

      final ExtensionLevel extensionLevel = make(anExtensionLevel().withProvision(ap));

      final Action targetX = make(aMinimalAction());

      assertEmpty(extensionLevel.getActionProvisionsByRequestedAction(targetX));
    }

    @Test
    public void returnEmptySetWhenNoActionProvisionHasTheGivenRequestedAction() {
      final ExtensionLevel extensionLevel = make(anExtensionLevel()
          .withProvision(aMinimalActionProvision()));

      final Action a = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w")));

      assertEmpty(extensionLevel.getActionProvisionsByRequestedAction(a));
    }

  }

  @Test
  public static class Equality {

    @Test
    public void useHashCodeOfActionProvisionSet() {
      final ExtensionLevel xl = new ExtensionLevel(setOf(make(aMinimalActionProvision())));
      assertEquals(xl.hashCode(), xl.getActionProvisions().hashCode());
    }

    @Test
    public void equalWhenActionProvisionSetsAreEqual() {
      final ExtensionLevel xl1 = new ExtensionLevel(setOf(make(aMinimalActionProvision())));
      final ExtensionLevel xl2 = new ExtensionLevel(setOf(make(aMinimalActionProvision())));
      assertEquals(xl1, xl2);
    }

    @Test
    public void notEqualWhenComparedWithNonExtensionLevel() {
      final ExtensionLevel xl = new ExtensionLevel(setOf(make(aMinimalActionProvision())));
      final Object x = new Object();
      assertNotEquals(xl, x);
    }

    @Test
    public void notEqualWhenActionProvisionSetsDiffer() {
      final Property p = make(aMinimalProperty().withName("p"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a2));

      final ActionProvision ap2 = make(aMinimalActionProvision());

      final ExtensionLevel xl1 = new ExtensionLevel(setOf(ap1));
      final ExtensionLevel xl2 = new ExtensionLevel(setOf(ap2));

      assertNotEquals(xl1, xl2);
    }

  }

}
