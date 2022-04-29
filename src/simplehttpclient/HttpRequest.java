package simplehttpclient;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import simplehttpclient.impl.SimpleHttpRequestBuilder;

public abstract class HttpRequest {

  public interface Builder {
    Builder uri(URI uri);

    Builder header(String name, String value);

    Builder HEAD();

    Builder GET();

    Builder POST(Body body);

    Builder PUT(Body body);

    Builder DELETE();

    Builder method(String method, Body body);

    Builder timeout(Duration duration);

    HttpRequest build();
  }

  public interface Body {
    byte[] getBytes();

    long contentLength();
  }

  public static Builder newBuilder(URI uri) {
    return newBuilder().uri(uri);
  }

  public static Builder newBuilder() {
    return new SimpleHttpRequestBuilder();
  }

  public abstract Optional<Body> body();

  public abstract String method();

  public abstract URI uri();

  public abstract HttpHeaders headers();

  public abstract Optional<Duration> timeout();
}
