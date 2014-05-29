/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.EffectSetMaker.anEffectSet;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.makers.TaskMaker.aTask;
import static vsr.cobalt.testing.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.testing.makers.WidgetMaker.aWidget;

@Test
public class ActionTest {

  @Test
  public static class Getters {

    private Action action;

    private Widget widget;

    private PropositionSet pre;

    private EffectSet effects;

    private ImmutableSet<Property> published;

    private ImmutableSet<Task> tasks;

    @BeforeMethod
    public void setUp() {
      final Property p = make(aMinimalProperty());
      widget = make(aMinimalWidget());
      pre = make(aPropositionSet().withCleared(p));
      effects = make(anEffectSet().withToFill(p));
      published = emptySet(Property.class);
      tasks = emptySet(Task.class);
      action = make(aMinimalAction()
          .withWidget(widget)
          .withPre(pre)
          .withEffects(effects)
          .withPubs(published)
          .withTasks(tasks));
    }

    @Test
    public void getWidget()
        throws Exception {
      assertSame(action.getWidget(), widget);
    }

    @Test
    public void getPreConditions() {
      assertSame(action.getPreConditions(), pre);
    }

    @Test
    public void getEffects() {
      assertSame(action.getEffects(), effects);
    }

    @Test
    public void getPostConditions() {
      assertEquals(action.getPostConditions(), effects.createPostConditions(pre));
    }

    @Test
    public void getPublishedProperties() {
      assertSame(action.getPublishedProperties(), published);
    }

    @Test
    public void getRealizedTasks() {
      assertSame(action.getRealizedTasks(), tasks);
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
  public static class Publishes {

    @Test
    public void returnTrueWhenTheGivenPropertyIsPublished() {
      final Property p = make(aMinimalProperty());
      final Action a = make(aMinimalAction().withPub(p));
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
    public void returnTrueWhenTheGivenTaskIsRealized() {
      final Task t = make(aMinimalTask());
      final Action a = make(aMinimalAction().withTask(t));
      assertTrue(a.realizes(t));
    }

    @Test
    public void returnFalseWhenTheGivenTaskIsNotRealized() {
      final Task t = make(aMinimalTask());
      final Action a = make(aMinimalAction());
      assertFalse(a.realizes(t));
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
          .withEffects(anEffectSet().withToClear(p)));

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
          .withEffects(anEffectSet()
              .withToClear(p1, p2)));

      final ImmutableSet<Property> ps = target.getFilledPropertiesNotSatisfiedByPrecursor(precursor);

      assertEquals(ps, setOf(p1));
    }

  }

  @Test
  public static class Equals {

    @Test
    public void returnFalseWhenComparedWithNonAction() {
      final Action a = make(aMinimalAction());
      final Object x = new Object();
      assertNotEquals(a, x);
    }

    @Test
    public void returnFalseWhenWidgetDiffers() {
      final Action a1 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w1")));

      final Action a2 = make(aMinimalAction()
          .withWidget(aWidget().withIdentifier("w2")));

      assertNotEquals(a1, a2);
    }

    @Test
    public void returnFalseWhenPreConditionsDiffer() {
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
    public void returnFalseWhenPostConditionsDiffer() {
      final Action a1 = make(aMinimalAction()
          .withEffects(anEffectSet()
              .withToClear(aMinimalProperty()
                  .withName("p1"))));

      final Action a2 = make(aMinimalAction()
          .withEffects(anEffectSet()
              .withToClear(aMinimalProperty()
                  .withName("p2"))));

      assertNotEquals(a1, a2);
    }

    @Test
    public void returnFalseWhenPublishedPropertiesDiffer() {
      final Action a1 = make(aMinimalAction()
          .withPub(aMinimalProperty().withName("p1")));

      final Action a2 = make(aMinimalAction()
          .withPub(aMinimalProperty().withName("p2")));

      assertNotEquals(a1, a2);
    }

    @Test
    public void returnFalseWhenRealizedTasksDiffer() {
      final Action a1 = make(aMinimalAction()
          .withTask(aTask().withIdentifier("t1")));

      final Action a2 = make(aMinimalAction()
          .withTask(aTask().withIdentifier("t2")));

      assertNotEquals(a1, a2);
    }

    @Test
    public void returnTrueWhenEqual() {
      final Action a1 = make(aMinimalAction());
      final Action a2 = make(aMinimalAction());
      assertEquals(a1, a2);
    }

  }

  @Test
  public static class HashCode {

    @Test
    public void returnHashValue() {
      final Action a = make(aMinimalAction());
      final int h = Objects.hash(
          a.getWidget(),
          a.getPreConditions(),
          a.getPostConditions(),
          a.getPublishedProperties(),
          a.getRealizedTasks());
      assertEquals(a.hashCode(), h);
    }

  }

}
