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
