package simplehttpclient.impl;

import java.net.URI;
import java.util.Objects;

import simplehttpclient.HttpRequest;

public class SimpleHttpRequestBuilder implements HttpRequest.Builder
{
  private URI uri;
  private String method = "GET";
  private HttpHeadersBuilder headers = new HttpHeadersBuilder();
  private HttpRequest.Body body = EmptyRequestBody.getInstance();

  public SimpleHttpRequestBuilder uri(URI uri) {
    this.uri = uri;
    return this;
  }

  public SimpleHttpRequestBuilder header(String name, String value) {
    headers.addHeader(name, value);
    return this;
  }

  public SimpleHttpRequestBuilder HEAD() {
    return method("HEAD", EmptyRequestBody.getInstance());
  }

  public SimpleHttpRequestBuilder GET() {
    return method("GET", EmptyRequestBody.getInstance());
  }

  public SimpleHttpRequestBuilder POST(HttpRequest.Body body) {
    Objects.requireNonNull(body);
    return method("POST", body);
  }

  public SimpleHttpRequestBuilder PUT(HttpRequest.Body body) {
    Objects.requireNonNull(body);
    return method("PUT", body);
  }

  public SimpleHttpRequestBuilder DELETE() {
    return method("DELETE", EmptyRequestBody.getInstance());
  }

  public SimpleHttpRequestBuilder method(String method, HttpRequest.Body body) {
    Objects.requireNonNull(method);
    Objects.requireNonNull(body);
    if (method.equals(""))
      throw new IllegalArgumentException("illegal method <empty string>");
    this.method = method;
    this.body = body;
    return this;
  }

  @Override
  public HttpRequest build()
  {
    if (uri == null)
      throw new IllegalStateException("uri is null");
    return new ImmutableHttpRequest(uri, method, headers.build(), body);
  }
}
