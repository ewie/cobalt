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
import vsr.cobalt.planner.graph.Provision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Widget;
import vsr.cobalt.utils.OrderedPowerSetIterator;

/**
 * A provision provider which composes providing actions when possible.
 *
 * @param <T> a provision subject type
 * @param <P> a provision type with subject type {@link T}
 *
 * @author Erik Wienhold
 */
abstract class ComposingProvisionProvider<T, P extends Provision<T>> {

  /**
   * Get provisions matching any given request. The resulting provisions may have composite providing actions.
   *
   * @param requests a set of requests
   *
   * @return a set of provisions
   */
  public Set<P> getProvisionsFor(final Set<T> requests) {
    final Set<P> provisions = new HashSet<>();

    // Get matching provisions for each request and add them to the result set.
    for (final T r : requests) {
      provisions.addAll(findProvisionsFor(r));
    }

    // Index the provisions to efficiently create provisions with composite providing actions.
    final Index index = createIndex(provisions);

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
   * Get provisions matching a single request.
   *
   * @param request a request
   *
   * @return a set of matching provisions
   */
  protected abstract Set<P> findProvisionsFor(T request);

  /**
   * Create a provision
   *
   * @param request a request
   * @param offer   an offer
   * @param action  an action providing <var>offer</var>
   *
   * @return a new provision
   */
  protected abstract P createProvision(T request, T offer, Action action);

  /**
   * Get offering provided by an action.
   *
   * @param action an action
   *
   * @return a set of offered subjects
   */
  protected abstract Set<T> getOffers(Action action);

  private Index createIndex(final Set<P> provisions) {
    return new Index(provisions);
  }

  private class Index {

    /**
     * An iterable of action combinations for each widget.
     */
    public final Iterable<OrderedPowerSetIterator<Action>> actionCombinations = new CombinationsIterable();
    /**
     * Map requests to matching provisions.
     */
    private final SetMultimap<T, P> provisions = HashMultimap.create();
    /**
     * Map widgets to their actions.
     */
    private final SetMultimap<Widget, Action> actions = HashMultimap.create();

    public Index(final Set<P> provisions) {
      for (final P p : provisions) {
        this.provisions.put(p.getRequest(), p);
        final Action a = p.getProvidingAction();
        actions.put(a.getWidget(), a);
      }
    }

    /**
     * Create provisions for each offering of a providing action for which this index contains a matching provision.
     *
     * @param action a providing action
     *
     * @return a set of provisions
     */
    public Set<P> createProvisions(final Action action) {
      final Set<P> pps = new HashSet<>();
      for (final T r : getOffers(action)) {
        for (final P p : provisions.get(r)) {
          pps.add(createProvision(p.getRequest(), r, action));
        }
      }
      return pps;
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
