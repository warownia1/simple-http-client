package simplehttpclient;

import java.net.URI;
import java.util.Optional;

import simplehttpclient.impl.SimpleHttpRequestBuilder;

public interface HttpRequest
{
  public interface Builder {
    public Builder uri(URI uri);
    public Builder header(String name, String value);
    public Builder HEAD();
    public Builder GET();
    public Builder POST(Body body);
    public Builder PUT(Body body);
    public Builder DELETE();
    public Builder method(String method, Body body);
    public HttpRequest build();
  }

  public interface Body {
    byte[] getBytes();
    long contentLength();
  }

  public static Builder newBuilder(URI uri) {
    return new SimpleHttpRequestBuilder().uri(uri);
  }

  public static Builder newBuilder() {
    return new SimpleHttpRequestBuilder();
  }

  public Optional<Body> body();

  public String method();

  public URI uri();

  public HttpHeaders headers();
}
