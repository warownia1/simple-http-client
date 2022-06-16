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

import io.github.warownia1.simplehttpclient.impl.SimpleHttpRequestBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * An HTTP request.
 *
 * <p> An {@code HttpRequest} instance is built through an {@code HttpRequest}
 * {@linkplain HttpRequest.Builder builder}. An {@code HttpRequest} builder is obtained from
 * one of the {@link HttpRequest#newBuilder(URI) newBuilder} methods. A request's
 * {@link URI}, headers, and body can be set. Request bodies are provided through a
 * {@link Body} supplied to one of the {@link Builder#POST(Body) POST},
 * {@link Builder#PUT(Body) PUT} or {@link Builder#method(String, Body) method} methods.
 * Once all required parameters have been set in the builder, {@link Builder#build() build}
 * will return the {@code HttpRequest}. Builders can be copied and modified many times in
 * order to build multiple related requests that differ in some parameters.
 *
 * <p> The following is an example of a GET request that prints the response
 * body as a String:
 *
 * <pre>{@code    HttpClient client = HttpClient.newHttpClient();
 *   HttpRequest request = HttpRequest.newBuilder()
 *         .uri(URI.create("http://foo.com/"))
 *         .build();
 *   client.sendAsync(request, BodyHandlers.ofString())
 *         .thenApply(HttpResponse::body)
 *         .thenAccept(System.out::println)
 *         .join(); }</pre>
 */
public abstract class HttpRequest {

  public interface Builder {
    /**
     * Sets this {@code HttpRequest}'s request {@code URI}.
     *
     * @param uri the request URI
     * @return this builder
     * @throws IllegalArgumentException if the {@code URI} scheme is not supported
     */
    Builder uri(URI uri);

    /**
     * Adds the given name value pair to the set of headers for this request. The given
     * value is added to the list of values for that name.
     *
     * @param name the header name
     * @param value the header value
     * @return this builder
     * @throws IllegalArgumentException if the header name or value is not valid, see <a
     *     href="https://tools.ietf.org/html/rfc7230#section-3.2">RFC 7230 section-3.2</a>,
     *     or the header name or value is restricted by the implementation.
     * @implNote An implementation may choose to restrict some header names or values,
     *     as the HTTP Client may determine their value itself. For example,
     *     "Content-Length", which will be determined by the request Publisher. In such a
     *     case, an implementation of {@code HttpRequest.Builder} may choose to throw an
     *     {@code IllegalArgumentException} if such a header is passed to the builder.
     */
    Builder header(String name, String value);

    /**
     * Sets the request method of this builder to HEAD.
     *
     * @return this builder
     */
    Builder HEAD();

    /**
     * Sets the request method of this builder to GET. This is the default.
     *
     * @return this builder
     */
    Builder GET();

    /**
     * Sets the request method of this builder to POST and sets its request body publisher
     * to the given value.
     *
     * @param body the request body
     * @return this builder
     */
    Builder POST(Body body);

    /**
     * Sets the request method of this builder to PUT and sets its request body publisher to
     * the given value.
     *
     * @param body the request body
     * @return this builder
     */
    Builder PUT(Body body);

    /**
     * Sets the request method of this builder to DELETE.
     *
     * @return this builder
     */
    Builder DELETE();

    /**
     * Sets the request method and request body of this bulder to the given values.
     *
     * @param method the method to use
     * @param body the request body
     * @return this builder
     */
    Builder method(String method, Body body);

    /**
     * Sets the timeout for this request.
     * <p>
     * If the response is not received within the specified timeout than an
     * {@link java.net.SocketTimeoutException SocketTimeoutException} is thrown from
     * {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler) HttpClient.send} or
     * {@link HttpClient#sendAsync(HttpRequest, HttpResponse.BodyHandler, Executor)
     * HttpClient.sendAsync} completes exceptionally with an {@code SocketTimeoutException}.
     * The effect of not setting a timeout is the same as setting an infinite Duration, i.e.
     * block forever.
     *
     * @param duration the timeout duration
     * @return this builder
     * @throws IllegalArgumentException if the duration is non-positive
     */
    Builder timeout(Duration duration);


    /**
     * Builds and returns an {@link HttpRequest}.
     *
     * @return a new {@code HttpRequest}
     * @throws IllegalStateException if a URI has not been set
     */
    HttpRequest build();
  }

  public interface Body {
    byte[] getBytes();

    long contentLength();
  }

  public static Builder newBuilder(URI uri) {
    return newBuilder().uri(uri);
  }

  public static Builder newBuilder() {
    return new SimpleHttpRequestBuilder();
  }

  public abstract Optional<Body> body();

  public abstract String method();

  public abstract URI uri();

  public abstract HttpHeaders headers();

  public abstract Optional<Duration> timeout();
}
