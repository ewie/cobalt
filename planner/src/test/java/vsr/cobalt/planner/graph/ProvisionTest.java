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
import vsr.cobalt.models.Offer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class ProvisionTest {

  private static class DummyProvision extends Provision<Object> {

    public DummyProvision(final Object request, final DummyOffer offer) {
      super(request, offer);
    }

    @Override
    public boolean canEqual(final Object other) {
      return true;
    }

  }

  private static class DummyOffer extends Offer<Object> {

    public DummyOffer(final Object subject) {
      super(subject, null);
    }

    @Override
    public boolean canEqual(final Object other) {
      return true;
    }

  }

  @Test
  public static class Getters {

    private DummyProvision provision;

    private Object request;

    private DummyOffer offer;

    @BeforeMethod
    public void setUp() {
      request = new Object();
      offer = mock(DummyOffer.class);
      when(offer.getAction()).thenReturn(make(aMinimalAction()));
      provision = new DummyProvision(request, offer);
    }

    @Test
    public void getRequest() {
      assertSame(provision.getRequest(), request);
    }

    @Test
    public void getOffer() {
      assertSame(provision.getOffer(), offer);
    }

    @Test
    public void getProvidingAction() {
      assertSame(provision.getProvidingAction(), offer.getAction());
    }

  }

  @Test
  public static class Equality {

    @Test
    public void calculateHashCodeFromRequestAndOffer() {
      final Object r = new Object();

      final DummyOffer o = new DummyOffer(null);

      final DummyProvision provision = new DummyProvision(r, o);
      assertEquals(provision.hashCode(), Objects.hash(r, o));
    }

    @Test
    public void equalWhenRequestsAndOffersEqual() {
      final Object r = new Object();

      final DummyOffer o = new DummyOffer(null);

      final DummyProvision p1 = new DummyProvision(r, o);
      final DummyProvision p2 = new DummyProvision(r, o);

      assertEquals(p1, p2);
    }

    @Test
    public void notEqualToObjectOfDifferentClass() {
      final Object r = new Object();
      final DummyOffer o = mock(DummyOffer.class);

      final DummyProvision p = new DummyProvision(r, o);
      final Object x = new Object();

      assertNotEquals(p, x);
    }

    @Test
    public void notEqualWhenOtherCanEqualReturnsFalse() {
      final Object r = new Object();
      final DummyOffer o = mock(DummyOffer.class);

      final DummyProvision p1 = new DummyProvision(r, o);

      final DummyProvision p2 = mock(DummyProvision.class);
      when(p2.canEqual(any())).thenReturn(false);

      assertNotEquals(p1, p2);
    }

    @Test
    public void notEqualWhenRequestDiffers() {
      final Object r1 = new Object();
      final Object r2 = new Object();

      final DummyOffer o = mock(DummyOffer.class);

      final DummyProvision p1 = new DummyProvision(r1, o);
      final DummyProvision p2 = new DummyProvision(r2, o);

      assertNotEquals(p1, p2);
    }

    @Test
    public void notEqualWhenOfferDiffers() {
      final Object r = new Object();

      final DummyOffer o1 = new DummyOffer(new Object());
      final DummyOffer o2 = new DummyOffer(new Object());

      final DummyProvision p1 = new DummyProvision(r, o1);
      final DummyProvision p2 = new DummyProvision(r, o2);

      assertNotEquals(p1, p2);
    }

  }

}
