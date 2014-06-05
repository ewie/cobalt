/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.UnmodifiableIterator;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Offer;
import vsr.cobalt.models.Widget;
import vsr.cobalt.planner.graph.Provision;
import vsr.cobalt.utils.OrderedPowerSetIterator;

/**
 * A provision provider which composes providing actions when possible.
 *
 * @param <T> a subject type
 * @param <O> an offer type with subject type {@link T}
 * @param <P> a provision type with subject type {@link T}
 *
 * @author Erik Wienhold
 */
abstract class ComposingProvisionProvider<T, O extends Offer<T>, P extends Provision<T>> {

  /**
   * Get provisions matching any given request. The resulting provisions may have composite providing actions.
   *
   * @param requests a set of requests
   *
   * @return a set of provisions
   */
  public Set<P> getProvisionsFor(final Set<T> requests) {
    final Set<P> provisions = new HashSet<>();

    final Index index = new Index();

    // Get offers for each request and index them to efficiently create provisions with composite providing actions.
    for (final T r : requests) {
      final Set<O> os = getOffersFor(r);
      for (final O o : os) {
        index.add(r, o);
      }
    }

    // Create composite actions and applicable provisions.
    for (final OrderedPowerSetIterator<Action> it : index.actionCombinations) {
      while (it.hasNext()) {
        final Set<Action> actions = it.next();
        if (Action.isComposable(actions)) {
          provisions.addAll(index.createProvisions(Action.compose(actions)));
        }
      }
    }

    return provisions;
  }

  /**
   * Get offers for a single request.
   *
   * @param request a request
   *
   * @return a set of offers
   */
  protected abstract Set<O> getOffersFor(T request);

  /**
   * Create a provision.
   *
   * @param request a request
   * @param subject an offered subject
   * @param action  an action providing the subject
   *
   * @return a new provision
   */
  protected abstract P createProvision(T request, T subject, Action action);

  /**
   * Get subjects offered by an action.
   *
   * @param action an action
   *
   * @return a set of offered subjects
   */
  protected abstract Set<T> getOfferedSubjects(Action action);

  private class Index {

    /**
     * An iterable of action combinations for each widget.
     */
    public final Iterable<OrderedPowerSetIterator<Action>> actionCombinations = new CombinationsIterable();

    /**
     * Map widgets to their actions.
     */
    private final SetMultimap<Widget, Action> actions = HashMultimap.create();

    /**
     * Map subjects to their offers.
     */
    private final SetMultimap<T, O> subjects2offers = HashMultimap.create();

    /**
     * Map offers to matching request.
     */
    private final SetMultimap<O, T> offers2requests = HashMultimap.create();

    /**
     * Add a request and matching offer to the index.
     *
     * @param request a requested subject
     * @param offer   a offer for the request
     */
    public void add(final T request, final O offer) {
      final Action a = offer.getAction();
      actions.put(a.getWidget(), a);
      subjects2offers.put(offer.getSubject(), offer);
      offers2requests.put(offer, request);
    }

    /**
     * Create provisions for each offering of a providing action for which this index contains a matching provision.
     *
     * @param action a providing action
     *
     * @return a set of provisions
     */
    public Set<P> createProvisions(final Action action) {
      final Set<P> provisions = new HashSet<>();
      for (final T subject : getOfferedSubjects(action)) {
        for (final O offer : subjects2offers.get(subject)) {
          for (final T request : offers2requests.get(offer)) {
            provisions.add(createProvision(request, offer.getSubject(), action));
          }
        }
      }
      return provisions;
    }

    private class CombinationsIterable implements Iterable<OrderedPowerSetIterator<Action>> {

      @Override
      public CombinationsIterator iterator() {
        return new CombinationsIterator(actions.keySet().iterator());
      }

    }

    private class CombinationsIterator extends UnmodifiableIterator<OrderedPowerSetIterator<Action>> {

      private final Iterator<Widget> widgets;

      public CombinationsIterator(final Iterator<Widget> widgets) {
        this.widgets = widgets;
      }

      @Override
      public boolean hasNext() {
        return widgets.hasNext();
      }

      @Override
      public OrderedPowerSetIterator<Action> next() {
        return new OrderedPowerSetIterator<>(actions.get(widgets.next()));
      }

    }

  }

}
