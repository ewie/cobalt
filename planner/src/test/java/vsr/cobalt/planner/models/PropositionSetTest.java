/*
* Copyright (c) 2014, Erik Wienhold
* All rights reserved.
*
* Licensed under the BSD 3-Clause License.
*/

package vsr.cobalt.planner.models;

import java.util.Objects;
import java.util.Set;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropositionSetMaker.aPropositionSet;

@Test
public class PropositionSetTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting cleared and filled properties to be disjoint sets")
    public void rejectWhenClearedAndFilledPropertiesAreNotDisjoint() {
      final Property p = make(aMinimalProperty());
      new PropositionSet(setOf(p), setOf(p));
    }

    @Test
    public void preventModificationOfPropertiesToClear() {
      final Set<Property> ps = emptySet();
      final PropositionSet pps = new PropositionSet(ps, emptySet(Property.class));
      ps.add(null);
      assertNotEquals(pps.getClearedProperties(), ps);
    }

    @Test
    public void preventModificationOfPropertiesToFill() {
      final Set<Property> ps = emptySet();
      final PropositionSet pps = new PropositionSet(emptySet(Property.class), ps);
      ps.add(null);
      assertNotEquals(pps.getFilledProperties(), ps);
    }

  }

  @Test
  public static class IsEmpty {

    @Test
    public void returnTrueWhenEmpty() {
      final PropositionSet ps = make(aPropositionSet());
      assertTrue(ps.isEmpty());
    }

    @Test
    public void returnFalseWithAtLeastOneClearedProperty() {
      final PropositionSet ps = make(aPropositionSet().withCleared(aMinimalProperty()));
      assertFalse(ps.isEmpty());
    }

    @Test
    public void returnFalseWithAtLeastOneFilledProperty() {
      final PropositionSet ps = make(aPropositionSet().withFilled(aMinimalProperty()));
      assertFalse(ps.isEmpty());
    }

  }

  @Test
  public static class IsFilled {

    private final Property p = make(aMinimalProperty());

    @Test
    public void returnTrueWhenFilled() {
      final PropositionSet ps = make(aPropositionSet().withFilled(p));
      assertTrue(ps.isFilled(p));
    }

    @Test
    public void returnFalseWhenNotFilled() {
      final PropositionSet ps = make(aPropositionSet());
      assertFalse(ps.isFilled(p));
    }

  }

  @Test
  public static class IsCleared {

    private final Property p = make(aMinimalProperty());

    @Test
    public void returnTrueWhenCleared() {
      final PropositionSet ps = make(aPropositionSet().withCleared(p));
      assertTrue(ps.isCleared(p));
    }

    @Test
    public void returnFalseWhenNotCleared() {
      final PropositionSet ps = make(aPropositionSet());
      assertFalse(ps.isCleared(p));
    }

  }

  @Test
  public static class Getters {

    private final Property p = make(aMinimalProperty());

    @Test
    public void getClearedProperties() {
      final PropositionSet ps = make(aPropositionSet().withCleared(p));
      assertEquals(ps.getClearedProperties(), setOf(p));
    }

    @Test
    public void getFilledProperties() {
      final PropositionSet ps = make(aPropositionSet().withFilled(p));
      assertEquals(ps.getFilledProperties(), setOf(p));
    }

  }

  @Test
  public static class Equals {

    @Test
    public void returnFalseWhenNotInstanceOfSameClass() {
      final PropositionSet ps = make(aPropositionSet());
      assertFalse(ps.equals(new Object()));
    }

    @Test
    public void returnFalseWhenClearedPropertiesDiffer() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final PropositionSet ps1 = make(aPropositionSet().withCleared(p1));
      final PropositionSet ps2 = make(aPropositionSet().withCleared(p2));
      assertNotEquals(ps1, ps2);
    }

    @Test
    public void returnFalseWhenFilledPropertiesDiffer() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final PropositionSet ps1 = make(aPropositionSet().withFilled(p1));
      final PropositionSet ps2 = make(aPropositionSet().withFilled(p2));
      assertNotEquals(ps1, ps2);
    }

    @Test
    public void returnTrueWithSameClearedAndFilledProperties() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final PropositionSet ps1 = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));
      final PropositionSet ps2 = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));
      assertEquals(ps1, ps2);
    }

  }

  @Test
  public static class HashCode {

    @Test
    public void calculateHashCodeFromClearedAndFilledProperties() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final PropositionSet ps = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));
      assertEquals(ps.hashCode(), Objects.hash(ps.getClearedProperties(), ps.getFilledProperties()));
    }

  }

}
