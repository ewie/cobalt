/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors;

import java.util.AbstractQueue;
import java.util.Queue;

import com.google.common.collect.Iterators;
import org.testng.annotations.Test;
import vsr.cobalt.planner.Plan;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.testing.Assert.assertSubClass;

@Test
public class QueueingPlanCollectorTest {

  @Test
  public void extendsAbstractQueue() {
    assertSubClass(QueueingPlanCollector.class, AbstractQueue.class);
  }

  @Test
  public void delegateSize() {
    final Queue<Object> q = mockQueue();
    final DummyCollector c = new DummyCollector(q);
    c.size();
    verify(q).size();
    assertEquals(c.size(), q.size());
  }

  @Test
  public void delegateOffer() {
    final Queue<Object> q = mockQueue();
    final DummyCollector c = new DummyCollector(q);
    final Object o = new Object();
    c.offer(o);
    verify(q).offer(o);
  }

  @Test
  public void delegatePoll() {
    final Queue<Object> q = mockQueue();
    final DummyCollector c = new DummyCollector(q);
    c.poll();
    verify(q).poll();
    assertSame(c.poll(), q.poll());
  }

  @Test
  public void delegateIterator() {
    final Queue<Object> q = mockQueue();
    when(q.iterator()).thenReturn(Iterators.emptyIterator());
    final DummyCollector c = new DummyCollector(q);
    assertSame(c.iterator(), q.iterator());
  }

  @SuppressWarnings("unchecked")
  private static Queue<Object> mockQueue() {
    return (Queue<Object>) mock(Queue.class);
  }

  private static class DummyCollector extends QueueingPlanCollector<Object> {

    public DummyCollector(final Queue<Object> queue) {
      super(queue);
    }

    @Override
    public void collect(final Plan plan) {
      // empty
    }
  }

}
