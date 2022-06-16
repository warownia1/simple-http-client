/*
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * The code has been adapted by Mateusz Warowny for use with the
 * simple http client - a java2script compatible http client. Please contact
 * mmzwarowny@dundee.ac.uk if you need additional information.
 */

package io.github.warownia1.simplehttpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * An HTTP Response
 *
 * A {@code HttpResponse} is not created directly, but rather returned as
 * a result of sending {@link HttpRequest}.
 *
 * This class provides methods for accessing the response status code,
 * headers, the body, and the {@code HttpRequest} associated with this
 * response.
 *
 * @param <T> the response body type
 */
public interface HttpResponse<T> {

  /**
   * A handler for response bodies. The class {@link BodyHandlers} provides
   * implementations of common body handlers.
   *
   * The {@code BodyHandler} interface is responsible for creating response
   * bodies. It consumes the actual response body bytes from the
   * {@code InputStream} and, typically, converts them into a higher-level
   * Java type.
   *
   * A {@code BodyHandler} is a function that takes a {@code InputStream}
   * and returns a higher-level response body object which can be later
   * retrieved from {@code HttpResponse}.
   *
   * @param <T> the response body type
   */
  @FunctionalInterface
  interface BodyHandler<T> {
    T apply(InputStream stream) throws IOException;
  }

  /**
   * Implementations of {@link BodyHandler} that provide some useful handlers
   * such as handling the response body as a {@code String}.
   */
  class BodyHandlers {
    private BodyHandlers() {
    }

    /**
     * Returns a {@code BodyHandler<String>} that returns a response body
     * as a String using default charset.
     *
     * @return a response body handler
     */
    public static BodyHandler<String> ofString() {
      return stream -> new String(stream.readAllBytes());
    }

    public static BodyHandler<Void> discarding() {
      return stream -> null;
    }

    /**
     * Returns a {@code BodyHandler<InputStream>} that returns a response
     * body as an {@link InputStream} from which the body can be read as
     * it is received.
     *
     * @return a response body handler
     */
    public static BodyHandler<InputStream> ofInputStream() {
      return stream -> stream;
    }
  }

  /**
   * Returns the status code for this response.
   *
   * @return the response code
   */
  int statusCode();

  /**
   * Returns the {@link HttpRequest} corresponding to this response.
   *
   * @return the request
   */
  HttpRequest request();

  /**
   * Returns the response headers received.
   *
   * @return the response headers
   */
  HttpHeaders headers();

  /**
   * Returns the body. Depending on the type of {@code T}, the returned body
   * may represent the data after it was read or may represent and object with
   * which the body is read.
   *
   * @return the response body
   */
  T body();

  /**
   * Returns the URI the response was received from. This may be different from
   * the request URI if redirection occurred.
   *
   * @return the URI of the response
   */
  URI uri();
}
