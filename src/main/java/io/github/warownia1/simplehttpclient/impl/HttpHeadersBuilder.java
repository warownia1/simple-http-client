/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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

package io.github.warownia1.simplehttpclient.impl;

import io.github.warownia1.simplehttpclient.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A mutable builder for collecting and building HTTP headers.
 */
public class HttpHeadersBuilder {

  private final TreeMap<String, List<String>> headers =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  public HttpHeadersBuilder structuralCopy() {
    HttpHeadersBuilder builder = new HttpHeadersBuilder();
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      List<String> valuesCopy = new ArrayList<>(entry.getValue());
      builder.headers.put(entry.getKey(), valuesCopy);
    }
    return builder;
  }

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
