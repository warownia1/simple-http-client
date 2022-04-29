package simplehttpclient.impl;

import java.net.URI;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.lang.String.format;

import simplehttpclient.HttpRequest;

public class SimpleHttpRequestBuilder implements HttpRequest.Builder {

  URI uri;
  String method = "GET";
  HttpHeadersBuilder headers = new HttpHeadersBuilder();
  HttpRequest.Body body = EmptyRequestBody.getInstance();
  Duration timeout;

  @Override
  public SimpleHttpRequestBuilder uri(URI uri) {
    requireNonNull(uri);
    checkURI(uri);
    this.uri = uri;
    return this;
  }

  static void checkURI(URI uri) {
    String scheme = uri.getScheme();
    if (scheme == null)
      throw new IllegalArgumentException("URI with undefined scheme");
    scheme = scheme.toLowerCase(Locale.US);
    if (!scheme.equals("https") && !scheme.equals("http"))
      throw new IllegalArgumentException(format("invalid URI scheme %s", scheme));
    if (uri.getHost() == null)
      throw new IllegalArgumentException(format("unsupported URI %s", uri));
  }

  @Override
  public SimpleHttpRequestBuilder header(String name, String value) {
    requireNonNull(name);
    requireNonNull(value);
    headers.addHeader(name, value);
    return this;
  }

  @Override
  public SimpleHttpRequestBuilder HEAD() {
    return method("HEAD", EmptyRequestBody.getInstance());
  }

  @Override
  public SimpleHttpRequestBuilder GET() {
    return method("GET", EmptyRequestBody.getInstance());
  }

  @Override
  public SimpleHttpRequestBuilder POST(HttpRequest.Body body) {
    Objects.requireNonNull(body);
    return method("POST", body);
  }

  @Override
  public SimpleHttpRequestBuilder PUT(HttpRequest.Body body) {
    Objects.requireNonNull(body);
    return method("PUT", body);
  }

  @Override
  public SimpleHttpRequestBuilder DELETE() {
    return method("DELETE", EmptyRequestBody.getInstance());
  }

  @Override
  public SimpleHttpRequestBuilder method(String method, HttpRequest.Body body) {
    Objects.requireNonNull(method);
    if (method.equals(""))
      throw new IllegalArgumentException("illegal method <empty string>");
    this.method = method;
    this.body = requireNonNull(body);
    return this;
  }

  @Override
  public SimpleHttpRequestBuilder timeout(Duration duration) {
    if (duration == null) {
      this.timeout = null;
      return this;
    }
    if (duration.isNegative() || duration.isZero()) {
      throw new IllegalArgumentException("invalid duration: " + duration);
    }
    this.timeout = duration;
    return this;
  }

  @Override
  public ImmutableHttpRequest build() {
    return new ImmutableHttpRequest(this);
  }
}
