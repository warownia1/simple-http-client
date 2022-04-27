package simplehttpclient.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import simplehttpclient.HttpClient;
import simplehttpclient.HttpHeaders;
import simplehttpclient.HttpRequest;
import simplehttpclient.HttpResponse;
import simplehttpclient.HttpResponse.BodyHandler;

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
    if (request.body().isPresent()) {
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Length",
          Long.toString(request.body().get().contentLength()));
    }
    else {
      conn.setRequestProperty("Content-Length", "0");
    }
    conn.connect();
    if (request.body().isPresent()) {
      HttpRequest.Body body = request.body().get();
      OutputStream os = conn.getOutputStream();
      os.write(body.getBytes());
    }
    int statusCode = conn.getResponseCode();
    HttpHeaders headers = HttpHeaders.of(conn.getHeaderFields());
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
    // TODO Auto-generated method stub
    return null;
  }

}
