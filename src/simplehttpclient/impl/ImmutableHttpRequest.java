/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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

package simplehttpclient.impl;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import simplehttpclient.HttpHeaders;
import simplehttpclient.HttpRequest;

import static java.util.Objects.requireNonNull;

public final class ImmutableHttpRequest extends HttpRequest {

  private final String method;
  private final URI uri;
  private final HttpHeaders headers;
  private final Body body;
  private final Duration timeout;

  ImmutableHttpRequest(SimpleHttpRequestBuilder builder) {
    this.method = requireNonNull(builder.method);
    this.uri = requireNonNull(builder.uri);
    this.headers = HttpHeaders.of(builder.headers.map());
    this.body = builder.body;
    this.timeout = builder.timeout;
  }

  @Override
  public String method() {
    return method;
  }

  @Override
  public URI uri() {
    return uri;
  }

  @Override
  public HttpHeaders headers() {
    return headers;
  }

  @Override
  public Optional<Body> body() {
    return Optional.ofNullable(body);
  }

  @Override
  public Optional<Duration> timeout() {
    return Optional.ofNullable(timeout);
  }

  @Override
  public String toString() {
    return uri.toString() + " " + method;
  }
}
