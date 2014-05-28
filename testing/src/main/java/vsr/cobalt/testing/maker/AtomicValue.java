/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.maker;

/**
 * @author Erik Wienhold
 */
public class AtomicValue<T> implements Value<T> {

  private T value;

  @Override
  public T get() {
    return value;
  }

  @Override
  public void set(final T value) {
    this.value = value;
  }

  public void set(final Maker<T> maker) {
    value = maker.make();
  }

}
