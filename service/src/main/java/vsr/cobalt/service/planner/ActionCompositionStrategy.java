/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

/**
 * @author Erik Wienhold
 */
public class ActionCompositionStrategy {

  private final static ActionCompositionStrategy DEFAULT =
      new ActionCompositionStrategy(PrecursorCompositionStrategy.NONE, false, false);

  private final PrecursorCompositionStrategy precursorCompositionStrategy;

  private final boolean composeFunctionalityProviders;

  private final boolean composePropertyProviders;

  public ActionCompositionStrategy(final PrecursorCompositionStrategy precursorCompositionStrategy,
                                   final boolean composeFunctionalityProviders,
                                   final boolean composePropertyProviders) {
    this.precursorCompositionStrategy = precursorCompositionStrategy;
    this.composeFunctionalityProviders = composeFunctionalityProviders;
    this.composePropertyProviders = composePropertyProviders;
  }

  public static ActionCompositionStrategy getDefault() {
    return DEFAULT;
  }

  public PrecursorCompositionStrategy getPrecursorCompositionStrategy() {
    return precursorCompositionStrategy;
  }

  public boolean composeFunctionalityProviders() {
    return composeFunctionalityProviders;
  }

  public boolean composePropertyProviders() {
    return composePropertyProviders;
  }

}
