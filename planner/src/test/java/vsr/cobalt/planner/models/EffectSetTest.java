/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Utilities.immutableSetOf;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.EffectSetMaker.anEffectSet;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropositionSetMaker.aPropositionSet;

@Test
public class EffectSetTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting properties to clear and fill to be disjoint sets")
    public void rejectNonDisjointPropertySets() {
      final Property p = make(aMinimalProperty());
      final ImmutableSet<Property> clear = immutableSetOf(p);
      final ImmutableSet<Property> fill = immutableSetOf(p);
      new EffectSet(clear, fill);
    }

  }

  @Test
  public static class Empty {

    @Test
    public void createEmptyEffects() {
      final EffectSet e = EffectSet.empty();
      assertEmpty(e.getPropertiesToClear());
      assertEmpty(e.getPropertiesToFill());
    }

  }

  @Test
  public static class Clear {

    @Test
    public void createEffectsWithOnlyPropertiesToClear() {
      final Property p = make(aMinimalProperty());
      final EffectSet e = EffectSet.clear(p);
      assertEquals(e.getPropertiesToClear(), setOf(p));
    }

  }

  @Test
  public static class Fill {

    @Test
    public void createEffectsWithOnlyPropertiesToFill() {
      final Property p = make(aMinimalProperty());
      final EffectSet e = EffectSet.fill(p);
      assertEquals(e.getPropertiesToFill(), setOf(p));
    }

  }

  @Test
  public static class CreatePostConditions {

    @Test
    public void createPostConditions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final EffectSet e = new EffectSet(immutableSetOf(p1), immutableSetOf(p2));

      final PropositionSet pre = make(aPropositionSet());

      final PropositionSet post = e.createPostConditions(pre);

      final PropositionSet xpost = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));

      assertEquals(post, xpost);
    }

    @Test
    public void carryOverUnaffectedPreConditions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final EffectSet e = EffectSet.empty();

      final PropositionSet pre = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));

      final PropositionSet post = e.createPostConditions(pre);

      final PropositionSet xpost = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));

      assertEquals(post, xpost);
    }

    @Test
    public void overwriteClearedProperty() {
      final Property p = make(aMinimalProperty());

      final EffectSet e = EffectSet.clear(p);

      final PropositionSet pre = make(aPropositionSet().withFilled(p));

      final PropositionSet post = e.createPostConditions(pre);

      final PropositionSet xpost = make(aPropositionSet().withCleared(p));

      assertEquals(post, xpost);
    }

    @Test
    public void overwriteFilledProperty() {
      final Property p = make(aMinimalProperty());

      final EffectSet e = EffectSet.fill(p);

      final PropositionSet pre = make(aPropositionSet().withCleared(p));

      final PropositionSet post = e.createPostConditions(pre);

      final PropositionSet xpost = make(aPropositionSet().withFilled(p));

      assertEquals(post, xpost);
    }

  }

  @Test
  public static class Equality {

    @Test
    public void calculateHashCodeFromClearedAndFilledProperties() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final EffectSet e = make(anEffectSet()
          .withToClear(p1)
          .withToFill(p2));
      assertEquals(e.hashCode(), Objects.hash(e.getPropertiesToClear(), e.getPropertiesToFill()));
    }

    @Test
    public void returnFalseWhenNotInstanceOfSameClass() {
      final EffectSet e = make(anEffectSet());
      assertFalse(e.equals(new Object()));
    }

    @Test
    public void returnFalseWhenPropertiesToClearDiffer() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final EffectSet e1 = make(anEffectSet().withToClear(p1));
      final EffectSet e2 = make(anEffectSet().withToClear(p2));
      assertNotEquals(e1, e2);
    }

    @Test
    public void returnFalseWhenPropertiesToFillDiffer() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final EffectSet e1 = make(anEffectSet().withToFill(p1));
      final EffectSet e2 = make(anEffectSet().withToFill(p2));
      assertNotEquals(e1, e2);
    }

    @Test
    public void returnTrueWithSamePropertiesToClearAndFill() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final EffectSet e1 = make(anEffectSet()
          .withToClear(p1)
          .withToFill(p2));
      final EffectSet e2 = make(anEffectSet()
          .withToClear(p1)
          .withToFill(p2));
      assertEquals(e1, e2);
    }

  }

}
