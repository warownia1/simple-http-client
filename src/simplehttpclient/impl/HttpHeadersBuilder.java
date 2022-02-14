package simplehttpclient.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import simplehttpclient.HttpHeaders;

public class HttpHeadersBuilder
{
  private final TreeMap<String, List<String>> headers =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);


  public void addHeader(String name, String value) {
    List<String> values = headers.get(name);
    if (values == null) {
      values = new ArrayList<>(1);
      headers.put(name, values);
    }
    values.add(value);
  }

  public void setHeader(String name, String value) {
    List<String> values = new ArrayList<>(1);
    values.add(value);
    headers.put(name, values);
  }

  public HttpHeaders build() {
    return HttpHeaders.of(headers);
  }
}
