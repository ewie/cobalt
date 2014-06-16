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

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import vsr.cobalt.testing.maker.Maker;

import static java.lang.reflect.Modifier.isAbstract;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEFAULTS;

/**
 * @author Erik Wienhold
 */
public final class Utilities {

  /**
   * Answer useful when mocking abstract classes.
   *
   * @see <a href="http://blogs.agilefaqs.com/2013/12/05/mocking-only-abstract-methods-using-mockito-partial-mocking/">
   * http://blogs.agilefaqs.com/2013/12/05/mocking-only-abstract-methods-using-mockito-partial-mocking/</a>
   */
  public static final Answer<Object> MOCKS_ABSTRACT_CLASS = new Answer<Object>() {

    @Override
    public Object answer(final InvocationOnMock invocation) throws Throwable {
      final Answer<Object> answer;
      if (isAbstract(invocation.getMethod().getModifiers())) {
        answer = RETURNS_DEFAULTS;
      } else {
        answer = CALLS_REAL_METHODS;
      }
      return answer.answer(invocation);
    }

  };

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
