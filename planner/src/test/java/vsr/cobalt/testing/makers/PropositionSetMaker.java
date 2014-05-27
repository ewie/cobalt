/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.makers;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.PropositionSet;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public class PropositionSetMaker implements Maker<PropositionSet> {

  private CollectionValue<Property> cleared = new CollectionValue<>();

  private CollectionValue<Property> filled = new CollectionValue<>();

  public static PropositionSetMaker aPropositionSet() {
    return new PropositionSetMaker();
  }

  @Override
  public PropositionSet make() {
    return new PropositionSet(
        ImmutableSet.copyOf(cleared.get()),
        ImmutableSet.copyOf(filled.get()));
  }

  public PropositionSetMaker withCleared(final ImmutableSet<Property> properties) {
    cleared = new CollectionValue<>(properties);
    return this;
  }

  public PropositionSetMaker withCleared(final Property... properties) {
    cleared.addValues(properties);
    return this;
  }

  public PropositionSetMaker withCleared(final Maker<Property> maker) {
    cleared.add(maker);
    return this;
  }

  public PropositionSetMaker withFilled(final ImmutableSet<Property> properties) {
    filled = new CollectionValue<>(properties);
    return this;
  }

  public PropositionSetMaker withFilled(final Property... properties) {
    filled.addValues(properties);
    return this;
  }

  public PropositionSetMaker withFilled(final Maker<Property> maker) {
    filled.add(maker);
    return this;
  }

}
