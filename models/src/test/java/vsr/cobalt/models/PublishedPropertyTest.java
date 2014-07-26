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
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PublishedPropertyTest {

  @Test
  public void extendsOffer() {
    assertSubClass(PublishedProperty.class, Offer.class);
  }

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
  public static class CanEqual {

    PublishedProperty pp;

    @BeforeMethod
    public void setUp() {
      final Property p = make(aMinimalProperty());
      final Action a = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withFilled(p)));
      pp = new PublishedProperty(p, a);
    }

    @Test
    public void returnTrueWhenCalledWithPublishedProperty() {
      assertTrue(pp.canEqual(pp));
    }

    @Test
    public void returnFalseWhenCalledWithNonPublishedProperty() {
      final Object x = new Object();
      assertFalse(pp.canEqual(x));
    }

  }

}
