/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import java.net.URI;

import vsr.cobalt.models.Identifiable;
import vsr.cobalt.models.Identifier;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public abstract class IdentifiableMaker<T extends Identifiable> implements Maker<T> {

  protected final AtomicValue<Identifier> identifier = new AtomicValue<>();

  public IdentifiableMaker<T> withIdentifier(final Identifier identifier) {
    this.identifier.set(identifier);
    return this;
  }

  public IdentifiableMaker<T> withIdentifier(final String id) {
    identifier.set(Identifier.create(id));
    return this;
  }

  public IdentifiableMaker<T> withIdentifier(final URI uri) {
    identifier.set(Identifier.create(uri));
    return this;
  }

}
