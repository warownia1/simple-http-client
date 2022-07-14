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

package io.github.warownia1.simplehttpclient.impl;

import io.github.warownia1.simplehttpclient.HttpClient;
import io.github.warownia1.simplehttpclient.HttpHeaders;
import io.github.warownia1.simplehttpclient.HttpRequest;
import io.github.warownia1.simplehttpclient.HttpResponse;
import io.github.warownia1.simplehttpclient.HttpResponse.BodyHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class SimpleHttpClient implements HttpClient {

  @Override
  public <T> HttpResponse<T> send(HttpRequest request, BodyHandler<T> handler)
      throws IOException {
    HttpURLConnection conn = (HttpURLConnection) request.uri().toURL().openConnection();
    if (!request.method().equals("HEAD")) {
      conn.setDoInput(true);
    }
    conn.setRequestMethod(request.method());
    request.headers().map().forEach((header, values) -> {
      for (String value : values) {
        conn.addRequestProperty(header, value);
      }
    });
    if (request.body().isPresent() && request.body().get().contentLength() != 0) {
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Length",
          Long.toString(request.body().get().contentLength()));
    }
    else {
      conn.setRequestProperty("Content-Length", "0");
    }
    if (request.timeout().isPresent()) {
      int timeoutMillis = (int) Math.min(request.timeout().get().toMillis(), Integer.MAX_VALUE);
      conn.setConnectTimeout(timeoutMillis);
      conn.setReadTimeout(timeoutMillis);
    }
    conn.connect();
    if (conn.getDoOutput()) {
      HttpRequest.Body body = request.body().get();
      OutputStream os = conn.getOutputStream();
      os.write(body.getBytes());
    }
    int statusCode = conn.getResponseCode();
    var headersMap = new LinkedHashMap<>(conn.getHeaderFields());
    // URLConnection maps null to status line
    headersMap.remove(null);
    HttpHeaders headers = HttpHeaders.of(headersMap);
    T body = handler.apply(conn.getInputStream());
    URI uri;
    try {
      uri = conn.getURL().toURI();
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
      uri = request.uri();
    }
    return new SimpleHttpResponse<T>(statusCode, request, headers, body, uri);
  }

  @Override
  public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request,
      BodyHandler<T> handler, Executor executor) {
    Supplier<HttpResponse<T>> supplier = () -> {
      try {
        return send(request, handler);
      }
      catch (IOException e) {
        throw new CompletionException(e);
      }
    };
    return CompletableFuture.supplyAsync(supplier, executor);
  }

}
