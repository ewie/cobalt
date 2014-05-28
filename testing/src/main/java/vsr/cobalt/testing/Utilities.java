/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.testing.maker.Maker;

import static org.testng.Assert.fail;

/**
 * @author Erik Wienhold
 */
public abstract class Utilities {

  public static <T> T make(final Maker<T> maker) {
    return maker.make();
  }

  @SafeVarargs
  public static <E> ImmutableSet<E> immutableSetOf(final E... elements) {
    return ImmutableSet.of(elements);
  }

  @SafeVarargs
  public static <E> Set<E> setOf(final E... elements) {
    return new HashSet<>(Arrays.asList(elements));
  }

  public static <E> ImmutableSet<E> emptySet() {
    return ImmutableSet.of();
  }

  public static <E> ImmutableSet<E> emptySet(final Class<E> unused) {
    return ImmutableSet.of();
  }

}
