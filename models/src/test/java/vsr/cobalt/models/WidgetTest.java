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
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class WidgetTest {

  @Test
  public void isAnIdentifiable() {
    assertSubClass(Widget.class, Identifiable.class);
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

}
