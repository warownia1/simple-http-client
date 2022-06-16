package io.github.warownia1.simplehttpclient.impl;

import io.github.warownia1.simplehttpclient.HttpHeaders;
import io.github.warownia1.simplehttpclient.HttpRequest;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

class HttpRequestStub extends HttpRequest {

  String method = "GET";
  Body body = null;
  URI uri;
  HttpHeaders headers = HttpHeaders.of(Collections.emptyMap());
  Duration timeout = null;

  HttpRequestStub(String url) {
    this.uri = URI.create(url);
  }

  @Override
  public Optional<Body> body() {
    return Optional.ofNullable(body);
  }

  @Override
  public String method() {
    return method;
  }

  @Override
  public URI uri() {
    return uri;
  }

  @Override
  public HttpHeaders headers() {
    return headers;
  }

  @Override
  public Optional<Duration> timeout() {
    return Optional.ofNullable(timeout);
  }
}
