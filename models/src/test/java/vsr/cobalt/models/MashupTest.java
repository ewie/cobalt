/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class MashupTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting one or more functionalities")
    public void rejectEmptySetOfFunctionalities() {
      new Mashup(emptySet(Functionality.class));
    }

    @Test
    public void preventModificationOfFunctionalities() {
      final Functionality f = make(aMinimalFunctionality());
      final Set<Functionality> fs = setOf(f);
      final Mashup m = new Mashup(fs);
      fs.add(null);
      assertNotEquals(m.getFunctionalities(), fs);
    }

  }

  @Test
  public static class GetFunctionalities {

    @Test
    public void returnTheFunctionalities() {
      final Functionality f = make(aMinimalFunctionality());
      final Set<Functionality> fs = setOf(f);
      final Mashup m = new Mashup(fs);
      assertEquals(m.getFunctionalities(), fs);
    }

  }

  @Test
  public static class Equality {

    private Mashup mashup;

    @BeforeMethod
    public void setUp() {
      final Functionality f = make(aMinimalFunctionality());
      mashup = new Mashup(setOf(f));
    }

    @Test
    public void calculateHastCodeFromFunctionalities() {
      assertEquals(mashup.hashCode(), mashup.getFunctionalities().hashCode());
    }

    @Test
    public void notEqualToObjectOfDifferentClass() {
      final Object x = new Object();
      assertNotEquals(mashup, x);
    }

    @Test
    public void notEqualWhenRealizedFunctionalitiesDiffer() {
      final Functionality f = make(aMinimalFunctionality().withIdentifier("f"));
      final Mashup other = new Mashup(setOf(f));
      assertNotEquals(mashup, other);
    }

    @Test
    public void equalWhenFunctionalitiesEqual() {
      final Mashup other = new Mashup(mashup.getFunctionalities());
      assertEquals(mashup, other);
    }

  }

}
