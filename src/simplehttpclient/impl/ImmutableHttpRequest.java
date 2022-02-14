package simplehttpclient.impl;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import simplehttpclient.HttpHeaders;
import simplehttpclient.HttpRequest;

public class ImmutableHttpRequest implements HttpRequest
{
  private final URI uri;
  private final String method;
  private final HttpHeaders headers;
  private final Optional<Body> body;

  public ImmutableHttpRequest(
      URI uri,
      String method,
      HttpHeaders headers,
      Body body) {
    this.uri = Objects.requireNonNull(uri);
    this.method = Objects.requireNonNull(method);
    this.headers = Objects.requireNonNull(headers);
    this.body = Optional.ofNullable(body);
  }

  @Override
  public Optional<Body> body()
  {
    return body;
  }

  @Override
  public String method()
  {
    return method;
  }

  @Override
  public URI uri()
  {
    return uri;
  }

  @Override
  public HttpHeaders headers()
  {
    return headers;
  }

}
