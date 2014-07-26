/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph.makers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;
import vsr.cobalt.models.makers.ActionMaker;
import vsr.cobalt.models.makers.PropertyMaker;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;

/**
 * @author Erik Wienhold
 */
public class PropertyProvisionMaker implements Maker<PropertyProvision> {

  private final AtomicValue<Property> request = new AtomicValue<>();

  private final AtomicValue<Property> offer = new AtomicValue<>();

  private final AtomicValue<Action> action = new AtomicValue<>();

  public static PropertyProvisionMaker aPropertyProvision() {
    return new PropertyProvisionMaker();
  }

  public static PropertyProvisionMaker aMinimalPropertyProvision() {
    final PropertyMaker p = aMinimalProperty();
    final ActionMaker a = aMinimalAction()
        .withEffects(aPropositionSet()
            .withFilled(p));
    return aPropertyProvision()
        .withProvidingAction(a)
        .withOffer(p)
        .withRequest(p);
  }

  @Override
  public PropertyProvision make() {
    return new PropertyProvision(request.get(), new PublishedProperty(offer.get(), action.get()));
  }

  public PropertyProvisionMaker withRequest(final Maker<Property> maker) {
    request.set(maker);
    return this;
  }

  public PropertyProvisionMaker withRequest(final Property property) {
    request.set(property);
    return this;
  }

  public PropertyProvisionMaker withOffer(final Maker<Property> maker) {
    offer.set(maker);
    return this;
  }

  public PropertyProvisionMaker withOffer(final Property property) {
    offer.set(property);
    return this;
  }

  public PropertyProvisionMaker withProvidingAction(final Maker<Action> maker) {
    action.set(maker);
    return this;
  }

  public PropertyProvisionMaker withProvidingAction(final Action action) {
    this.action.set(action);
    return this;
  }

}
