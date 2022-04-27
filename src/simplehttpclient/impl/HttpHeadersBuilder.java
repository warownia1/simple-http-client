package simplehttpclient.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import simplehttpclient.HttpHeaders;

/**
 * A mutable builder for collecting and building HTTP headers.
 */
public class HttpHeadersBuilder {

  private final TreeMap<String, List<String>> headers =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  public void addHeader(String name, String value) {
    headers.computeIfAbsent(name, k -> new ArrayList<>(1))
        .add(value);
  }

  public void setHeader(String name, String value) {
    List<String> values = new ArrayList<>(1);
    values.add(value);
    headers.put(name, values);
  }

  public void clear() {
    headers.clear();
  }

  public Map<String, List<String>> map() {
    return headers;
  }

  public HttpHeaders build() {
    return HttpHeaders.of(headers);
  }

  @Override
  public String toString() {
    return super.toString() + " { " + map() + " }";
  }
}
