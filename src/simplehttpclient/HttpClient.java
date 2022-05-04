/*
 * Copyright (c) 2022, Mateusz Warowny.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. This particular file is
 * subject to the "Classpath" exception as provided in the LICENSE file
 * that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Mateusz Warowny, mmzwarowny@dundee.ac.uk if you need
 * additional information.
 */

package simplehttpclient;

import simplehttpclient.impl.JQueryHttpClient;
import simplehttpclient.impl.SimpleHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * An HTTP Client.
 * <p>
 * An {@code HttpClient} can be used to send {@linkplain HttpRequest HTTP requests} and
 * retrieve their {@linkplain HttpResponse responses}. A new client is created through a
 * {@link #newHttpClient()} method, which instantiates the {@link HttpClient} implementation
 * based on the current execution environment being either JRE or javascript. One
 * {@code HttpClient} can be used to send multiple requests.
 * <p>
 * A {@link HttpResponse.BodyHandler BodyHandler} must be supplied for each
 * {@link HttpRequest} sent. The {@code BodyHandler} processes the response body bytes
 * received from the server and re-packs them into a higher-level java object. Once a
 * {@link HttpResponse} is received, the response code, headers and body are available.
 */
public interface HttpClient {

  /**
   * Returns a new {@code HttpClient}.
   * <p>
   * The returned implementation of an {@code HttpClient} depends on the platform. For JRE a
   * {@link SimpleHttpClient} is used, and for javascript a {@link JQueryHttpClient} is
   * used.
   *
   * @return a new HttpClient
   */
  static HttpClient newHttpClient() {
    if (/** @j2sNative false && */true)
      return new SimpleHttpClient();
    else
      return new JQueryHttpClient();
  }

  /**
   * Sends the given request and returns a response.
   * <p>
   * The method is blocking until the response is received. The returned
   * {@link HttpResponse} contains the response status, header and body as handled by a
   * given response handler.
   *
   * @param request the request
   * @param handler the response body handler
   * @param <T> the response body type
   * @return the response
   * @throws IOException if an IO error occurs during sending, receiving or processing
   *     the response
   */
  <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler)
      throws IOException;

  /**
   * Sends the given request asynchronously using provided {@link Executor}.
   *
   * @param request the request
   * @param handler the response body handler
   * @param executor the executor
   * @return the response wrapped in a future
   * @param <T> the response body type
   */
  <T> CompletableFuture<HttpResponse<T>> sendAsync(
      HttpRequest request, HttpResponse.BodyHandler<T> handler, Executor executor);
}
