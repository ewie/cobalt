/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Objects;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vsr.cobalt.planner.models.Action;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.PropertyMaker.aProperty;

@Test
public class ProvisionTest {

  private static class DummyProvision extends Provision<Object> {

    public DummyProvision(final Action providingAction, final Object offer, final Object request) {
      super(request, offer, providingAction);
    }

    @Override
    public boolean canEqual(final Object other) {
      return true;
    }

  }

  @Test
  public static class Getters {

    private DummyProvision provision;

    private Object offer;

    private Object request;

    private Action action;

    @BeforeMethod
    public void setUp() {
      offer = new Object();
      request = new Object();
      action = make(aMinimalAction());
      provision = new DummyProvision(action, offer, request);
    }

    @Test
    public void getProvidingAction() {
      assertSame(provision.getProvidingAction(), action);
    }

    @Test
    public void getOffer() {
      assertSame(provision.getOffer(), offer);
    }

    @Test
    public void getRequest() {
      assertSame(provision.getRequest(), request);
    }

  }

  @Test
  public class Equality {

    @Test
    public void returnHashValue() {
      final Action a = make(aMinimalAction());
      final Object o = new Object();
      final Object r = new Object();
      final DummyProvision provision = new DummyProvision(a, o, r);
      assertEquals(provision.hashCode(), Objects.hash(a, o, r));
    }

    @Test
    public void returnTrueWhenEqual() {
      final Action a = make(aMinimalAction());
      final Object o = new Object();
      final Object r = new Object();

      final DummyProvision p1 = new DummyProvision(a, o, r);
      final DummyProvision p2 = new DummyProvision(a, o, r);

      assertEquals(p1, p2);
    }

    @Test
    public void returnFalseWhenOtherIsNoProvision() {
      final Action a = make(aMinimalAction());
      final Object o = new Object();
      final Object r = new Object();

      final DummyProvision p = new DummyProvision(a, o, r);
      final Object x = new Object();

      assertNotEquals(p, x);
    }

    @Test
    public void returnFalseWhenOtherCanEqualReturnsFalse() {
      final Action a = make(aMinimalAction());
      final Object o = new Object();
      final Object r = new Object();

      final DummyProvision p1 = new DummyProvision(a, o, r);
      final DummyProvision p2 = mock(DummyProvision.class);

      when(p2.canEqual(any())).thenReturn(false);

      assertNotEquals(p1, p2);
    }

    @Test
    public void returnFalseWhenActionDiffers() {
      final Action a1 = make(aMinimalAction()
          .withPub(aProperty().withName("p")));

      final Action a2 = make(aMinimalAction()
          .withPub(aProperty().withName("q")));

      final Object o = new Object();
      final Object r = new Object();

      final DummyProvision p1 = new DummyProvision(a1, o, r);
      final DummyProvision p2 = new DummyProvision(a2, o, r);

      assertNotEquals(p1, p2);
    }

    @Test
    public void returnFalseWhenOfferDiffers() {
      final Action a = make(aMinimalAction());

      final Object o1 = new Object();
      final Object o2 = new Object();
      final Object r = new Object();

      final DummyProvision p1 = new DummyProvision(a, o1, r);
      final DummyProvision p2 = new DummyProvision(a, o2, r);

      assertNotEquals(p1, p2);
    }

    @Test
    public void returnFalseWhenRequestDiffers() {
      final Action a = make(aMinimalAction());

      final Object o = new Object();
      final Object r1 = new Object();
      final Object r2 = new Object();

      final DummyProvision p1 = new DummyProvision(a, o, r1);
      final DummyProvision p2 = new DummyProvision(a, o, r2);

      assertNotEquals(p1, p2);
    }

  }

}
