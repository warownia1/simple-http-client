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
