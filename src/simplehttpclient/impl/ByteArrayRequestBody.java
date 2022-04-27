package simplehttpclient.impl;

import simplehttpclient.HttpRequest;

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
