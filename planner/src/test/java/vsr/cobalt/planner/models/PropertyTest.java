/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

import java.util.Objects;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropertyMaker.aProperty;
import static vsr.cobalt.testing.makers.TypeMaker.aMinimalType;
import static vsr.cobalt.testing.makers.TypeMaker.aType;

@Test
public class PropertyTest {

  @Test
  public static class Getters {

    private Property property;

    private String name;

    private Type type;

    @BeforeMethod
    public void setUp() {
      name = "p";
      type = make(aMinimalType());
      property = make(aProperty()
          .withName(name)
          .withType(type));
    }

    @Test
    public void getName() {
      assertEquals(property.getName(), name);
    }

    @Test
    public void getType() {
      assertSame(property.getType(), type);
    }

  }

  @Test
  public static class Equals {

    @Test
    public void returnFalseWhenNotInstanceOfSameClass() {
      final Property p = make(aMinimalProperty());
      final Object x = new Object();
      assertFalse(p.equals(x));
    }

    @Test
    public void returnFalseWhenNameDiffers() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      assertFalse(p1.equals(p2));
    }

    @Test
    public void returnFalseWhenTypeDiffers() {
      final Property p1 = make(aMinimalProperty().withType(aType().withIdentifier("t1")));
      final Property p2 = make(aMinimalProperty().withType(aType().withIdentifier("t2")));
      assertFalse(p1.equals(p2));
    }

    @Test
    public void returnTrueWhenEqual() {
      final Property p1 = make(aMinimalProperty());
      final Property p2 = make(aMinimalProperty());
      assertTrue(p1.equals(p2));
    }

  }

  @Test
  public static class HashCode {

    @Test
    public void useNameAndType() {
      final String name = "baz";
      final Type type = make(aMinimalType());
      final Property property = make(aProperty()
          .withName(name)
          .withType(type));
      assertEquals(property.hashCode(), Objects.hash(name, type));
    }

  }

}
