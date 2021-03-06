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
public interface Value<T> {

  T get();

  void set(T value);

}
