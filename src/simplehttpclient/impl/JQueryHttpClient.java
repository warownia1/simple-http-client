package simplehttpclient.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import simplehttpclient.HttpClient;
import simplehttpclient.HttpRequest;
import simplehttpclient.HttpResponse;
import simplehttpclient.HttpResponse.BodyHandler;

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
      int status = /** @j2sNative jqXHR.status || */0;
      if (status > 0) {
        try {
          HttpResponse<T> response = responseFromJqXHR(request, handler, jqXHR);
          responseFuture.complete(response);
        }
        catch (Exception e) {
          responseFuture.completeExceptionally(e);
        }
      }
      else {
        responseFuture.completeExceptionally(new IOException(errorThrown));
      }
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
    return new SimpleHttpResponse<>(
        statusCode, request, responseHeaders.build(), handler.apply(stream),
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
    /** @j2sNative
     * return jQuery.ajax(url, {
     *  async: async,
     *  contentType: false,
     *  data: data,
     *  dataType: "text",
     *  headers: headers,
     *  method: method,
     *  processData: false
     * })
     */
    {
      return null;
    }
  }

}
