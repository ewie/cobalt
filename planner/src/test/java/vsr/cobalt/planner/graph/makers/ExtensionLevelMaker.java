/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph.makers;

import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.aMinimalActionProvision;

/**
 * @author Erik Wienhold
 */
public class ExtensionLevelMaker implements Maker<ExtensionLevel> {

  private final CollectionValue<ActionProvision> provisions = new CollectionValue<>();

  public static ExtensionLevelMaker anExtensionLevel() {
    return new ExtensionLevelMaker();
  }

  public static ExtensionLevelMaker aMinimalExtensionLevel() {
    return new ExtensionLevelMaker().withProvision(aMinimalActionProvision());
  }

  @Override
  public ExtensionLevel make() {
    return new ExtensionLevel(provisions.asSet());
  }

  public ExtensionLevelMaker withProvision(final Maker<ActionProvision> provision) {
    provisions.add(provision);
    return this;
  }

  public ExtensionLevelMaker withProvision(final ActionProvision... provisions) {
    this.provisions.addValues(provisions);
    return this;
  }

}
