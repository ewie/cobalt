/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph.makers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.RealizedFunctionality;
import vsr.cobalt.models.makers.ActionMaker;
import vsr.cobalt.models.makers.FunctionalityMaker;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;

/**
 * @author Erik Wienhold
 */
public class FunctionalityProvisionMaker implements Maker<FunctionalityProvision> {

  private final AtomicValue<Functionality> request = new AtomicValue<>();

  private final AtomicValue<Functionality> offer = new AtomicValue<>();

  private final AtomicValue<Action> action = new AtomicValue<>();

  public static FunctionalityProvisionMaker aFunctionalityProvision() {
    return new FunctionalityProvisionMaker();
  }

  public static FunctionalityProvisionMaker aMinimalFunctionalityProvision() {
    final FunctionalityMaker f = aMinimalFunctionality();
    final ActionMaker action = aMinimalAction().withFunctionality(f);
    return aFunctionalityProvision()
        .withProvidingAction(action)
        .withOffer(f)
        .withRequest(f);
  }

  @Override
  public FunctionalityProvision make() {
    return new FunctionalityProvision(request.get(), new RealizedFunctionality(offer.get(), action.get()));
  }

  public FunctionalityProvisionMaker withRequest(final Maker<Functionality> maker) {
    request.set(maker);
    return this;
  }

  public FunctionalityProvisionMaker withRequest(final Functionality functionality) {
    request.set(functionality);
    return this;
  }

  public FunctionalityProvisionMaker withOffer(final Maker<Functionality> maker) {
    offer.set(maker);
    return this;
  }

  public FunctionalityProvisionMaker withOffer(final Functionality functionality) {
    offer.set(functionality);
    return this;
  }

  public FunctionalityProvisionMaker withProvidingAction(final Maker<Action> maker) {
    action.set(maker);
    return this;
  }

  public FunctionalityProvisionMaker withProvidingAction(final Action action) {
    this.action.set(action);
    return this;
  }

}
