/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PublishedPropertyTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting property to be published by given action")
    public void rejectPropertyNotPublishedByAction() {
      final Action a = make(aMinimalAction());
      final Property p = make(aMinimalProperty());
      new PublishedProperty(p, a);
    }

  }

  @Test
  public static class Getters {

    private Property p;

    private Action a;

    private PublishedProperty ap;

    @BeforeMethod
    public void setUp() {
      p = make(aMinimalProperty());
      a = make(aMinimalAction().withPub(p));
      ap = new PublishedProperty(p, a);
    }

    @Test
    public void getProperty() {
      assertSame(ap.getProperty(), p);
    }

    @Test
    public void getAction() {
      assertSame(ap.getAction(), a);
    }

  }

  @Test
  public static class Equality {

    private Property p;

    private Action a;

    @BeforeMethod
    public void setUp() {
      p = make(aMinimalProperty());
      a = make(aMinimalAction().withPub(p));
    }

    @Test
    public void generateHashCodeFromPropertyAndAction() {
      final PublishedProperty ap = new PublishedProperty(p, a);
      assertEquals(ap.hashCode(), Objects.hash(p, a));
    }

    @Test
    public void notEqualToObjectOfDifferentClass() {
      final PublishedProperty ap = new PublishedProperty(p, a);
      final Object x = new Object();
      assertNotEquals(ap, x);
    }

    @Test
    public void notEqualWhenPropertyDiffers() {
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final Action a2 = make(aMinimalAction().withPub(p2));
      final PublishedProperty ap1 = new PublishedProperty(p, a);
      final PublishedProperty ap2 = new PublishedProperty(p2, a2);
      assertNotEquals(ap1, ap2);
    }

    @Test
    public void notEqualWhenActionDiffers() {
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final Action a2 = make(aMinimalAction().withPub(p, p2));
      final PublishedProperty ap1 = new PublishedProperty(p, a);
      final PublishedProperty ap2 = new PublishedProperty(p, a2);
      assertNotEquals(ap1, ap2);
    }

    @Test
    public void equalWhenPropertiesAndActionsEqual() {
      final PublishedProperty ap1 = new PublishedProperty(p, a);
      final PublishedProperty ap2 = new PublishedProperty(p, a);
      assertEquals(ap1, ap2);
    }

  }

}
