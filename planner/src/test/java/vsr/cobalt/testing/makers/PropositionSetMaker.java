/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.makers;

import java.util.Collection;

import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.PropositionSet;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public class PropositionSetMaker implements Maker<PropositionSet> {

  private final CollectionValue<Property> cleared = new CollectionValue<>();

  private final CollectionValue<Property> filled = new CollectionValue<>();

  public static PropositionSetMaker aPropositionSet() {
    return new PropositionSetMaker();
  }

  @Override
  public PropositionSet make() {
    return new PropositionSet(cleared.asSet(), filled.asSet());
  }

  public PropositionSetMaker withCleared(final Collection<Property> properties) {
    cleared.set(properties);
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

  public PropositionSetMaker withFilled(final Collection<Property> properties) {
    filled.set(properties);
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
