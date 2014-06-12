/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import com.hp.hpl.jena.query.Dataset;
import org.testng.annotations.Test;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Type;
import vsr.cobalt.repository.semantic.finders.RequestOfferDistanceFinder;

import static org.testng.Assert.assertEquals;
import static vsr.cobalt.repository.semantic.Datasets.loadDataset;
import static vsr.cobalt.repository.semantic.Models.task;
import static vsr.cobalt.repository.semantic.Models.type;

@Test
public class RequestOfferDistanceFinderTest {

  @Test
  public void taskDistance() {
    final Dataset ds = loadDataset("distance/tasks.n3");
    final RequestOfferDistanceFinder df = new RequestOfferDistanceFinder(ds);

    final Task t0 = task(0);
    final Task t2 = task(2);

    assertEquals(df.getDistance(t0, t2), 2);
  }

  @Test
  public void typeDistance() {
    final Dataset ds = loadDataset("distance/types.n3");
    final RequestOfferDistanceFinder df = new RequestOfferDistanceFinder(ds);

    final Type y0 = type(0);
    final Type y2 = type(2);

    assertEquals(df.getDistance(y0, y2), 2);
  }

}
