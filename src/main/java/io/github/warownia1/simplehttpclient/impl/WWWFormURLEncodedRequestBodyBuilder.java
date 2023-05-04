package io.github.warownia1.simplehttpclient.impl;

import io.github.warownia1.simplehttpclient.HttpRequest;

import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;

public class WWWFormURLEncodedRequestBodyBuilder {

  private ByteBuffer bodyBuffer = ByteBuffer.allocate(256);

  public WWWFormURLEncodedRequestBodyBuilder append(String key, String value) {
    byte[] encodedKey, encodedValue;
    try {
      encodedKey = URLEncoder.encode(key, "UTF-8")
              .replaceAll("\\*", "%2A")
              .getBytes("UTF-8");
      encodedValue = URLEncoder.encode(value, "UTF-8")
              .replaceAll("\\*", "%2A")
              .getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      throw new UncheckedIOException(e);
    }
    // additional two bytes for & and = characters
    if (encodedKey.length + encodedValue.length + 2 >= bodyBuffer.remaining()) {
      growBuffer(encodedKey.length + encodedValue.length + 2);
    }
    bodyBuffer.put((byte) '&')
        .put(encodedKey)
        .put((byte) '=')
        .put(encodedValue);
    return this;
  }

  private void growBuffer(int requiredRemaining) {
    int requiredCapacity = bodyBuffer.position() + requiredRemaining;
    int capacity = bodyBuffer.capacity();
    while (capacity <= requiredCapacity) {
      capacity += capacity / 2;
    }
    bodyBuffer.flip();
    var newBuffer = ByteBuffer.allocate(capacity);
    newBuffer.put(bodyBuffer);
    bodyBuffer = newBuffer;
  }

  public HttpRequest.Body build() {
    var buffer = bodyBuffer.duplicate();
    buffer.flip();
    if (buffer.limit() > 0)
      buffer.get();
    return new ByteArrayRequestBody(buffer.array(),
        buffer.arrayOffset() + buffer.position(), buffer.remaining());
  }
}
