package simplehttpclient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import simplehttpclient.impl.JQueryHttpClient;
import simplehttpclient.impl.SimpleHttpClient;

public interface HttpClient {

  static HttpClient newHttpClient() {
    if (/** @j2sNative false && */true)
      return new SimpleHttpClient();
    else
      return new JQueryHttpClient();
  }

  <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler)
      throws IOException;

  <T> CompletableFuture<HttpResponse<T>> sendAsync(
      HttpRequest request, HttpResponse.BodyHandler<T> handler, Executor executor);
}
