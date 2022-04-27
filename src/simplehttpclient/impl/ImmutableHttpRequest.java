package simplehttpclient.impl;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import simplehttpclient.HttpHeaders;
import simplehttpclient.HttpRequest;

import static java.util.Objects.requireNonNull;

public final class ImmutableHttpRequest extends HttpRequest {

  private final String method;
  private final URI uri;
  private final HttpHeaders headers;
  private final Body body;
  private final Duration timeout;

  ImmutableHttpRequest(SimpleHttpRequestBuilder builder) {
    this.method = requireNonNull(builder.method);
    this.uri = requireNonNull(builder.uri);
    this.headers = HttpHeaders.of(builder.headers.map());
    this.body = builder.body;
    this.timeout = builder.timeout;
  }

  @Override
  public String method() {
    return method;
  }

  @Override
  public URI uri() {
    return uri;
  }

  @Override
  public HttpHeaders headers() {
    return headers;
  }

  @Override
  public Optional<Body> body() {
    return Optional.ofNullable(body);
  }

  @Override
  public Optional<Duration> timeout() {
    return Optional.ofNullable(timeout);
  }

  @Override
  public String toString() {
    return uri.toString() + " " + method;
  }
}
