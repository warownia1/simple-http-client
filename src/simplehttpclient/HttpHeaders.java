package simplehttpclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

public final class HttpHeaders
{
  private Map<String, List<String>> headers;

  private HttpHeaders(Map<String, List<String>> headers) {
    this.headers = headers;
  }

  public static HttpHeaders of(Map<String, List<String>> map) {
    Objects.requireNonNull(map);
    TreeMap<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.forEach((key, valuesList) -> {
      String headerName = Objects.requireNonNull(key, "Header names must not be null").trim();
      if (headerName.isEmpty()) {
        throw new IllegalArgumentException("Empty header name");
      }
      Objects.requireNonNull(valuesList, "Header values must not be null");
      ArrayList<String> headerValues = new ArrayList<>(1);
      for (String headerValue : valuesList) {
        headerValue = Objects.requireNonNull(headerValue).trim();
        headerValues.add(headerValue);
      }
      if (!headerValues.isEmpty()) {
        if (headers.containsKey(headerName)) {
          throw new IllegalArgumentException("Duplicate header: " + headerName);
        }
        headers.put(headerName, Collections.unmodifiableList(headerValues));
      }
    });
    return new HttpHeaders(Collections.unmodifiableMap(headers));
  }

  public Map<String, List<String>> map() {
    return headers;
  }

  public List<String> allValues(String name) {
    Objects.requireNonNull(name);
    List<String> values = headers.get(name);
    return values != null ? values : Collections.emptyList();
  }

  public Optional<String> firstValue(String name) {
    List<String> values = allValues(name);
    if (values.isEmpty()) return Optional.empty();
    else return Optional.ofNullable(values.get(0));
  }

  public Collection<String> allHeaders() {
    return headers.keySet();
  }
}
