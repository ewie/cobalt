/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.InteractionMaker.aMinimalInteraction;
import static vsr.cobalt.models.makers.InteractionMaker.anInteraction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ActionTest {

  @Test
  public static class Create {

    @Test
    public void preventModificationOfRealizedFunctionalities() {
      final Widget w = make(aMinimalWidget());
      final Property p = make(aMinimalProperty());
      final PropositionSet pre = make(aPropositionSet().withCleared(p));
      final PropositionSet ef = make(aPropositionSet().withFilled(p));
      final Functionality f = make(aMinimalFunctionality());
      final Set<Functionality> fs = setOf(f);
      final Action a = Action.create(w, pre, ef, fs);
      fs.add(null);
      assertNotEquals(a.getRealizedFunctionalities(), fs);
    }

    @Test
    public void preventModificationOfInteractions() {
      final Widget w = make(aMinimalWidget());
      final Property p = make(aMinimalProperty());
      final PropositionSet pre = make(aPropositionSet().withCleared(p));
      final PropositionSet ef = make(aPropositionSet().withFilled(p));
      final Set<Functionality> fs = emptySet();
      final Interaction i = make(aMinimalInteraction());
      final Set<Interaction> is = setOf(i);
      final Action a = Action.create(w, pre, ef, fs, is);
      is.add(null);
      assertNotEquals(a.getInteractions(), is);
    }

    @Test
    public void derivePostConditionsFromPreConditionsAndEffects() {
      final Widget w = make(aMinimalWidget());
      final Property p = make(aMinimalProperty());
      final PropositionSet pre = make(aPropositionSet().withCleared(p));
      final PropositionSet ef = make(aPropositionSet().withFilled(p));
      final Action a = Action.create(w, pre, ef);
      assertEquals(a.getPostConditions(), ef.createPostConditions(pre));
    }

    @Test
    public void defaultToEmptyPreConditions() {
      final Widget w = make(aMinimalWidget());
      final Action a = Action.create(w);
      assertEquals(a.getPreConditions(), PropositionSet.empty());
    }

    @Test
    public void defaultToEmptyEffects() {
      final Widget w = make(aMinimalWidget());
      final Action a = Action.create(w);
      assertEquals(a.getEffects(), PropositionSet.empty());
    }

    @Test
    public void defaultToEmptyRealizedFunctionalities() {
      final Widget w = make(aMinimalWidget());
      final Action a = Action.create(w);
      assertEmpty(a.getRealizedFunctionalities());
    }

    @Test
    public void defaultToEmptyInteractions() {
      final Widget w = make(aMinimalWidget());
      final Action a = Action.create(w);
      assertEmpty(a.getInteractions());
    }

  }

  @Test
  public static class Compose {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting one or more actions")
    public void rejectEmptyActionCollection() {
      Action.compose(emptySet(Action.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting composable actions")
    public void rejectActionsNotComposable() {
      final Action a1 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w1")));
      final Action a2 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w2")));
      Action.compose(a1, a2);
    }

    @Test
    public void createCompositeAction() {
      final Action a1 = make(aMinimalAction());
      final Action a2 = make(aMinimalAction());

      final Action a3 = Action.compose(a1, a2);

      final PropositionSet pre = make(aPropositionSet()
          .withCleared(Sets.union(a1.getPreConditions().getClearedProperties(),
              a2.getPreConditions().getClearedProperties()))
          .withFilled(Sets.union(a1.getPreConditions().getFilledProperties(),
              a2.getPreConditions().getFilledProperties())));

      final PropositionSet eff = make(aPropositionSet()
          .withCleared(Sets.union(a1.getEffects().getClearedProperties(),
              a2.getEffects().getClearedProperties()))
          .withFilled(Sets.union(a1.getEffects().getFilledProperties(),
              a2.getEffects().getFilledProperties())));

      assertEquals(a3.getWidget(), a1.getWidget());

      assertEquals(a3.getRealizedFunctionalities(),
          Sets.union(a1.getRealizedFunctionalities(), a2.getRealizedFunctionalities()));

      assertEquals(a3.getInteractions(),
          Sets.union(a1.getInteractions(), a2.getInteractions()));

      assertEquals(a3.getPreConditions(), pre);
      assertEquals(a3.getEffects(), eff);
    }

    @Test
    public void returnSameActionWhenCalledWithOnlyOneAction() {
      final Action a = make(aMinimalAction());
      assertSame(Action.compose(setOf(a)), a);
    }

  }

  @Test
  public static class IsComposable {

    @Test
    public void returnFalseWhenWidgetsDiffer() {
      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w1"))
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w2"))
          .withPre(aPropositionSet().withFilled(p)));

      assertFalse(Action.isComposable(asList(a1, a2)));
      assertFalse(Action.isComposable(asList(a2, a1)));
    }

    @Test
    public void returnFalseWithEmptyCollection() {
      assertFalse(Action.isComposable(emptySet(Action.class)));
    }

    @Test
    public void returnTrueWithJustOneAction() {
      final Property p = make(aMinimalProperty());

      final Action a = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p))
          .withEffects(aPropositionSet().withFilled(p)));

      assertTrue(Action.isComposable(asList(a)));
    }

    @Test
    public void ignoreDuplicateActions() {
      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p))
          .withEffects(aPropositionSet().withFilled(p)));

      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p))
          .withEffects(aPropositionSet().withFilled(p)));

      assertTrue(Action.isComposable(asList(a1, a2)));
    }

    @Test
    public void returnTrueWhenNoActionPairMutex() {
      final Action a1 = make(aMinimalAction());
      final Action a2 = make(aMinimalAction());

      assertTrue(Action.isComposable(asList(a1, a2)));
      assertTrue(Action.isComposable(asList(a2, a1)));
    }

    @Test
    public void returnFalseOnCompetingNeeds() {
      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withFilled(p))
          .withEffects(aPropositionSet().withCleared(p)));

      assertFalse(Action.isComposable(asList(a1, a2)));
      assertFalse(Action.isComposable(asList(a2, a1)));
    }

    @Test
    public void returnFalseOnInconsistentEffect() {
      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withFilled(p)));

      assertFalse(Action.isComposable(asList(a1, a2)));
      assertFalse(Action.isComposable(asList(a2, a1)));
    }

    @Test
    public void returnFalseOnInterference() {
      final Property p = make(aMinimalProperty().withName("p"));

      final Action a1 = make(aMinimalAction()
          .withEffects(aPropositionSet().withFilled(p)));

      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p))
          .withEffects(aPropositionSet().withFilled(p)));

      assertFalse(Action.isComposable(asList(a1, a2)));
      assertFalse(Action.isComposable(asList(a2, a1)));
    }

  }

  @Test
  public static class ComputingGetters {

    private Action action;

    private Property property;

    private PropositionSet pre;

    private PropositionSet effects;

    @BeforeMethod
    public void setUp() {
      final Widget widget = make(aMinimalWidget());

      property = make(aMinimalProperty());
      pre = make(aPropositionSet().withCleared(property));
      effects = make(aPropositionSet().withFilled(property));

      action = Action.create(widget, pre, effects);
    }

    @Test
    public void getPostConditions() {
      assertEquals(action.getPostConditions(), effects.createPostConditions(pre));
    }

    @Test
    public void getPublishedProperties() {
      assertEquals(action.getPublishedProperties(), setOf(property));
    }

  }

  @Test
  public static class Represents {

    @Test
    public void returnTrueWhenAtomicCalledWithEqualAction() {
      final Action a1 = make(aMinimalAction());
      final Action a2 = make(aMinimalAction());
      assertTrue(a1.represents(a2));
    }

    @Test
    public void returnFalseWhenAtomicCalledWithDifferentAction() {
      final Action a1 = make(aMinimalAction());
      final Action a2 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w")));
      assertFalse(a1.represents(a2));
    }

    @Test
    public void returnTrueWhenCompositeCalledWithSameAction() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = Action.compose(a1, a2);

      assertTrue(a3.represents(a3));
    }

    @Test
    public void returnTrueWhenCompositeCalledWithConstituentAction() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = Action.compose(a1, a2);

      assertTrue(a3.represents(a1));
    }

    @Test
    public void returnTrueWhenCompositeCalledWithTransitiveConstituentAction() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final Property p3 = make(aMinimalProperty().withName("p3"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = Action.compose(a1, a2);

      final Action a4 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p3)));

      final Action a5 = Action.compose(a3, a4);

      assertTrue(a5.represents(a1));
    }

    @Test
    public void returnFalseWhenCompositeCalledWithNonConstituentAction() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = Action.compose(a1, a2);

      final Action a4 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w")));

      assertFalse(a3.represents(a4));
    }

  }

  @Test
  public static class IsEnabled {

    @Test
    public void returnTrueWhenPreConditionsEmpty() {
      final Action a = make(aMinimalAction());
      assertTrue(a.isEnabled());
    }

    @Test
    public void returnFalseWhenPreConditionsNotEmpty() {
      final Action a = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(aMinimalProperty())));
      assertFalse(a.isEnabled());
    }

  }

  @Test
  public static class IsMaintenance {

    @Test
    public void returnFalseWhenThereAreRealizedFunctionalities() {
      final Action a = make(aMinimalAction()
          .withFunctionality(aMinimalFunctionality()));
      assertFalse(a.isMaintenance());
    }

    @Test
    public void returnFalseWhenThereAreInteractions() {
      final Action a = make(aMinimalAction()
          .withInteraction(aMinimalInteraction()));
      assertFalse(a.isMaintenance());
    }

    @Test
    public void returnFalseWhenPreAndPostConditionsDiffer() {
      final Action a = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(aMinimalProperty())));
      assertFalse(a.isMaintenance());
    }

    @Test
    public void returnTrueWhenAllPostConditionsArePreConditions() {
      final Action a = make(aMinimalAction()
          .withPre(aPropositionSet()));
      assertTrue(a.isMaintenance());
    }

  }

  @Test
  public static class Publishes {

    @Test
    public void returnTrueWhenTheGivenPropertyIsPublished() {
      final Property p = make(aMinimalProperty());
      final Action a = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withFilled(p)));
      assertTrue(a.publishes(p));
    }

    @Test
    public void returnFalseWhenTheGivenPropertyIsNotPublished() {
      final Property p = make(aMinimalProperty());
      final Action a = make(aMinimalAction());
      assertFalse(a.publishes(p));
    }

  }

  @Test
  public static class Realizes {

    @Test
    public void returnTrueWhenTheGivenFunctionalityIsRealized() {
      final Functionality f = make(aMinimalFunctionality());
      final Action a = make(aMinimalAction().withFunctionality(f));
      assertTrue(a.realizes(f));
    }

    @Test
    public void returnFalseWhenTheGivenFunctionalityIsNotRealized() {
      final Functionality f = make(aMinimalFunctionality());
      final Action a = make(aMinimalAction());
      assertFalse(a.realizes(f));
    }

  }

  @Test
  public static class CanBePrecursorOf {

    @Test
    public void returnFalseWhenWidgetDiffers() {
      final Action source = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w1")));

      final Action target = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w2")));

      assertFalse(source.canBePrecursorOf(target));
    }

    @Test
    public void returnFalseWhenSourceDoesNotClearAllPropertiesRequiredClearedByTarget() {
      final Property p = make(aMinimalProperty());

      final Action source = make(aMinimalAction());

      final Action target = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      assertFalse(source.canBePrecursorOf(target));
    }

    @Test
    public void returnTrueWhenSourceClearsAllPropertiesRequiredClearedByTarget() {
      final Property p = make(aMinimalProperty());

      final Action source = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final Action target = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      assertTrue(source.canBePrecursorOf(target));
    }

  }

  @Test
  public static class RequiresPrecursor {

    @Test
    public void returnFalseWhenPreConditionsAreEmpty() {
      final Action action = make(aMinimalAction());
      assertFalse(action.requiresPrecursor());
    }

    @Test
    public void returnFalseWhenPreConditionsRequireOnlyFilledProperties() {
      final Property p = make(aMinimalProperty());

      final Action action = make(aMinimalAction()
          .withPre(aPropositionSet().withFilled(p)));

      assertFalse(action.requiresPrecursor());
    }

    @Test
    public void returnTrueWhenPreConditionsRequireClearedProperties() {
      final Property p = make(aMinimalProperty());

      final Action action = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      assertTrue(action.requiresPrecursor());
    }

  }

  @Test
  public static class GetFilledPropertiesNotSatisfiedByPrecursor {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting an action of the same widget")
    public void rejectActionOfDifferentWidget() {
      final Action target = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w1")));

      final Action precursor = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w2")));

      target.getFilledPropertiesNotSatisfiedByPrecursor(precursor);
    }

    @Test
    public void returnAllUnsatisfiedPropertiesRequiredFilled() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action target = make(aMinimalAction()
          .withPre(aPropositionSet()
              .withCleared(p2)
              .withFilled(p1)));

      final Action precursor = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p1, p2)));

      final Set<Property> ps = target.getFilledPropertiesNotSatisfiedByPrecursor(precursor);

      assertEquals(ps, setOf(p1));
    }

  }

  @Test
  public static class Equality {

    @Test
    public void calculateHashCode() {
      final Action a = make(aMinimalAction());
      final int h = Objects.hash(
          a.getWidget(),
          a.getPreConditions(),
          a.getEffects(),
          a.getRealizedFunctionalities(),
          a.getInteractions());
      assertEquals(a.hashCode(), h);
    }

    @Test
    public void notEqualWhenComparedWithNonAction() {
      final Action a = make(aMinimalAction());
      final Object x = new Object();
      assertNotEquals(a, x);
    }

    @Test
    public void notEqualWhenWidgetDiffers() {
      final Action a1 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w1")));

      final Action a2 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w2")));

      assertNotEquals(a1, a2);
    }

    @Test
    public void notEqualWhenPreConditionsDiffer() {
      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet()
              .withCleared(aMinimalProperty()
                  .withName("p1"))));

      final Action a2 = make(aMinimalAction()
          .withPre(aPropositionSet()
              .withCleared(aMinimalProperty()
                  .withName("p2"))));

      assertNotEquals(a1, a2);
    }

    @Test
    public void notEqualWhenEffectsDiffer() {
      final Action a1 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(aMinimalProperty()
                  .withName("p1"))));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(aMinimalProperty()
                  .withName("p2"))));

      assertNotEquals(a1, a2);
    }

    @Test
    public void notEqualWhenRealizedFunctionalitiesDiffer() {
      final Action a1 = make(aMinimalAction()
          .withFunctionality(aFunctionality().withIdentifier("t1")));

      final Action a2 = make(aMinimalAction()
          .withFunctionality(aFunctionality().withIdentifier("t2")));

      assertNotEquals(a1, a2);
    }

    @Test
    public void notEqualWhenInteractionsDiffer() {
      final Action a1 = make(aMinimalAction()
          .withInteraction(anInteraction()
              .withInstruction("i1")));

      final Action a2 = make(aMinimalAction()
          .withInteraction(anInteraction()
              .withInstruction("i2")));

      assertNotEquals(a1, a2);
    }

    @Test
    public void equal() {
      final Action a1 = make(aMinimalAction());
      final Action a2 = make(aMinimalAction());
      assertEquals(a1, a2);
    }

  }

}
