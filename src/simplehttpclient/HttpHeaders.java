package simplehttpclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

public final class HttpHeaders {

  private final Map<String, List<String>> headers;

  private HttpHeaders(Map<String, List<String>> headers) {
    this.headers = headers;
  }

  public static HttpHeaders of(Map<String, List<String>> map) {
    requireNonNull(map);
    TreeMap<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.forEach((key, value) -> {
      String headerName = requireNonNull(key, "header name").trim();
      if (headerName.isEmpty()) {
        throw new IllegalArgumentException("empty header name");
      }
      requireNonNull(value, "header values");
      ArrayList<String> headerValues = new ArrayList<>(1);
      for (String headerValue : value) {
        headerValue = requireNonNull(headerValue, "header value").trim();
        headerValues.add(headerValue);
      }
      if (!headerValues.isEmpty()) {
        if (headers.containsKey(headerName)) {
          throw new IllegalArgumentException("duplicate header: " + headerName);
        }
        headers.put(headerName, unmodifiableList(headerValues));
      }
    });
    return new HttpHeaders(unmodifiableMap(headers));
  }

  public Map<String, List<String>> map() {
    return headers;
  }

  public List<String> allValues(String name) {
    requireNonNull(name);
    List<String> values = headers.get(name);
    return values != null ? values : Collections.emptyList();
  }

  public Optional<String> firstValue(String name) {
    List<String> values = allValues(name);
    if (values.isEmpty()) return Optional.empty();
    else return Optional.ofNullable(values.get(0));
  }

  @Override
  public String toString() {
    return super.toString() + " { " + map() + " }";
  }
}
