/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PropositionTest {

  @Test
  public static class Cleared {

    @Test
    public void createPropositionRepresentingClearedValue() {
      final Property p = make(aMinimalProperty());
      final Proposition q = Proposition.cleared(p);
      assertFalse(q.isFilled());
    }

  }

  @Test
  public static class Filled {

    @Test
    public void createPropositionRepresentingFilledValue() {
      final Property p = make(aMinimalProperty());
      final Proposition q = Proposition.filled(p);
      assertTrue(q.isFilled());
    }

  }

  @Test
  public static class Negation {

    @Test
    public void returnNegatedPropositionWhenTrue() {
      final Property p = make(aMinimalProperty());
      final Proposition q = Proposition.cleared(p);
      final Proposition r = q.negation();
      assertEquals(r, Proposition.filled(p));
    }

    @Test
    public void returnNegatedPropositionWhenFalse() {
      final Property p = make(aMinimalProperty());
      final Proposition q = Proposition.filled(p);
      final Proposition r = q.negation();
      assertEquals(r, Proposition.cleared(p));
    }

  }

  @Test
  public static class Equality {

    @Test
    public void hashCodeFromPropertyAndState() {
      final Property p = make(aMinimalProperty());
      final Proposition q = Proposition.cleared(p);
      assertEquals(q.hashCode(), Objects.hash(p, q.isFilled()));
    }

    @Test
    public void notEqualToObjectOfDifferentType() {
      final Property p = make(aMinimalProperty());
      final Proposition q = Proposition.cleared(p);
      final Object x = new Object();
      assertNotEquals(q, x);
    }

    @Test
    public void notEqualWhenPropertyDiffers() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final Proposition q1 = Proposition.cleared(p1);
      final Proposition q2 = Proposition.cleared(p2);
      assertNotEquals(q1, q2);
    }

    @Test
    public void notEqualWhenValueStateDiffers() {
      final Property p = make(aMinimalProperty());
      final Proposition q1 = Proposition.cleared(p);
      final Proposition q2 = Proposition.filled(p);
      assertNotEquals(q1, q2);
    }

    @Test
    public void notEqualWhenPropertyAndValueStateEquals() {
      final Property p = make(aMinimalProperty());
      final Proposition q1 = Proposition.cleared(p);
      final Proposition q2 = Proposition.cleared(p);
      assertEquals(q1, q2);
    }

  }

}
