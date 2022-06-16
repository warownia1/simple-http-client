/*
 * Copyright (c) 2022, Mateusz Warowny.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. This particular file is
 * subject to the "Classpath" exception as provided in the LICENSE file
 * that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Mateusz Warowny, mmzwarowny@dundee.ac.uk if you need
 * additional information.
 */

package io.github.warownia1.simplehttpclient.impl;

import io.github.warownia1.simplehttpclient.HttpRequest;

public class ByteArrayRequestBody implements HttpRequest.Body {

  private final int length;
  private final byte[] content;
  private final int offset;

  public ByteArrayRequestBody(byte[] content) {
    this(content, 0, content.length);
  }

  public ByteArrayRequestBody(byte[] content, int offset, int length) {
    this.content = content;
    this.offset = offset;
    this.length = length;
  }

  @Override
  public byte[] getBytes() {
    byte[] buffer = new byte[length];
    System.arraycopy(content, offset, buffer, 0, length);
    return buffer;
  }

  @Override
  public long contentLength() {
    return length;
  }

}