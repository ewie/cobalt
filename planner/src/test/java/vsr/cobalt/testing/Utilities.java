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

  public static void assertSubClass(final Class<?> clazz, final Class<?> parent) {
    Class<?> c = clazz;
    while (true) {
      c = c.getSuperclass();
      if (c == null) {
        fail("expecting " + clazz.getCanonicalName() + " to be subclass of " + parent.getCanonicalName());
        break;
      } else if (c.equals(parent)) {
        break;
      }
    }
  }

  public static void assertEmpty(final Object obj) {
    final Method mth = getMethod(obj, "isEmpty");
    if (mth == null) {
      fail("object has no method isEmpty");
    }
    if (!returnsAny(mth, "boolean", "java.lang.Boolean")) {
      fail("expecting isEmpty to return boolean or java.lang.Boolean");
    }
    boolean isEmpty = false;
    try {
      isEmpty = (boolean) mth.invoke(obj);
    } catch (IllegalAccessException | InvocationTargetException ex) {
      fail("cannot invoke isEmpty " + ex.getMessage(), ex);
    }
    if (!isEmpty) {
      fail("not empty: " + obj);
    }
  }

  private static boolean returnsAny(final Method mth, final String... names) {
    final Class<?> cls = mth.getReturnType();
    final String name = cls.getCanonicalName();
    return 0 <= Arrays.binarySearch(names, name);
  }

  private static Method getMethod(final Object obj, final String name) {
    final Class<?> cls = obj.getClass();
    Method mth = null;
    try {
      mth = cls.getMethod(name);
    } catch (final NoSuchMethodException ignored) {
    }
    if (mth != null) {
      mth.setAccessible(true);
    }
    return mth;
  }

}
