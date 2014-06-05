/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Objects;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Offer;

/**
 * Base class to represent provisions conforming to the request/offer/provider triple.
 *
 * @author Erik Wienhold
 */
public abstract class Provision<T> {

  private final T request;

  private final Offer<T> offer;

  /**
   * @param request a requested subject
   * @param offer   an offer
   */
  public Provision(final T request, final Offer<T> offer) {
    this.request = request;
    this.offer = offer;
  }

  /**
   * Create a provision using the offered subject as request.
   *
   * @param offer an offer
   */
  public Provision(final Offer<T> offer) {
    this(offer.getSubject(), offer);
  }

  /**
   * @return the requested object
   */
  public T getRequest() {
    return request;
  }

  /**
   * @return the object offered by the providing action
   */
  public Offer<T> getOffer() {
    return offer;
  }

  /**
   * @return the providing action
   */
  public Action getProvidingAction() {
    return offer.getAction();
  }

  protected abstract boolean canEqual(Object other);

  @Override
  public int hashCode() {
    return Objects.hash(request, offer);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Provision
        && equals((Provision<?>) other);
  }

  private boolean equals(final Provision<?> other) {
    return other.canEqual(this)
        && Objects.equals(request, other.request)
        && Objects.equals(offer, other.offer);
  }

}
