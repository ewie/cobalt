/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Objects;
import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.aMinimalActionProvision;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aMinimalPropertyProvision;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ActionProvisionTest {

  @Test
  public static class CreateWithPrecursor {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp =
            "expecting each requested property to be provided by only one property provision")
    public void rejectMultiplePropertyProvisionsForTheSameRequestedProperty() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w1 = make(aMinimalWidget()
          .withIdentifier("w1")
          .withPublic(p1));

      final Widget w2 = make(aMinimalWidget()
          .withIdentifier("w2")
          .withPublic(p1));

      final Action request = make(aMinimalAction()
          .withWidget(w1)
          .withPre(aPropositionSet()
              .withFilled(p1)
              .withCleared(p2)));

      final Action precursor = make(aMinimalAction()
          .withWidget(w1)
          .withEffects(aPropositionSet().withCleared(p2)));

      final Action a1 = make(aMinimalAction()
          .withWidget(w1)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w2)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final PropertyProvision pp1 = make(aPropertyProvision()
          .withProvidingAction(a1)
          .withOffer(p1)
          .withRequest(p1));

      final PropertyProvision pp2 = make(aPropertyProvision()
          .withProvidingAction(a2)
          .withOffer(p1)
          .withRequest(p1));

      ActionProvision.createWithPrecursor(request, precursor, setOf(pp1, pp2));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a satisfying precursor to the requested action")
    public void rejectPrecursorNotSatisfyingRequestedAction() {
      final Property p = make(aMinimalProperty());

      final Action request = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w1"))
          .withPre(aPropositionSet().withCleared(p)));

      final Action precursor = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w2"))
          .withEffects(aPropositionSet().withCleared(p)));

      ActionProvision.createWithPrecursor(request, precursor);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting the precursor to be required by requested action")
    public void rejectPrecursorWhenNotRequired() {
      final Action request = make(aMinimalAction());
      final Action precursor = make(aMinimalAction());

      ActionProvision.createWithPrecursor(request, precursor);
    }

    @Test
    public void acceptPrecursorWhenFillingRequiredProperties() {
      final Property p = make(aMinimalProperty());

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withFilled(p)));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet().withFilled(p)));

      ActionProvision.createWithPrecursor(request, precursor);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting all property provisions to use a property required filled by " +
            "requested action and not already provided by precursor")
    public void rejectPropertyProvisionWithRequestedPropertyNotRequiredByRequestedAction() {
      final Property p = make(aMinimalProperty());

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final PropertyProvision pp = make(aMinimalPropertyProvision());

      ActionProvision.createWithPrecursor(request, precursor, setOf(pp));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting all property provisions to use a property required filled by " +
            "requested action and not already provided by precursor")
    public void rejectPropertyProvisionWithRequestedPropertyAlreadyProvidedByPrecursor() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w1 = make(aMinimalWidget()
          .withIdentifier("w1"));

      final Widget w2 = make(aMinimalWidget()
          .withIdentifier("w2")
          .withPublic(p1));

      final Action request = make(aMinimalAction()
          .withWidget(w1)
          .withPre(aPropositionSet()
              .withFilled(p1)
              .withCleared(p2)));

      final Action precursor = make(aMinimalAction()
          .withWidget(w1)
          .withEffects(aPropositionSet()
              .withFilled(p1)
              .withCleared(p2)));

      final PropertyProvision pp = make(aPropertyProvision()
          .withProvidingAction(aMinimalAction()
              .withWidget(w2)
              .withEffects(aPropositionSet()
                  .withFilled(p1)))
          .withOffer(p1)
          .withRequest(p1));

      ActionProvision.createWithPrecursor(request, precursor, setOf(pp));
    }

    @Test
    public void preventModificationOfPropertyProvisions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p2));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      final Action precursor = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withCleared(p1)));

      final PropertyProvision pp = make(aPropertyProvision()
          .withProvidingAction(aMinimalAction()
              .withWidget(w)
              .withEffects(aPropositionSet()
                  .withFilled(p2)))
          .withOffer(p2)
          .withRequest(p2));

      final Set<PropertyProvision> pps = setOf(pp);

      final ActionProvision ap = ActionProvision.createWithPrecursor(request, precursor, pps);
      pps.add(null);

      assertNotEquals(ap.getPropertyProvisions(), pps);
    }

    @Test
    public void defaultToEmptySetOfPropertyProvisions() {
      final Property p = make(aMinimalProperty());

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final ActionProvision ap = ActionProvision.createWithPrecursor(request, precursor);

      assertEmpty(ap.getPropertyProvisions());
    }

  }

  @Test
  public static class CreateWithoutPrecursor {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp =
            "expecting each requested property to be provided by only one property provision")
    public void rejectMultiplePropertyProvisionsForTheSameRequestedProperty() {
      final Property p = make(aMinimalProperty());

      final Widget w1 = make(aMinimalWidget()
          .withIdentifier("w1")
          .withPublic(p));

      final Widget w2 = make(aMinimalWidget()
          .withIdentifier("w2")
          .withPublic(p));

      final Action request = make(aMinimalAction()
          .withWidget(w1)
          .withPre(aPropositionSet()
              .withFilled(p)));

      final Action a1 = make(aMinimalAction()
          .withWidget(w1)
          .withEffects(aPropositionSet()
              .withFilled(p)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w2)
          .withEffects(aPropositionSet()
              .withFilled(p)));

      final PropertyProvision pp1 = make(aPropertyProvision()
          .withProvidingAction(a1)
          .withOffer(p)
          .withRequest(p));

      final PropertyProvision pp2 = make(aPropertyProvision()
          .withProvidingAction(a2)
          .withOffer(p)
          .withRequest(p));

      ActionProvision.createWithoutPrecursor(request, setOf(pp1, pp2));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "requested action requires a precursor")
    public void rejectRequestedActionRequiringPrecursorWhenNoPrecursorGiven() {
      final Property p = make(aMinimalProperty());

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet()
              .withCleared(p)));

      ActionProvision.createWithoutPrecursor(request, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting one or more property provisions")
    public void rejectEmptySetOfPropertyProvisions() {
      final Action request = make(aMinimalAction());
      ActionProvision.createWithoutPrecursor(request, emptySet(PropertyProvision.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp =
            "expecting all property provisions to use a property required filled by requested action")
    public void rejectPropertyProvisionWithRequestedPropertyNotRequiredByRequestedAction() {
      final Action request = make(aMinimalAction());
      final PropertyProvision pp = make(aMinimalPropertyProvision());
      ActionProvision.createWithoutPrecursor(request, setOf(pp));
    }

    @Test
    public void preventModificationOfPropertyProvisions() {
      final Property p = make(aMinimalProperty());

      final Widget w = make(aMinimalWidget().withPublic(p));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet()
              .withFilled(p)));

      final PropertyProvision pp = make(aPropertyProvision()
          .withProvidingAction(aMinimalAction()
              .withWidget(w)
              .withEffects(aPropositionSet().withFilled(p)))
          .withOffer(p)
          .withRequest(p));

      final Set<PropertyProvision> pps = setOf(pp);

      final ActionProvision ap = ActionProvision.createWithoutPrecursor(request, pps);
      pps.add(null);

      assertNotEquals(ap.getPropertyProvisions(), pps);
    }

  }

  @Test
  public static class GetProvidingActions {

    @Test
    public void returnProvidingActions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet().withFilled(p1, p2)));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withProvision(aPropertyProvision()
              .withProvidingAction(a1)
              .withOffer(p1)
              .withRequest(p1))
          .withProvision(aPropertyProvision()
              .withProvidingAction(a2)
              .withOffer(p2)
              .withRequest(p2)));

      assertEquals(ap.getProvidingActions(), setOf(a1, a2));
    }

  }

  @Test
  public static class GetRequiredActions {

    @Test
    public void includeProvidingActions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      final Action provider1 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Action provider2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet().withFilled(p1, p2)));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withProvision(aPropertyProvision()
              .withProvidingAction(provider1)
              .withOffer(p1)
              .withRequest(p1))
          .withProvision(aPropertyProvision()
              .withProvidingAction(provider2)
              .withOffer(p2)
              .withRequest(p2)));

      assertEquals(ap.getRequiredActions(), setOf(provider1, provider2));
    }

    @Test
    public void includePrecursorActionWhenGiven() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p2));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      final Action precursor = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withCleared(p1)));

      final Action provider = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withPrecursor(precursor)
          .withProvision(aPropertyProvision()
              .withProvidingAction(provider)
              .withOffer(p2)
              .withRequest(p2)));

      assertEquals(ap.getRequiredActions(), setOf(precursor, provider));
    }

  }

  @Test
  public static class GetRequestedProperties {

    @Test
    public void returnRequestedProperties() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      final Action request = make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet().withFilled(p1, p2)));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(request)
          .withProvision(aPropertyProvision()
              .withProvidingAction(aMinimalAction()
                  .withWidget(w)
                  .withEffects(aPropositionSet()
                      .withFilled(p1)))
              .withOffer(p1)
              .withRequest(p1))
          .withProvision(aPropertyProvision()
              .withProvidingAction(aMinimalAction()
                  .withWidget(w)
                  .withEffects(aPropositionSet()
                      .withFilled(p2)))
              .withOffer(p2)
              .withRequest(p2)));

      assertEquals(ap.getRequestedProperties(), setOf(p1, p2));
    }

  }

  @Test
  public static class Equality {

    @Test
    public void calculateHashCode() {
      final ActionProvision ap = make(aMinimalActionProvision());
      final int hashCode = Objects.hash(ap.getRequestedAction(), ap.getPrecursorAction(), ap.getPropertyProvisions());
      assertEquals(ap.hashCode(), hashCode);
    }

    @Test
    public void notEqualWhenComparedWithNonActionProvision() {
      final ActionProvision ap = make(aMinimalActionProvision());
      final Object x = new Object();
      assertNotEquals(ap, x);
    }

    @Test
    public void notEqualWhenRequestedActionDiffers() {
      final Property p = make(aMinimalProperty().withName("p"));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final ActionProvision ap1 = make(aMinimalActionProvision()
          .withPrecursor(precursor)
          .withRequest(aMinimalAction()
              .withPre(aPropositionSet().withCleared(p))));

      final ActionProvision ap2 = make(aMinimalActionProvision()
          .withPrecursor(precursor)
          .withRequest(aMinimalAction()
              // use a dummy functionality to create a distinct requested action
              .withFunctionality(aMinimalFunctionality())
              .withPre(aPropositionSet().withCleared(p))));

      assertNotEquals(ap1, ap2);
    }

    @Test
    public void notEqualWhenPrecursorActionDiffers() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action request = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1)));

      final ActionProvision ap1 = make(aMinimalActionProvision()
          .withRequest(request)
          .withPrecursor(aMinimalAction()
              .withEffects(aPropositionSet().withCleared(p1))));

      final ActionProvision ap2 = make(aMinimalActionProvision()
          .withRequest(request)
          .withPrecursor(aMinimalAction()
              .withEffects(aPropositionSet()
                  .withCleared(p1)
                  .withCleared(p2))));

      assertNotEquals(ap1, ap2);
    }

    @Test
    public void notEqualWhenPropertyProvisionsDiffer() throws Exception {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w1 = make(aMinimalWidget()
          .withIdentifier("w1")
          .withPublic(p2));

      final Widget w2 = make(aMinimalWidget()
          .withIdentifier("w2")
          .withPublic(p2));

      final Action request = make(aMinimalAction()
          .withWidget(w1)
          .withPre(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      final Action precursor = make(aMinimalAction()
          .withWidget(w1)
          .withEffects(aPropositionSet().withCleared(p1)));

      final ActionProvision ap1 = make(aMinimalActionProvision()
          .withRequest(request)
          .withPrecursor(precursor)
          .withProvision(aPropertyProvision()
              .withProvidingAction(aMinimalAction()
                  .withWidget(w1)
                  .withEffects(aPropositionSet()
                      .withFilled(p2)))
              .withOffer(p2)
              .withRequest(p2)));

      final ActionProvision ap2 = make(aMinimalActionProvision()
          .withRequest(request)
          .withPrecursor(precursor)
          .withProvision(aPropertyProvision()
              .withProvidingAction(aMinimalAction()
                  .withWidget(w2)
                  .withEffects(aPropositionSet()
                      .withFilled(p2)))
              .withOffer(p2)
              .withRequest(p2)));

      assertNotEquals(ap1, ap2);
    }

    @Test
    public void equal() {
      final ActionProvision ap1 = make(aMinimalActionProvision());
      final ActionProvision ap2 = make(aMinimalActionProvision());
      assertEquals(ap1, ap2);
    }

  }

}
