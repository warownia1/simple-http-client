package simplehttpclient.impl;

import simplehttpclient.HttpRequest;

public class EmptyRequestBody implements HttpRequest.Body {

  public static final EmptyRequestBody instance = new EmptyRequestBody();
  private static final byte[] body = new byte[0];

  public static HttpRequest.Body getInstance() {
    return instance;
  }

  @Override
  public byte[] getBytes() {
    return body;
  }

  @Override
  public long contentLength() {
    return 0;
  }

}
