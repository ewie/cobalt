/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

/**
 * A mashup planner is the combined strategy of graph creation, extension, and plan extraction.
 *
 * @author Erik Wienhold
 */
public interface MashupPlanner extends GraphFactory, GraphExtender, PlanExtractor {
}
