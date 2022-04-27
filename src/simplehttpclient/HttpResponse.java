package simplehttpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface HttpResponse<T> {

  @FunctionalInterface
  interface BodyHandler<T> {
    T apply(InputStream stream) throws IOException;
  }

  class BodyHandlers {
    private BodyHandlers() {
    }

    public static BodyHandler<String> ofString() {
      return stream -> new String(stream.readAllBytes());
    }
  }

  int statusCode();

  HttpRequest request();

  HttpHeaders headers();

  T body();

  URI uri();
}
