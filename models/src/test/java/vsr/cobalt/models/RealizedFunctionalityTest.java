/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class RealizedFunctionalityTest {

  @Test
  public void extendsOffer() {
    assertSubClass(RealizedFunctionality.class, Offer.class);
  }

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting functionality to be realized by given action")
    public void rejectFunctionalityNotPublishedByAction() {
      final Action a = make(aMinimalAction());
      final Functionality f = make(aMinimalFunctionality());
      new RealizedFunctionality(f, a);
    }

  }

  @Test
  public static class CanEqual {

    RealizedFunctionality rf;

    @BeforeMethod
    public void setUp() {
      final Functionality f = make(aMinimalFunctionality());
      final Action a = make(aMinimalAction().withFunctionality(f));
      rf = new RealizedFunctionality(f, a);
    }

    @Test
    public void returnTrueWhenCalledWithRealizedFunctionality() {
      assertTrue(rf.canEqual(rf));
    }

    @Test
    public void returnFalseWhenCalledWithNonRealizedFunctionality() {
      final Object x = new Object();
      assertFalse(rf.canEqual(x));
    }

  }

}
