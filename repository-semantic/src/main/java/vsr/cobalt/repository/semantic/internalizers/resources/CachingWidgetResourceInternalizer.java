/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic.internalizers.resources;

import com.hp.hpl.jena.rdf.model.Resource;
import vsr.cobalt.models.Widget;
import vsr.cobalt.repository.semantic.internalizers.CachingResourceInternalizer;

/**
 * @author Erik Wienhold
 */
public class CachingWidgetResourceInternalizer extends CachingResourceInternalizer<Widget> {

  @Override
  protected Widget create(final Resource r) {
    return new Widget(r.getURI());
  }

}
