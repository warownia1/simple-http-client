package io.github.warownia1.simplehttpclient.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import io.github.warownia1.simplehttpclient.HttpClient;
import io.github.warownia1.simplehttpclient.HttpRequest;
import io.github.warownia1.simplehttpclient.HttpResponse;
import org.testng.annotations.*;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


public class HttpClientSendTest {
  WireMockServer server;

  @BeforeClass
  public void setupServer() {
    server = new WireMockServer(wireMockConfig().dynamicPort());
    server.start();
    WireMock.configureFor(server.port());
  }

  @AfterClass
  public void teardownServer() {
    server.stop();
  }

  @BeforeMethod
  public void resetWireMock() {
    WireMock.reset();
  }

  @DataProvider(name = "SimplePath")
  public Object[][] createSimplePath() {
    return new Object[][]{
        {"/"},
        {"/index"},
        {"/index.html"},
        {"/path/to/resource"},
        {"/page/15"},
        {"/alpha%20bravo"},
        {"/alpha-bravo"},
        {"/?key=val"},
        {"/?param1=A&param2=B"},
        {"/index?key=val"},
    };
  }

  @Test(dataProvider = "SimplePath")
  public void send_SimplePath_RequestPathMatches(String path)
      throws IOException {
    stubFor(get(path).willReturn(ok()));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest
        .newBuilder(URI.create(server.url(path))).build();
    client.send(request, HttpResponse.BodyHandlers.discarding());
    verify(getRequestedFor(urlEqualTo(path)));
  }

  @Test
  public void send_EmptyPath_PathIsRoot() throws IOException {
    stubFor(any(anyUrl()).willReturn(ok()));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest
        .newBuilder(URI.create(server.baseUrl())).build();
    client.send(request, HttpResponse.BodyHandlers.discarding());
    verify(getRequestedFor(urlEqualTo("/")));
  }

  @Test(dataProvider = "SimplePath")
  public void send_PathWithFragment_FragmentNotSent(String path)
      throws IOException {
    stubFor(any(anyUrl()).willReturn(ok()));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest
        .newBuilder(URI.create(server.url(path) + "#fragment")).build();
    client.send(request, HttpResponse.BodyHandlers.discarding());
    verify(getRequestedFor(urlEqualTo(path)));
  }

  @DataProvider(name = "HttpMethod")
  public Object[][] createHttpMethod() {
    return new Object[][]{
        {"GET"},
        {"HEAD"},
        {"POST"},
        {"PUT"},
        {"DELETE"},
        {"OPTIONS"},
        {"TRACE"},
        {"PATCH"}, // unfortunately HttpURLConnection does not allow PATCH
    };
  }

  @Test(dataProvider = "HttpMethod")
  public void send_TestMethodSent(String method) throws IOException {
    stubFor(any(anyUrl()).willReturn(ok()));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest
        .newBuilder(URI.create(server.baseUrl()))
        .method(method, EmptyRequestBody.instance)
        .build();
    client.send(request, HttpResponse.BodyHandlers.discarding());
    verify(RequestPatternBuilder.newRequestPattern(
        RequestMethod.fromString(method), urlEqualTo("/")));
  }

  @DataProvider(name = "InvalidHttpMethod")
  public Object[][] createInvalidHttpMethod() {
    return new Object[][]{
        {"CUSTOM"},
        {"get"},
        {"A METHOD"}
    };
  }

  @Test(dataProvider = "InvalidHttpMethod", expectedExceptions = ProtocolException.class)
  public void send_InvalidHttpMethod_ThrowProtocolException(String method)
      throws IOException {
    stubFor(any(anyUrl()).willReturn(ok()));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest
        .newBuilder(URI.create(server.baseUrl()))
        .method(method, EmptyRequestBody.instance)
        .build();
    client.send(request, HttpResponse.BodyHandlers.discarding());
  }

  @DataProvider(name = "RequestContent")
  public Object[][] createRequestContent() {
    var strings = new String[]{
        "",
        "Content",
        "Request Content",
        "param1=val&param2=val",
        "Multi\r\nline\r\ncontent",
        "úñìÇóÐË",
    };
    var args = new Object[strings.length][];
    for (int i = 0; i < strings.length; i++) {
      args[i] = new Object[]{strings[i].getBytes(StandardCharsets.UTF_8)};
    }
    return args;
  }

  @Test(dataProvider = "RequestContent")
  public void send_SendBody_ContentReceived(byte[] body) throws IOException {
    stubFor(post("/").willReturn(ok()));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest
        .newBuilder(URI.create(server.baseUrl()))
        .method("POST", new ByteArrayRequestBody(body))
        .build();
    client.send(request, HttpResponse.BodyHandlers.discarding());
    verify(postRequestedFor(urlPathEqualTo("/"))
        .withRequestBody(binaryEqualTo(body)));
  }

  @Test(dataProvider = "RequestContent")
  public void send_SendBody_ContentLengthSet(byte[] body) throws IOException {
    stubFor(post("/").willReturn(ok()));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest
        .newBuilder(URI.create(server.baseUrl()))
        .method("POST", new ByteArrayRequestBody(body))
        .build();
    client.send(request, HttpResponse.BodyHandlers.discarding());
    verify(postRequestedFor(urlPathEqualTo("/"))
        .withHeader("Content-Length", equalTo(Integer.toString(body.length))));
  }

  @DataProvider(name = "Headers")
  public Object[][] createHeaderValues() {
    return new Object[][]{
        {Map.of("A", List.of("B"))},
        {Map.of("A", List.of("B", "C"))},
        {Map.of("A", List.of("B", "C", "D"))},
        {Map.of("A", List.of("B"), "X", List.of("Y"))},
        {Map.of("A", List.of("B"), "X", List.of("Y", "Z"))},
        {Map.of("A", List.of("B", "C"), "X", List.of("Y", "Z", "T"))}
    };
  }

  @Test(dataProvider = "Headers")
  public void send_SetHeaders_HeadersPresent(Map<String, List<String>> headersMap)
      throws IOException {
    stubFor(any(anyUrl()).willReturn(ok()));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest
        .newBuilder(URI.create(server.baseUrl()));
    for (var entry : headersMap.entrySet()) {
      entry.getValue().forEach(it -> request.header(entry.getKey(), it));
    }
    client.send(request.build(), HttpResponse.BodyHandlers.discarding());
    var reqPattern = getRequestedFor(urlEqualTo("/"));
    for (var entry : headersMap.entrySet()) {
      String headerName = entry.getKey();
      for (var headerValue : entry.getValue()) {
        reqPattern = reqPattern.withHeader(headerName, equalTo(headerValue));
      }
    }
    verify(reqPattern);
  }
}
