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

package simplehttpclient.impl;

import java.net.URI;

import simplehttpclient.HttpHeaders;
import simplehttpclient.HttpRequest;
import simplehttpclient.HttpResponse;

public class SimpleHttpResponse<T> implements HttpResponse<T> {

  private final int statusCode;
  private final HttpRequest request;
  private final HttpHeaders headers;
  private final T body;
  private final URI uri;

  public SimpleHttpResponse(int statusCode, HttpRequest request,
      HttpHeaders headers, T body, URI uri) {
    this.statusCode = statusCode;
    this.request = request;
    this.headers = headers;
    this.body = body;
    this.uri = uri;
  }

  @Override
  public int statusCode() {
    return statusCode;
  }

  @Override
  public HttpRequest request() {
    return request;
  }

  @Override
  public HttpHeaders headers() {
    return headers;
  }

  @Override
  public T body() {
    return body;
  }

  @Override
  public URI uri() {
    return uri;
  }

}
