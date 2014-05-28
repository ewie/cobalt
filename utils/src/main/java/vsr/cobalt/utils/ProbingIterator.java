/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.NoSuchElementException;

import com.google.common.collect.UnmodifiableIterator;

/**
 * An iterator probing a value in order to check if a next value exists.
 * <p/>
 * This strategy is useful when the check for a next value involves the creation of said value, i.e. checking for a next
 * value and creating it is essentially the same process.
 *
 * @author Erik Wienhold
 */
public abstract class ProbingIterator<T> extends UnmodifiableIterator<T> {

  private boolean done;

  private boolean hasPeeked;

  private T probedValue;

  /**
   * @return the next value or null when there are no more values
   */
  protected abstract T probeNextValue();

  @Override
  public final boolean hasNext() {
    return peek();
  }

  @Override
  public final T next() {
    if (!peek()) {
      throw new NoSuchElementException();
    }
    final T result = probedValue;
    probedValue = null;
    hasPeeked = false;
    return result;
  }

  private boolean peek() {
    if (!hasPeeked) {
      probedValue = probeNextValue();
      hasPeeked = true;
      done = probedValue == null;
    }
    return !done;
  }

}
