/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

/**
 * @author Erik Wienhold
 */
public class InvalidModelException extends Exception {

  public InvalidModelException() {
  }

  public InvalidModelException(final String message) {
    super(message);
  }

  public InvalidModelException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidModelException(final Throwable cause) {
    super(cause);
  }

  public InvalidModelException(final String message, final Throwable cause, final boolean enableSuppression,
                               final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
