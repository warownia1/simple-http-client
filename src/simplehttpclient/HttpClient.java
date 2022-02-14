package simplehttpclient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import simplehttpclient.impl.JQueryHttpClient;
import simplehttpclient.impl.SimpleHttpClient;

public interface HttpClient
{
  public static HttpClient newHttpClient() {
    if (/** @j2sNative false && */ true)
      return new SimpleHttpClient();
    else
      return new JQueryHttpClient();
  }

  public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler) throws IOException;

  public <T> CompletableFuture<HttpResponse<T>> sendAsync(
      HttpRequest request, HttpResponse.BodyHandler<T> handler, Executor executor);
}
