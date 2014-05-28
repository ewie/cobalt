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

import static org.testng.Assert.fail;

/**
 * @author Erik Wienhold
 */
public final class Assert {

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
    if (!empty(obj)) {
      fail("not empty: " + obj);
    }
  }

  public static void assertNotEmpty(final Object obj) {
    if (empty(obj)) {
      fail("empty: " + obj);
    }
  }

  public static void assertContains(final Object obj, final Object x) {
    if (!contains(obj, x)) {
      fail("does not contain: " + x + " " + obj);
    }
  }

  public static void assertNotContains(final Object obj, final Object x) {
    if (contains(obj, x)) {
      fail("does contain: " + x + " " + obj);
    }
  }

  private static boolean empty(final Object obj) {
    final Method mth = getMethod(obj, "isEmpty");
    if (mth == null) {
      fail("object has no method isEmpty");
    }
    if (!returnsAny(mth, "boolean", "java.lang.Boolean")) {
      fail("expecting isEmpty to return boolean or java.lang.Boolean");
    }
    try {
      return (boolean) mth.invoke(obj);
    } catch (IllegalAccessException | InvocationTargetException ex) {
      fail("cannot invoke isEmpty " + ex.getMessage(), ex);
    }
    // never reached
    return false;
  }

  private static boolean contains(final Object obj, final Object x) {
    final Method mth = getMethod(obj, "contains", Object.class);
    if (mth == null) {
      fail("object has no method contains");
    }
    if (!returnsAny(mth, "boolean", "java.lang.Boolean")) {
      fail("expecting contains to return boolean or java.lang.Boolean");
    }
    try {
      return (boolean) mth.invoke(obj, x);
    } catch (IllegalAccessException | InvocationTargetException ex) {
      fail("cannot invoke contains " + ex.getMessage(), ex);
    }
    // never reached
    return false;
  }

  private static boolean returnsAny(final Method mth, final String... names) {
    final Class<?> cls = mth.getReturnType();
    final String name = cls.getCanonicalName();
    return 0 <= Arrays.binarySearch(names, name);
  }

  private static Method getMethod(final Object obj, final String name, final Class<?>... params) {
    final Class<?> cls = obj.getClass();
    Method mth = null;
    try {
      mth = cls.getMethod(name, params);
    } catch (final NoSuchMethodException ignored) {
    }
    if (mth != null) {
      mth.setAccessible(true);
    }
    return mth;
  }

}
