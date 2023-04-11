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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class JQueryHttpClient implements HttpClient {

  @Override
  public <T> HttpResponse<T> send(HttpRequest request, BodyHandler<T> handler)
      throws IOException {
    Object jqXHR = doAjax(request, false);
    return responseFromJqXHR(request, handler, jqXHR);
  }

  @FunctionalInterface
  interface AjaxDoneConsumer {
    void accept(String data, String textStatus, Object jqXHR);
  }

  @FunctionalInterface
  interface AjaxFailConsumer {
    void accept(Object jqXHR, String textStatus, String errorThrown);
  }

  @Override
  public <T> CompletableFuture<HttpResponse<T>> sendAsync(
      HttpRequest request, BodyHandler<T> handler, Executor executor) {
    final var responseFuture = new CompletableFuture<HttpResponse<T>>();
    AjaxDoneConsumer doneConsumer = (data, textStatus, jqXHR) -> {
      try {
        HttpResponse<T> response = responseFromJqXHR(request, handler, jqXHR);
        responseFuture.complete(response);
      }
      catch (Exception e) {
        responseFuture.completeExceptionally(e);
      }
    };
    AjaxFailConsumer failConsumer = (jqXHR, textStatus, errorThrown) -> {
      responseFuture.completeExceptionally(new IOException(errorThrown));
    };
    Object XHRHandler = doAjax(request, true);
    /** @j2sNative
     * XHRHandler.done(doneConsumer.accept$S$S$O);
     * XHRHandler.fail(failConsumer.accept$O$S$S);
     */
    return responseFuture;
  }

  private <T> HttpResponse<T> responseFromJqXHR(
      HttpRequest request, BodyHandler<T> handler, Object jqXHR) throws IOException {
    int statusCode = /** @j2sNative jqXHR.status || */0;
    HttpHeadersBuilder responseHeaders = new HttpHeadersBuilder();
    String allHeadersText = /** @j2sNative jqXHR.getAllResponseHeaders() || */"";
    for (String line : allHeadersText.split("\\n")) {
      int idx = line.indexOf(':');
      if (idx < 0) continue;
      String header = line.substring(0, idx);
      for (String value : line.substring(idx + 1).trim().split(",")) {
        responseHeaders.addHeader(header, value.trim());
      }
    }
    String responseText = /** @j2sNative jqXHR.responseText || */"";
    InputStream stream = new ByteArrayInputStream(responseText.getBytes(StandardCharsets.UTF_8));
    HttpHeaders headers = responseHeaders.build();
    ResponseInfoImpl responseInfo = new ResponseInfoImpl(statusCode, headers, Version.HTTP_1_1);
    return new SimpleHttpResponse<>(
        statusCode, request, headers, handler.apply(responseInfo, stream),
        request.uri()
    );
  }


  private Object doAjax(HttpRequest request, boolean async) {
    String url = request.uri().toString();
    String method = request.method();
    Object headers = /** @j2sNative {} || */null;
    for (Map.Entry entry : request.headers().map().entrySet()) {
      /** @j2sNative
       * headers[entry.getKey$()] = entry.getValue$().toArray$();
       */
    }
    byte[] data = null;
    if (request.body().isPresent()) {
      data = request.body().get().getBytes();
    }
    long timeout = 0;
    if (request.timeout().isPresent()) {
      timeout = request.timeout().get().toMillis();
    }
    /** @j2sNative
     * return jQuery.ajax(url, {
     *  async: async,
     *  contentType: false,
     *  data: data,
     *  dataType: "text",
     *  headers: headers,
     *  method: method,
     *  processData: false,
     *  timeout: timeout
     * })
     */
    {
      return null;
    }
  }

}
