/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;

import vsr.cobalt.planner.PlanCollector;

/**
 * A queue-based plan collector.
 *
 * @author Erik Wienhold
 */
abstract class QueueingPlanCollector<E> extends AbstractQueue<E> implements PlanCollector {

  private final Queue<E> queue;

  /**
   * @param queue a queue backing up the collector
   */
  public QueueingPlanCollector(final Queue<E> queue) {
    this.queue = queue;
  }

  @Override
  public Iterator<E> iterator() {
    return queue.iterator();
  }

  @Override
  public int size() {
    return queue.size();
  }

  @Override
  public boolean offer(final E e) {
    return queue.offer(e);
  }

  @Override
  public E poll() {
    return queue.poll();
  }

  @Override
  public E peek() {
    return queue.peek();
  }

}
