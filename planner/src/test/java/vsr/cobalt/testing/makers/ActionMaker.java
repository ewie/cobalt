/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.makers;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;
import vsr.cobalt.planner.models.PropositionSet;
import vsr.cobalt.planner.models.Task;
import vsr.cobalt.planner.models.Widget;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.testing.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.makers.WidgetMaker.aMinimalWidget;

/**
 * @author Erik Wienhold
 */
public class ActionMaker implements Maker<Action> {

  private final AtomicValue<Widget> widget = new AtomicValue<>();

  private final AtomicValue<PropositionSet> pre = new AtomicValue<>();

  private final AtomicValue<PropositionSet> post = new AtomicValue<>();

  private final CollectionValue<Property> pubs = new CollectionValue<>();

  private final CollectionValue<Task> tasks = new CollectionValue<>();

  public static ActionMaker anAction() {
    return new ActionMaker();
  }

  public static ActionMaker aMinimalAction() {
    return anAction()
        .withWidget(aMinimalWidget())
        .withPre(aPropositionSet())
        .withPost(aPropositionSet());
  }

  @Override
  public Action make() {
    return new Action(
        widget.get(),
        pre.get(),
        post.get(),
        ImmutableSet.copyOf(pubs.get()),
        ImmutableSet.copyOf(tasks.get()));
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

  public ActionMaker withPost(final PropositionSet post) {
    this.post.set(post);
    return this;
  }

  public ActionMaker withPost(final Maker<PropositionSet> post) {
    this.post.set(post);
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

  public ActionMaker withTasks(final Collection<Task> tasks) {
    this.tasks.set(tasks);
    return this;
  }

  @SafeVarargs
  public final ActionMaker withTask(final Maker<Task>... makers) {
    tasks.add(makers);
    return this;
  }

  public ActionMaker withTask(final Task... tasks) {
    this.tasks.addValues(tasks);
    return this;
  }

}
