/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterators;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class PropositionSetTest {

  @Test
  public void extendsAbstractSet() {
    assertSubClass(PropositionSet.class, AbstractSet.class);
  }

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting no proposition to also appear negated")
    public void rejectWhenAnyPropositionIsAlsoNegated() {
      final Property p = make(aMinimalProperty());
      final Proposition q = Proposition.cleared(p);
      final Proposition r = q.negation();
      new PropositionSet(setOf(q, r));
    }

    @Test
    public void preventModificationOfPropositions() {
      final Set<Proposition> ps = emptySet();
      final PropositionSet pps = new PropositionSet(ps);
      ps.add(null);
      assertNotEquals(pps.getPropositions(), ps);
    }

    @Test
    public void createFromSetsOfClearedAndFilledProperties() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final PropositionSet ps = new PropositionSet(setOf(p1), setOf(p2));
      assertEquals(ps.getClearedProperties(), setOf(p1));
      assertEquals(ps.getFilledProperties(), setOf(p2));
    }

  }

  @Test
  public static class Empty {

    @Test
    public void createEmptyPropositionSet() {
      final PropositionSet ps = PropositionSet.empty();
      assertEmpty(ps);
    }

  }

  @Test
  public static class Cleared {

    @Test
    public void createPropositionsWithOnlyClearedProperties() {
      final Property p = make(aMinimalProperty());
      final PropositionSet ps = PropositionSet.cleared(p);
      assertEquals(ps.getClearedProperties(), setOf(p));
    }

  }

  @Test
  public static class Filled {

    @Test
    public void createPropositionsWithOnlyFilledProperties() {
      final Property p = make(aMinimalProperty());
      final PropositionSet ps = PropositionSet.filled(p);
      assertEquals(ps.getFilledProperties(), setOf(p));
    }

  }

  @Test
  public static class Size {

    @Test
    public void returnNumberOfPropositions() {
      final Property p = make(aMinimalProperty());
      final PropositionSet ps = make(aPropositionSet().withCleared(p));
      assertEquals(ps.size(), 1);
    }

  }

  @Test
  public static class Iterator_ {

    @Test
    public void returnIteratorOverPropositions() {
      final Property p = make(aMinimalProperty());
      final PropositionSet ps = make(aPropositionSet().withCleared(p));
      final Iterator<Proposition> it = ps.iterator();
      assertTrue(Iterators.elementsEqual(it, ps.getPropositions().iterator()));
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
  public static class ComputingGetters {

    private final Property property = make(aMinimalProperty());

    @Test
    public void getClearedProperties() {
      final PropositionSet ps = make(aPropositionSet().withCleared(property));
      assertEquals(ps.getClearedProperties(), setOf(property));
    }

    @Test
    public void getFilledProperties() {
      final PropositionSet ps = make(aPropositionSet().withFilled(property));
      assertEquals(ps.getFilledProperties(), setOf(property));
    }

  }

  @Test
  public static class CreatePostConditions {

    @Test
    public void createPostConditions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final PropositionSet eff = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));

      final PropositionSet pre = make(aPropositionSet());

      final PropositionSet post = eff.createPostConditions(pre);

      final PropositionSet xpost = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));

      assertEquals(post, xpost);
    }

    @Test
    public void carryOverUnaffectedPreConditions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final PropositionSet eff = PropositionSet.empty();

      final PropositionSet pre = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));

      final PropositionSet post = eff.createPostConditions(pre);

      final PropositionSet xpost = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));

      assertEquals(post, xpost);
    }

    @Test
    public void overwriteClearedProperty() {
      final Property p = make(aMinimalProperty());
      final PropositionSet eff = PropositionSet.cleared(p);
      final PropositionSet pre = make(aPropositionSet().withFilled(p));
      final PropositionSet post = eff.createPostConditions(pre);
      final PropositionSet xpost = make(aPropositionSet().withCleared(p));
      assertEquals(post, xpost);
    }

    @Test
    public void overwriteFilledProperty() {
      final Property p = make(aMinimalProperty());
      final PropositionSet eff = PropositionSet.filled(p);
      final PropositionSet pre = make(aPropositionSet().withCleared(p));
      final PropositionSet post = eff.createPostConditions(pre);
      final PropositionSet xpost = make(aPropositionSet().withFilled(p));
      assertEquals(post, xpost);
    }

  }

  @Test
  public static class Equality {

    @Test
    public void hashCodeFromPropositions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final PropositionSet ps = make(aPropositionSet()
          .withCleared(p1)
          .withFilled(p2));
      assertEquals(ps.hashCode(), ps.getPropositions().hashCode());
    }

    @Test
    public void notEqualToObjectOfDifferentType() {
      final PropositionSet ps = make(aPropositionSet());
      assertFalse(ps.equals(new Object()));
    }

    @Test
    public void notEqualWhenPropositionsDiffer() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final PropositionSet ps1 = make(aPropositionSet().withCleared(p1));
      final PropositionSet ps2 = make(aPropositionSet().withCleared(p2));
      assertNotEquals(ps1, ps2);
    }

    @Test
    public void equalWhenPropositionsEqual() {
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

}
