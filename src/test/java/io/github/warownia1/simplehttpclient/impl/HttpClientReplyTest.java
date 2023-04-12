package io.github.warownia1.simplehttpclient.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.warownia1.simplehttpclient.HttpClient;
import io.github.warownia1.simplehttpclient.HttpRequest;
import io.github.warownia1.simplehttpclient.HttpResponse;
import org.testng.annotations.*;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.testng.Assert.assertEquals;

public class HttpClientReplyTest {
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

  @DataProvider(name = "1xxStatusCode")
  public Object[][] informationalStatusCode() {
    return new Object[][] {
        {100, "Continue"},
        {101, "Switching Protocols"},
        {102, "Processing"},
        {103, "Early Hints"},
    };
  }

  @DataProvider(name = "2xxStatusCode")
  public Object[][] successfulStatusCode() {
    return new Object[][] {
        {200, "OK"},
        {201, "Created"},
        {202, "Accepted"},
        {203, "Non-Authoritative Information"},
        {204, "No Content"},
        {205, "Reset Content"},
        {206, "Partial Content"},
    };
  }

  @DataProvider(name = "3xxStatusCode")
  public Object[][] redirectionStatusCode() {
    return new Object[][] {
        {300, "Multiple Choices"},
        {301, "Moved Permanently"},
        {302, "Found"},
        {303, "See Other"},
        {304, "Not Modified"},
        {307, "Temporary Redirect"},
        {308, "Permanent Redirect"},
    };
  }

  @DataProvider(name = "4xxStatusCode")
  public Object[][] clientErrorStatusCode() {
    return new Object[][] {
        {400, "Bad Request"},
        {401, "Unauthorised"},
        {403, "Forbidden"},
        {404, "Not Found"},
        {405, "Method Not Allowed"},
        {406, "Not Acceptable"},
        {407, "Proxy Authentication Required"},
        {408, "Request Timeout"},
        {409, "Conflict"},
        {410, "Gone"},
        {411, "Length Required"},
        {415, "Unsupported Media Type"},
        {418, "I'm a teapot"},
        {429, "Too Many Requests"},
    };
  }

  @DataProvider(name = "5xxStatusCode")
  public Object[][] serverErrorStatusCode() {
    return new Object[][] {
        {500, "Internal Server Error"},
        {501, "Not Implemented"},
        {502, "Bad Gateway"},
        {503, "Service Unavailable"},
        {504, "Gateway Timeout"},
    };
  }

  @DataProvider(name = "AnyStatusCode")
  public Iterator<Object[]> createAnyStatusCode() {
    // skip 1xx status codes as they are not meant to be seen by the user.
    var stream = Arrays.stream(successfulStatusCode());
    stream = Stream.concat(stream, Arrays.stream(redirectionStatusCode()));
    stream = Stream.concat(stream, Arrays.stream(clientErrorStatusCode()));
    stream = Stream.concat(stream, Arrays.stream(serverErrorStatusCode()));
    return stream.iterator();
  }

  @Test(dataProvider = "AnyStatusCode")
  public void send_ReceiveResponse_StatusMatches(int code, String message) throws IOException {
    stubFor(get("/").willReturn(aResponse().withStatus(code)));
    var client = HttpClient.newHttpClient();
    var request = HttpRequest.newBuilder(URI.create(server.baseUrl())).build();
    var response = client.send(request, HttpResponse.BodyHandlers.discarding());
    assertEquals(response.statusCode(), code);
  }
}
