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

/**
 * A read only view of HTTP headers.
 * <p>
 * A {@code HttpHeaders} is not typically instantiated directly, but rather returned from an
 * {@link HttpRequest#headers() HttpRequest} or an
 * {@link HttpResponse#headers() HttpResponse}. Specific HTTP headers can be set for a
 * {@link HttpRequest request} through the request builder's
 * {@link HttpRequest.Builder#header(String, String) headers} method.
 * <p>
 * The methods of this class that accept a String header name, and the {@code Map} returned
 * by the {@link #map()} method, operate without regard to case when retrieving the header
 * value(s).
 * <p>
 * An HTTP header name may appear more than one in the HTTP protocol. As such, headers are
 * represented as a name and a list of values. Each occurrence of a header value is added
 * verbatim to the appropriate header name list without interpreting its value. In
 * particular, {@code HttpHeaders} does not perform any splitting or joining of comma
 * separated header value strings. The order of elements in a header value list is preserved
 * then building a request. For responses, the order of elements in a header value list is
 * the order in which they were received. The {@code Map} returned by the {@code map}
 * method, however, does not provide any guarantee with regard to the ordering of its
 * entries.
 * <p>
 * {@code HttpHeaders} instances are immutable.
 */
public final class HttpHeaders {

  private final Map<String, List<String>> headers;

  private HttpHeaders(Map<String, List<String>> headers) {
    this.headers = headers;
  }

  /**
   * Returns an HTTP headers from the given map. The given map's key represents the header
   * name, and its value the list of string header values for that header name.
   *
   * <p> An HTTP header name may appear more than once in the HTTP protocol.
   * Such, <i>multi-valued</i>, headers must be represented by a single entry in the given
   * map, whose entry value is a list that represents the multiple header string values.
   * Leading and trailing whitespaces are removed from all string values retrieved from the
   * given map and its lists before processing. Only headers that contain at least one,
   * possibly empty string, value will be added to the HTTP headers.
   *
   * @param map the map containing the header names and values
   * @return an HTTP headers instance containing the given headers
   * @throws NullPointerException if any of: {@code headerMap}, a key or value in the
   *     given map, or an entry in the map's value list is {@code null}
   * @throws IllegalArgumentException if the given {@code headerMap} contains any two
   *     keys that are equal ( without regard to case ); or if the given map contains any
   *     key whose length, after trimming whitespaces, is {@code 0}
   * @apiNote The primary purpose of this method is for testing frameworks. Per-request
   *     headers can be set through one of the {@code HttpRequest}
   *     {@link HttpRequest.Builder#header(String, String) headers} methods.
   */
  public static HttpHeaders of(Map<String, List<String>> map) {
    requireNonNull(map);
    TreeMap<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    TreeSet<String> notAdded = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
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
      if (headers.containsKey(headerName) || notAdded.contains(headerName)) {
        throw new IllegalArgumentException("duplicate header: " + headerName);
      }
      if (!headerValues.isEmpty()) {
        headers.put(headerName, unmodifiableList(headerValues));
      }
      else {
        notAdded.add(headerName);
      }
    });
    return new HttpHeaders(unmodifiableMap(headers));
  }

  /**
   * Returns an unmodifiable multi Map view of this HttpHeaders.
   *
   * @return the Map
   */
  public Map<String, List<String>> map() {
    return headers;
  }

  /**
   * Returns an unmodifiable List of all the header string values for the given named
   * header. Always returns a List, which may be empty if the header is not present.
   *
   * @param name the header name
   * @return a List of headers string values
   */
  public List<String> allValues(String name) {
    requireNonNull(name);
    List<String> values = headers.get(name);
    return values != null ? values : Collections.emptyList();
  }

  /**
   * Returns an {@link Optional} containing the first header string value of the given named
   * (and possibly multi-valued) header. If the header is not present, then the returned
   * {@code Optional} is empty.
   *
   * @param name the header name
   * @return an {@code Optional<String>} containing the first named header string value, if
   *     present
   */
  public Optional<String> firstValue(String name) {
    List<String> values = allValues(name);
    if (values.isEmpty()) return Optional.empty();
    else return Optional.ofNullable(values.get(0));
  }

  /**
   * Tests this HTTP headers instance for equality with the given object.
   *
   * <p> If the given object is not an {@code HttpHeaders} then this
   * method returns {@code false}. Two HTTP headers are equal if each of their corresponding
   * {@linkplain #map() maps} are equal.
   *
   * <p> This method satisfies the general contract of the {@link
   * Object#equals(Object) Object.equals} method.
   *
   * @param obj the object to which this object is to be compared
   * @return {@code true} if, and only if, the given object is an {@code HttpHeaders} that
   *     is equal to this HTTP headers
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof HttpHeaders))
      return false;
    return this.map().equals(((HttpHeaders) obj).map());
  }

  /**
   * Computes a hash code for this HTTP headers instance.
   *
   * <p> The hash code is based upon the components of the HTTP headers
   * {@link #map() map}, and satisfies the general contract of the
   * {@link Object#hashCode Object.hashCode} method.
   *
   * @return the hash-code value for this HTTP headers
   */
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

  /**
   * Returns this HTTP headers as a string.
   *
   * @return a string describing the HTTP headers
   */
  @Override
  public String toString() {
    return super.toString() + " { " + map() + " }";
  }
}
