/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph.makers;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aMinimalFunctionalityProvision;

/**
 * @author Erik Wienhold
 */
public class InitialLevelMaker implements Maker<InitialLevel> {

  private final CollectionValue<FunctionalityProvision> provisions = new CollectionValue<>();

  public static InitialLevelMaker anInitialLevel() {
    return new InitialLevelMaker();
  }

  public static InitialLevelMaker aMinimalInitialLevel() {
    return anInitialLevel().withFunctionalityProvision(aMinimalFunctionalityProvision());
  }

  @Override
  public InitialLevel make() {
    return new InitialLevel(ImmutableSet.copyOf(provisions.get()));
  }

  @SafeVarargs
  public final InitialLevelMaker withFunctionalityProvision(final Maker<FunctionalityProvision>... makers) {
    provisions.add(makers);
    return this;
  }

  public InitialLevelMaker withFunctionalityProvision(final FunctionalityProvision... provisions) {
    this.provisions.addValues(provisions);
    return this;
  }

}
