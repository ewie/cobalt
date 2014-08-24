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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class WidgetTest {

  @Test
  public void isAnIdentifiable() {
    assertSubClass(Widget.class, Identifiable.class);
  }

  @Test
  public static class IsPublicProperty {

    Property property;

    Widget widget;

    @BeforeMethod
    public void setUp() {
      widget = make(aMinimalWidget().withPublic(property));
    }

    @Test
    public void returnTrueWhenPublic() {
      assertTrue(widget.isPublicProperty(property));
    }

    @Test
    public void returnFalseWhenPublic() {
      final Property p = make(aMinimalProperty().withName("p"));
      assertFalse(widget.isPublicProperty(p));
    }

  }

  @Test
  public static class CanEqual {

    @Test
    public void returnTrueWhenCalledWithWidget() {
      final Widget w1 = make(aMinimalWidget());
      final Widget w2 = make(aMinimalWidget());
      assertTrue(w1.canEqual(w2));
    }

    @Test
    public void returnFalseWhenCalledWithNonWidget() {
      final Widget w = make(aMinimalWidget());
      final Object x = new Object();
      assertFalse(w.canEqual(x));
    }

  }

  @Test
  public static class Equality {

    @Test
    public void hashCodeFromIdentifierAndPublicProperties() {
      final Property p = make(aMinimalProperty());

      final Widget w = make(aWidget()
          .withIdentifier("w")
          .withPublic(p));

      assertEquals(w.hashCode(),
          Objects.hash(w.getIdentifier(), w.getPublicProperties()));
    }

    @Test
    public void notEqualWhenIdentifierDiffers() {
      final Property p = make(aMinimalProperty());

      final Widget w1 = make(aMinimalWidget()
          .withIdentifier("w1")
          .withPublic(p));
      final Widget w2 = make(aMinimalWidget()
          .withIdentifier("w2")
          .withPublic(p));

      assertNotEquals(w1, w2);
    }

    @Test
    public void notEqualWhenPublicPropertiesDiffer() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w1 = make(aMinimalWidget().withPublic(p1));
      final Widget w2 = make(aMinimalWidget().withPublic(p2));

      assertNotEquals(w1, w2);
    }

    @Test
    public void equalWhenIdentifierAndPublicPropertiesEqual() {
      final Property p = make(aMinimalProperty());

      final Widget w1 = make(aMinimalWidget().withPublic(p));
      final Widget w2 = make(aMinimalWidget().withPublic(p));

      assertEquals(w1, w2);
    }

  }

}
