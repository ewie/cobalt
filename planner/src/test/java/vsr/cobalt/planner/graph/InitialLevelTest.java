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
import vsr.cobalt.models.Functionality;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aMinimalFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.aMinimalInitialLevel;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class InitialLevelTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting one or more functionality provisions")
    public void rejectEmptySetOfFunctionalityProvisions() {
      new InitialLevel(emptySet(FunctionalityProvision.class));
    }

    @Test
    public void preventModificationOfFunctionalityProvisions() {
      final FunctionalityProvision fp = make(aMinimalFunctionalityProvision());
      final Set<FunctionalityProvision> fps = setOf(fp);
      final InitialLevel il = new InitialLevel(fps);
      fps.add(null);
      assertNotEquals(il.getFunctionalityProvisions(), fps);
    }

  }

  @Test
  public static class GetFunctionalityProvisions {

    @Test
    public void returnAllFunctionalityProvisions() {
      final FunctionalityProvision fp = make(aMinimalFunctionalityProvision());
      final InitialLevel s = make(anInitialLevel().withProvision(fp));
      assertEquals(s.getFunctionalityProvisions(), setOf(fp));
    }

  }

  @Test
  public static class GetRequestedFunctionalities {

    @Test
    public void returnAllRequestedFunctionalities() {
      final Functionality f1 = make(aMinimalFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aMinimalFunctionality().withIdentifier("f2"));

      final Action a1 = make(aMinimalAction().withFunctionality(f1));
      final Action a2 = make(aMinimalAction().withFunctionality(f2));

      final FunctionalityProvision fp1 = make(aFunctionalityProvision()
          .withProvidingAction(a1)
          .withOffer(f1)
          .withRequest(f1));

      final FunctionalityProvision fp2 = make(aFunctionalityProvision()
          .withProvidingAction(a2)
          .withOffer(f2)
          .withRequest(f2));

      final InitialLevel s = make(anInitialLevel().withProvision(fp1, fp2));

      assertEquals(s.getRequestedFunctionalities(), setOf(f1, f2));
    }

  }

  @Test
  public static class GetFunctionalityProvisionsByRequestedFunctionality {

    @Test
    public void returnEmptySetWhenThereIsNoFunctionalityProvisionForTheGivenFunctionality() {
      final InitialLevel s = make(aMinimalInitialLevel());
      final Functionality f = make(aFunctionality().withIdentifier("x"));
      assertEmpty(s.getFunctionalityProvisionsByRequestedFunctionality(f));
    }

    @Test
    public void returnAllFunctionalityProvisionsHavingTheRequiredFunctionality() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));

      final FunctionalityProvision fp1 = make(aFunctionalityProvision()
          .withProvidingAction(aMinimalAction().withFunctionality(f1))
          .withOffer(f1)
          .withRequest(f1));

      final FunctionalityProvision fp2 = make(aMinimalFunctionalityProvision());

      final InitialLevel s = make(anInitialLevel().withProvision(fp1, fp2));

      assertEquals(s.getFunctionalityProvisionsByRequestedFunctionality(f1), setOf(fp1));
    }

  }

  @Test
  public static class GetRequiredActions {

    @Test
    public void returnTheUnionOfAllRequiredActions() {
      final Functionality f1 = make(aFunctionality().withIdentifier("f1"));
      final Functionality f2 = make(aFunctionality().withIdentifier("f2"));

      final Action a1 = make(aMinimalAction().withFunctionality(f1));
      final Action a2 = make(aMinimalAction().withFunctionality(f2));

      final InitialLevel s = make(anInitialLevel()
          .withProvision(aFunctionalityProvision()
              .withProvidingAction(a1)
              .withOffer(f1))
          .withProvision(aFunctionalityProvision()
              .withProvidingAction(a2)
              .withOffer(f2)));

      assertEquals(s.getRequiredActions(), setOf(a1, a2));
    }

  }

  @Test
  public static class Equality {

    @Test
    public void useHashCodeOfFunctionalityProvisionSet() {
      final InitialLevel il = new InitialLevel(setOf(make(aMinimalFunctionalityProvision())));
      assertEquals(il.hashCode(), il.getFunctionalityProvisions().hashCode());
    }

    @Test
    public void returnTrueWhenFunctionalityProvisionSetsAreEqual() {
      final InitialLevel il1 = new InitialLevel(setOf(make(aMinimalFunctionalityProvision())));
      final InitialLevel il2 = new InitialLevel(setOf(make(aMinimalFunctionalityProvision())));
      assertEquals(il1, il2);
    }

    @Test
    public void returnFalseWhenComparedWithNonInitialLevel() {
      final InitialLevel il = new InitialLevel(setOf(make(aMinimalFunctionalityProvision())));
      final Object x = new Object();
      assertNotEquals(il, x);
    }

    @Test
    public void returnFalseWhenFunctionalityProvisionSetsDiffer() {
      final Functionality f = make(aMinimalFunctionality().withIdentifier("f"));

      final Action a = make(aMinimalAction().withFunctionality(f));

      final FunctionalityProvision fp1 = make(aFunctionalityProvision()
          .withProvidingAction(a)
          .withRequest(f)
          .withOffer(f));

      final FunctionalityProvision fp2 = make(aMinimalFunctionalityProvision());

      final InitialLevel il1 = new InitialLevel(setOf(fp1));
      final InitialLevel il2 = new InitialLevel(setOf(fp2));

      assertNotEquals(il1, il2);
    }

  }

}
