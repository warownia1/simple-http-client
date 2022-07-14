package io.github.warownia1.simplehttpclient.impl;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.github.warownia1.simplehttpclient.HttpHeaders;
import io.github.warownia1.simplehttpclient.HttpRequest;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class SimpleHttpRequestBuilderTest {

  private static final URI EXAMPLE_URI = URI.create("http://example.org");

  @DataProvider(name = "EmptyBuilder")
  public Object[][] createBuilder() {
    return new Object[][]{{new SimpleHttpRequestBuilder()}};
  }

  @Test(expectedExceptions = {IllegalStateException.class},
      dataProvider = "EmptyBuilder")
  public void uri_NotProvided_Throw(HttpRequest.Builder builder) {
    builder.build();
  }

  @DataProvider(name = "ValidURI")
  public Object[][] createValidURI() {
    return new Object[][]{
        {URI.create("http://example.org")},
        {URI.create("https://example.org")},
        {URI.create("http://example.org/")},
        {URI.create("http://www.example.org")},
        {URI.create("http://example.org/path/1234")},
        {URI.create("http://example.org/index.html")},
        {URI.create("http://example.org?foo=hello%20world")},
        {URI.create("http://example.org?val1=A&val2=B")},
        {URI.create("http://example.org#fragment")},
        {URI.create("http://example.org:8080")},
        {URI.create("http://example.org:8080/path?val=A#fragm")},
        {URI.create("http://user@exmaple.org")},
        {URI.create("http://user:p4s5w0rd@example.org")},
        {URI.create("http://10.0.0.1")},
        {URI.create("http://10.0.0.2:8000")},
        {URI.create("http://[1080:0:0:0:8:800:200C:417A]")},
        {URI.create("http://[1080:0:0:0:8:800:200C:417A]:8000")}
    };
  }

  @Test(dataProvider = "ValidURI")
  public void uri_ValidURI(URI uri) {
    var request = new SimpleHttpRequestBuilder().uri(uri).build();
    assertEquals(request.uri(), uri);
  }

  @DataProvider(name = "InvalidURI")
  public Object[][] createInvalidURI() {
    return new Object[][] {
        {URI.create("example.org")},
        {URI.create("//example.org")},
        {URI.create("ftp://exmaple.org")},
        {URI.create("mailto://user@example.org")},
        {URI.create("http:///path")}
    };
  }

  @Test(dataProvider = "InvalidURI", expectedExceptions = IllegalArgumentException.class)
  public void uri_InvalidURI_ThrowIAE(URI uri) {
    new SimpleHttpRequestBuilder().uri(uri);
  }

  @DataProvider(name = "BuilderWithURI")
  public Object[][] createBuilderWithURI() {
    var builder = new SimpleHttpRequestBuilder();
    builder.uri(URI.create("http://example.org"));
    return new Object[][]{{builder}};
  }

  @Test(dataProvider = "BuilderWithURI")
  public void header_OneValue_ValuePresent(HttpRequest.Builder builder) {
    builder.header("Accept", "text/xml");
    var request = builder.build();
    assertEquals(request.headers(), HttpHeaders.of(Map.of("Accept", List.of("text/xml"))));
  }

  @Test(dataProvider = "BuilderWithURI")
  public void header_MultipleValues_ValuesAppended(HttpRequest.Builder builder) {
    builder.header("Accept", "text/xml");
    builder.header("Accept", "application/json");
    builder.header("Accept", "application/yaml");
    var expectValues = List.of("text/xml", "application/json", "application/yaml");
    var expectHeaders = HttpHeaders.of(Map.of("Accept", expectValues));
    var request = builder.build();
    assertEquals(request.headers(), expectHeaders);
  }

  @Test(dataProvider = "BuilderWithURI")
  public void header_MultiValueDifferentCase_ValuesAppended(HttpRequest.Builder builder) {
    builder.header("Accept", "text/xml");
    builder.header("accept", "text/css");
    builder.header("ACCEPT", "text/html");
    var expectValues = List.of("text/xml", "text/css", "text/html");
    var expectHeaders = HttpHeaders.of(Map.of("Accept", expectValues));
    var request = builder.build();
    assertEquals(request.headers(), expectHeaders);
  }

  @Test(dataProvider = "BuilderWithURI")
  public void header_TwoHeaders_TwoValuesPresent(HttpRequest.Builder builder) {
    builder.header("Accept", "text/plain");
    builder.header("Content-Length", "15");
    var expectedHeaders = HttpHeaders.of(Map.of(
        "Accept", List.of("text/plain"),
        "Content-Length", List.of("15")
    ));
    var request = builder.build();
    assertEquals(request.headers(), expectedHeaders);
  }

  @Test(dataProvider = "BuilderWithURI")
  public void HEAD_MethodUsed_MethodIsHEAD(HttpRequest.Builder builder) {
    builder.HEAD();
    var request = builder.build();
    assertEquals(request.method(), "HEAD");
  }

  @Test(dataProvider = "BuilderWithURI")
  public void GET_MethodUsed_MethodIsGET(HttpRequest.Builder builder) {
    builder.GET();
    var request = builder.build();
    assertEquals(request.method(), "GET");
  }

  @Test(dataProvider = "BuilderWithURI")
  public void POST_MethodUsed_MethodIsPOST(HttpRequest.Builder builder) {
    builder.POST(EmptyRequestBody.getInstance());
    var request = builder.build();
    assertEquals(request.method(), "POST");
  }

  @Test(dataProvider = "BuilderWithURI")
  public void PUT_MethodUsed_MethodIsPUT(HttpRequest.Builder builder) {
    builder.PUT(EmptyRequestBody.getInstance());
    var request = builder.build();
    assertEquals(request.method(), "PUT");
  }

  @Test(dataProvider = "BuilderWithURI")
  public void DELETE_MethodUsed_MethodIsDELETE(HttpRequest.Builder builder) {
    builder.DELETE();
    var request = builder.build();
    assertEquals(request.method(), "DELETE");
  }

  @Test(dataProvider = "BuilderWithURI")
  public void method_OtherMethodSet_MethodSet(HttpRequest.Builder builder) {
    builder.method("PATCH", EmptyRequestBody.getInstance());
    var request = builder.build();
    assertEquals(request.method(), "PATCH");
  }

  @Test(expectedExceptions = {NullPointerException.class},
      dataProvider = "BuilderWithURI")
  public void POST_BodyIsNull_ThrowNPE(HttpRequest.Builder builder) {
    builder.POST(null);
  }

  @Test(expectedExceptions = {NullPointerException.class},
      dataProvider = "BuilderWithURI")
  public void PUT_BodyIsNull_ThrowNPE(HttpRequest.Builder builder) {
    builder.PUT(null);
  }

  @Test(expectedExceptions = {NullPointerException.class},
      dataProvider = "BuilderWithURI")
  public void method_NullMethod_ThrowNPE(HttpRequest.Builder builder) {
    builder.method(null, EmptyRequestBody.getInstance());
  }

  @Test(expectedExceptions = {NullPointerException.class},
      dataProvider = "BuilderWithURI")
  public void method_NullBody_ThrowNPE(HttpRequest.Builder builder) {
    builder.method("PATCH", null);
  }

  @Test(dataProvider = "BuilderWithURI")
  public void timeout_PositiveTimeout_TimeoutSet(HttpRequest.Builder builder) {
    builder.timeout(Duration.ofSeconds(1));
    var request = builder.build();
    assertTrue(request.timeout().isPresent());
    assertEquals(request.timeout().get().toMillis(), 1000);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class},
      dataProvider = "BuilderWithURI")
  public void timeout_ZeroTimeout_ThrowIAE(HttpRequest.Builder builder) {
    builder.timeout(Duration.ZERO);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class},
      dataProvider = "BuilderWithURI")
  public void timeout_NegativeTimeout_ThrowIAE(HttpRequest.Builder builder) {
    builder.timeout(Duration.ofMillis(-10));
  }

  @Test(dataProvider = "BuilderWithURI")
  public void timeout_NullTimeout_NotPresent(HttpRequest.Builder builder) {
    builder.timeout(null);
    var request = builder.build();
    assertFalse(request.timeout().isPresent());
  }
}