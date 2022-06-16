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

package io.github.warownia1.simplehttpclient.impl;

import io.github.warownia1.simplehttpclient.HttpRequest;

import java.net.URI;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class SimpleHttpRequestBuilder implements HttpRequest.Builder {

  URI uri;
  String method = "GET";
  HttpHeadersBuilder headers = new HttpHeadersBuilder();
  HttpRequest.Body body = EmptyRequestBody.getInstance();
  Duration timeout;

  @Override
  public SimpleHttpRequestBuilder uri(URI uri) {
    requireNonNull(uri);
    checkURI(uri);
    this.uri = uri;
    return this;
  }

  static void checkURI(URI uri) {
    String scheme = uri.getScheme();
    if (scheme == null)
      throw new IllegalArgumentException("URI with undefined scheme");
    scheme = scheme.toLowerCase(Locale.US);
    if (!scheme.equals("https") && !scheme.equals("http"))
      throw new IllegalArgumentException(format("invalid URI scheme %s", scheme));
    if (uri.getHost() == null)
      throw new IllegalArgumentException(format("unsupported URI %s", uri));
  }

  @Override
  public SimpleHttpRequestBuilder header(String name, String value) {
    requireNonNull(name);
    requireNonNull(value);
    headers.addHeader(name, value);
    return this;
  }

  @Override
  public SimpleHttpRequestBuilder HEAD() {
    return method("HEAD", EmptyRequestBody.getInstance());
  }

  @Override
  public SimpleHttpRequestBuilder GET() {
    return method("GET", EmptyRequestBody.getInstance());
  }

  @Override
  public SimpleHttpRequestBuilder POST(HttpRequest.Body body) {
    Objects.requireNonNull(body);
    return method("POST", body);
  }

  @Override
  public SimpleHttpRequestBuilder PUT(HttpRequest.Body body) {
    Objects.requireNonNull(body);
    return method("PUT", body);
  }

  @Override
  public SimpleHttpRequestBuilder DELETE() {
    return method("DELETE", EmptyRequestBody.getInstance());
  }

  @Override
  public SimpleHttpRequestBuilder method(String method, HttpRequest.Body body) {
    Objects.requireNonNull(method);
    if (method.equals(""))
      throw new IllegalArgumentException("illegal method <empty string>");
    this.method = method;
    this.body = requireNonNull(body);
    return this;
  }

  @Override
  public SimpleHttpRequestBuilder timeout(Duration duration) {
    if (duration == null) {
      this.timeout = null;
      return this;
    }
    if (duration.isNegative() || duration.isZero()) {
      throw new IllegalArgumentException("invalid duration: " + duration);
    }
    this.timeout = duration;
    return this;
  }

  @Override
  public ImmutableHttpRequest build() {
    if (uri == null)
      throw new IllegalStateException("uri is null");
    return new ImmutableHttpRequest(this);
  }
}
