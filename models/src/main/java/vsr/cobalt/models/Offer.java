/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

/**
 * Specifies a subject offered by an action.
 *
 * @author Erik Wienhold
 */
public abstract class Offer<T> {

  private final T subject;

  private final Action action;

  /**
   * @param subject an offered subject
   * @param action  an offering action
   */
  public Offer(final T subject, final Action action) {
    this.subject = subject;
    this.action = action;
  }

  /**
   * @return the offered subject
   */
  public T getSubject() {
    return subject;
  }

  /**
   * @return the offering action
   */
  public Action getAction() {
    return action;
  }

  protected abstract boolean canEqual(Object other);

  @Override
  public int hashCode() {
    return Objects.hash(subject, action);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Offer
        && equals((Offer<?>) other);
  }

  private boolean equals(final Offer<?> other) {
    return other.canEqual(this)
        && Objects.equals(subject, other.subject)
        && Objects.equals(action, other.action);
  }

}
