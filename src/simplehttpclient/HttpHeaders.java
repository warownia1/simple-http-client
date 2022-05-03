/*
 * Copyright (c) 2015, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * The code has been adapted by Mateusz Warowny for use with the
 * simple http client - a java2script compatible http client. Please contact
 * mmzwarowny@dundee.ac.uk if you need additional information.
 */

package simplehttpclient;

import java.util.*;

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
  public boolean equals(Object obj) {
    if (!(obj instanceof HttpHeaders))
      return false;
    return this.map().equals(((HttpHeaders) obj).map());
  }

  @Override
  public int hashCode() {
    int h = 0;
    for (Map.Entry<String, List<String>> e : map().entrySet()) {
      h += entryHash(e);
    }
    return h;
  }

  private static int entryHash(Map.Entry<String, List<String>> e) {
    int keyHash = e.getKey().toLowerCase(Locale.ROOT).hashCode();
    int valHash = e.getValue().hashCode();
    return keyHash ^ valHash;
  }

  @Override
  public String toString() {
    return super.toString() + " { " + map() + " }";
  }
}
