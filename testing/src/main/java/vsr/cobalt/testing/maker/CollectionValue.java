/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.maker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Erik Wienhold
 */
public class CollectionValue<T> implements Value<Collection<T>> {

  private final Collection<T> values = new ArrayList<>();

  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  public Collection<T> get() {
    return values;
  }

  @Override
  public void set(final Collection<T> values) {
    this.values.clear();
    this.values.addAll(values);
  }

  @SafeVarargs
  public final void add(final Maker<T>... makers) {
    add(Arrays.asList(makers));
  }

  public void add(final Collection<Maker<T>> makers) {
    final Collection<T> values = new ArrayList<>(makers.size());
    for (final Maker<T> maker : makers) {
      values.add(maker.make());
    }
    this.values.addAll(values);
  }

  @SafeVarargs
  public final void addValues(final T... values) {
    addValues(Arrays.asList(values));
  }

  public void addValues(final Collection<T> values) {
    this.values.addAll(values);
  }

}
