/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public abstract class Utilities {

  public static <T> T make(final Maker<T> maker) {
    return maker.make();
  }

  @SafeVarargs
  public static <E> Set<E> setOf(final E... elements) {
    return new HashSet<>(Arrays.asList(elements));
  }

  public static <E> Set<E> emptySet() {
    return new HashSet<>();
  }

  public static <E> Set<E> emptySet(final Class<E> unused) {
    return emptySet();
  }

}
