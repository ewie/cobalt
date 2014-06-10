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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aProperty;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class OfferTest {

  private static class DummyOffer extends Offer<Object> {

    public DummyOffer(final Object subject, final Action action) {
      super(subject, action);
    }

    @Override
    public boolean canEqual(final Object other) {
      return true;
    }

  }

  @Test
  public static class Getters {

    private DummyOffer offer;

    private Object subject;

    private Action action;

    @BeforeMethod
    public void setUp() {
      subject = new Object();
      action = make(aMinimalAction());
      offer = new DummyOffer(subject, action);
    }

    @Test
    public void getProvidingAction() {
      assertSame(offer.getAction(), action);
    }

    @Test
    public void getSubject() {
      assertSame(offer.getSubject(), subject);
    }

  }

  @Test
  public static class Equality {

    @Test
    public void calculateHashValueFromSubjectAndAction() {
      final Object s = new Object();
      final Action a = make(aMinimalAction());
      final DummyOffer o = new DummyOffer(s, a);
      assertEquals(o.hashCode(), Objects.hash(s, a));
    }

    @Test
    public void equalWhenSubjectsAndActionsEqual() {
      final Object s = new Object();
      final Action a = make(aMinimalAction());

      final DummyOffer o1 = new DummyOffer(s, a);
      final DummyOffer o2 = new DummyOffer(s, a);

      assertEquals(o1, o2);
    }

    @Test
    public void notEqualToObjectOfDifferentClass() {
      final Object s = new Object();
      final Action a = make(aMinimalAction());

      final DummyOffer o = new DummyOffer(s, a);
      final Object x = new Object();

      assertNotEquals(o, x);
    }

    @Test
    public void notEqualWhenOtherCanEqualReturnsFalse() {
      final Object s = new Object();
      final Action a = make(aMinimalAction());

      final DummyOffer o1 = new DummyOffer(s, a);
      final DummyOffer o2 = mock(DummyOffer.class);

      when(o2.canEqual(any())).thenReturn(false);

      assertNotEquals(o1, o2);
    }

    @Test
    public void notEqualWhenSubjectDiffers() {
      final Object s1 = new Object();
      final Object s2 = new Object();

      final Action a = make(aMinimalAction());

      final DummyOffer o1 = new DummyOffer(s1, a);
      final DummyOffer o2 = new DummyOffer(s2, a);

      assertNotEquals(o1, o2);
    }

    @Test
    public void notEqualWhenActionDiffers() {
      final Object s = new Object();

      final Action a1 = make(aMinimalAction()
          .withPub(aProperty().withName("p1")));

      final Action a2 = make(aMinimalAction()
          .withPub(aProperty().withName("p2")));

      final DummyOffer o1 = new DummyOffer(s, a1);
      final DummyOffer o2 = new DummyOffer(s, a2);

      assertNotEquals(o1, o2);
    }

  }

}
