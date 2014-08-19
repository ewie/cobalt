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
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropertyMaker.aProperty;
import static vsr.cobalt.models.makers.TypeMaker.aMinimalType;
import static vsr.cobalt.models.makers.TypeMaker.aType;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PropertyTest {

  @Test
  public static class Equality {

    @Test
    public void calculateHashCode() {
      final String name = "baz";
      final Type type = make(aMinimalType());
      final Property property = make(aProperty()
          .withName(name)
          .withType(type));
      assertEquals(property.hashCode(), Objects.hash(name, type));
    }

    @Test
    public void notEqualWhenNotInstanceOfSameClass() {
      final Property p = make(aMinimalProperty());
      final Object x = new Object();
      assertFalse(p.equals(x));
    }

    @Test
    public void notEqualWhenNameDiffers() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      assertFalse(p1.equals(p2));
    }

    @Test
    public void notEqualWhenTypeDiffers() {
      final Property p1 = make(aMinimalProperty().withType(aType().withIdentifier("t1")));
      final Property p2 = make(aMinimalProperty().withType(aType().withIdentifier("t2")));
      assertFalse(p1.equals(p2));
    }

    @Test
    public void equal() {
      final Property p1 = make(aMinimalProperty());
      final Property p2 = make(aMinimalProperty());
      assertTrue(p1.equals(p2));
    }

  }

}
