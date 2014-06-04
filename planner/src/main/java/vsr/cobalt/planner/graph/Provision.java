/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Objects;

import vsr.cobalt.models.Action;

/**
 * Base class to represent provisions conforming to the request/offer/provider triple.
 *
 * @author Erik Wienhold
 */
public abstract class Provision<T> {

  private final T request;

  private final T offer;

  private final Action action;

  /**
   * @param request a requested subject
   * @param offer   a subject offered by a providing action
   * @param action  a providing action
   */
  public Provision(final T request, final T offer, final Action action) {
    this.request = request;
    this.offer = offer;
    this.action = action;
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
  public T getOffer() {
    return offer;
  }

  /**
   * @return the providing action
   */
  public Action getProvidingAction() {
    return action;
  }

  protected abstract boolean canEqual(Object other);

  @Override
  public int hashCode() {
    return Objects.hash(action, offer, request);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Provision
        && equals((Provision<?>) other);
  }

  private boolean equals(final Provision<?> other) {
    return other.canEqual(this)
        && Objects.equals(action, other.action)
        && Objects.equals(offer, other.offer)
        && Objects.equals(request, other.request);
  }

}
