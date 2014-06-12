/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.utils;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * An iterator mapping {@link RDFNode} to {@link Resource}.
 *
 * @author Erik Wienhold
 */
public class NodeToResourceMapper extends AbstractIterator<Resource> {

  private final Iterator<RDFNode> nodes;

  public NodeToResourceMapper(final Iterator<RDFNode> nodes) {
    this.nodes = nodes;
  }

  @Override
  public Resource computeNext() {
    while (nodes.hasNext()) {
      final RDFNode node = nodes.next();
      if (node.isResource()) {
        return node.asResource();
      }
    }
    return endOfData();
  }

}
