/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.makers;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.planner.models.EffectSet;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public class EffectSetMaker implements Maker<EffectSet> {

  private final CollectionValue<Property> toClear = new CollectionValue<>();

  private final CollectionValue<Property> toFill = new CollectionValue<>();

  public static EffectSetMaker anEffectSet() {
    return new EffectSetMaker();
  }

  @Override
  public EffectSet make() {
    return new EffectSet(
        ImmutableSet.copyOf(toClear.get()),
        ImmutableSet.copyOf(toFill.get()));
  }

  public EffectSetMaker withToClear(final Collection<Property> properties) {
    toClear.set(properties);
    return this;
  }

  public EffectSetMaker withToClear(final Property... properties) {
    toClear.addValues(properties);
    return this;
  }

  public EffectSetMaker withToClear(final Maker<Property> maker) {
    toClear.add(maker);
    return this;
  }

  public EffectSetMaker withToFill(final Collection<Property> properties) {
    toFill.set(properties);
    return this;
  }

  public EffectSetMaker withToFill(final Property... properties) {
    toFill.addValues(properties);
    return this;
  }

  public EffectSetMaker withToFill(final Maker<Property> maker) {
    toFill.add(maker);
    return this;
  }

}
