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
import static vsr.cobalt.models.makers.TypeMaker.aMinimalType;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class TypeTest {

  @Test
  public void isAnIdentifiable() {
    assertSubClass(Type.class, Identifiable.class);
  }

  @Test
  public static class CanEqual {

    @Test
    public void returnTrueWhenCalledWithType() {
      final Type t1 = make(aMinimalType());
      final Type t2 = make(aMinimalType());
      assertTrue(t1.canEqual(t2));
    }

    @Test
    public void returnFalseWhenCalledWithNonType() {
      final Type t = make(aMinimalType());
      final Object x = new Object();
      assertFalse(t.canEqual(x));
    }

  }

}
