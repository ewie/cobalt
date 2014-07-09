/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class FunctionalityTest {

  @Test
  public void isAnIdentifiable() {
    assertSubClass(Functionality.class, Identifiable.class);
  }

  @Test
  public static class CanEqual {

    @Test
    public void returnTrueWhenCalledWitFunctionality() {
      final Functionality f1 = make(aMinimalFunctionality());
      final Functionality f2 = make(aMinimalFunctionality());
      assertTrue(f1.canEqual(f2));
    }

    @Test
    public void returnFalseWhenCalledWithNonFunctionality() {
      final Functionality f = make(aMinimalFunctionality());
      final Object x = new Object();
      assertFalse(f.canEqual(x));
    }

  }

}
