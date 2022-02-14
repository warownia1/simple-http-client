package simplehttpclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface HttpResponse<T>
{
  @FunctionalInterface
  public interface BodyHandler<T> {
    public T apply(InputStream stream) throws IOException;
  }

  public static class BodyHandlers {
    private BodyHandlers() {}

    public static BodyHandler<String> ofString() {
      return stream -> new String(stream.readAllBytes());
    }
  }

  public int statusCode();
  public HttpRequest request();
  public HttpHeaders headers();
  public T body();
  public URI uri();
}
