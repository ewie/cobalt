/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import java.util.Collection;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PropositionSet;
import vsr.cobalt.models.Widget;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;

/**
 * @author Erik Wienhold
 */
public class ActionMaker implements Maker<Action> {

  private final AtomicValue<Widget> widget = new AtomicValue<>();

  private final AtomicValue<PropositionSet> pre = new AtomicValue<>();

  private final AtomicValue<PropositionSet> effects = new AtomicValue<>();

  private final CollectionValue<Property> pubs = new CollectionValue<>();

  private final CollectionValue<Functionality> functionalities = new CollectionValue<>();

  private final CollectionValue<Interaction> interactions = new CollectionValue<>();

  public static ActionMaker anAction() {
    return new ActionMaker();
  }

  public static ActionMaker aMinimalAction() {
    return anAction()
        .withWidget(aMinimalWidget())
        .withPre(aPropositionSet())
        .withEffects(aPropositionSet());
  }

  @Override
  public Action make() {
    return Action.create(
        widget.get(),
        pre.get(),
        effects.get(),
        pubs.asSet(),
        functionalities.asSet(),
        interactions.asSet());
  }

  public ActionMaker withWidget(final Widget widget) {
    this.widget.set(widget);
    return this;
  }

  public ActionMaker withWidget(final Maker<Widget> maker) {
    widget.set(maker);
    return this;
  }

  public ActionMaker withPre(final PropositionSet pre) {
    this.pre.set(pre);
    return this;
  }

  public ActionMaker withPre(final Maker<PropositionSet> maker) {
    pre.set(maker);
    return this;
  }

  public ActionMaker withEffects(final PropositionSet effects) {
    this.effects.set(effects);
    return this;
  }

  public ActionMaker withEffects(final Maker<PropositionSet> effects) {
    this.effects.set(effects);
    return this;
  }

  public ActionMaker withPubs(final Collection<Property> properties) {
    pubs.set(properties);
    return this;
  }

  public ActionMaker withPub(final Maker<Property> property) {
    pubs.add(property);
    return this;
  }

  public ActionMaker withPub(final Property... properties) {
    pubs.addValues(properties);
    return this;
  }

  public ActionMaker withPub(final Property property) {
    pubs.addValues(property);
    return this;
  }

  public ActionMaker withFunctionalities(final Collection<Functionality> functionalities) {
    this.functionalities.set(functionalities);
    return this;
  }

  @SafeVarargs
  public final ActionMaker withFunctionality(final Maker<Functionality>... makers) {
    functionalities.add(makers);
    return this;
  }

  public ActionMaker withFunctionality(final Functionality... functionalities) {
    this.functionalities.addValues(functionalities);
    return this;
  }

  public ActionMaker withInteractions(final Collection<Interaction> interactions) {
    this.interactions.set(interactions);
    return this;
  }

  @SafeVarargs
  public final ActionMaker withInteraction(final Maker<Interaction>... makers) {
    interactions.add(makers);
    return this;
  }

  public ActionMaker withInteraction(final Interaction... interactions) {
    this.interactions.addValues(interactions);
    return this;
  }

}
