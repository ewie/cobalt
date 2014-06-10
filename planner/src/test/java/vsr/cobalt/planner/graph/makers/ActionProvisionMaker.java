/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph.makers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.makers.PropertyMaker;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;

/**
 * @author Erik Wienhold
 */
public class ActionProvisionMaker implements Maker<ActionProvision> {

  private final AtomicValue<Action> request = new AtomicValue<>();

  private final AtomicValue<Action> precursor = new AtomicValue<>();

  private final CollectionValue<PropertyProvision> provisions = new CollectionValue<>();

  public static ActionProvisionMaker anActionProvision() {
    return new ActionProvisionMaker();
  }

  public static ActionProvisionMaker aMinimalActionProvision() {
    final PropertyMaker p = aMinimalProperty();
    return anActionProvision()
        .withRequest(aMinimalAction()
            .withPre(aPropositionSet()
                .withCleared(p)))
        .withPrecursor(aMinimalAction()
            .withEffects(aPropositionSet()
                .withCleared(p)));
  }

  @Override
  public ActionProvision make() {
    final Action r = request.get();
    final Action p = precursor.get();
    if (p == null) {
      return ActionProvision.createWithoutPrecursor(r, provisions.asSet());
    }
    return ActionProvision.createWithPrecursor(r, p, provisions.asSet());
  }

  public ActionProvisionMaker withRequest(final Action request) {
    this.request.set(request);
    return this;
  }

  public ActionProvisionMaker withRequest(final Maker<Action> maker) {
    request.set(maker);
    return this;
  }

  public ActionProvisionMaker withPrecursor(final Action precursor) {
    this.precursor.set(precursor);
    return this;
  }

  public ActionProvisionMaker withPrecursor(final Maker<Action> maker) {
    precursor.set(maker);
    return this;
  }

  public ActionProvisionMaker withProvision(final Maker<PropertyProvision> maker) {
    provisions.add(maker);
    return this;
  }

  public ActionProvisionMaker withProvision(final PropertyProvision... provisions) {
    this.provisions.addValues(provisions);
    return this;
  }

}
