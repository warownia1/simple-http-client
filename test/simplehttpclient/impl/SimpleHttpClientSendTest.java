package simplehttpclient.impl;

import org.testng.annotations.*;
import simplehttpclient.HttpClient;
import simplehttpclient.HttpHeaders;
import simplehttpclient.HttpResponse;

import java.io.*;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.testng.Assert.assertEquals;


class ParsedRequest {
  String method;
  String path;
  String httpVersion;
  Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  String body;

  private ParsedRequest() {}

  public static ParsedRequest fromStream(InputStream ins) throws IOException {
    final var charset = StandardCharsets.UTF_8;
    BufferedReader in = new BufferedReader(new InputStreamReader(ins, charset));
    var request = new ParsedRequest();
    String line = in.readLine();
    String[] reqLine = line.split(" ");
    if (reqLine.length != 3)
      throw new IOException("invalid status line");
    request.method = reqLine[0];
    request.path = reqLine[1];
    request.httpVersion = reqLine[2];
    while (!(line = in.readLine()).isEmpty()) {
      String[] header = line.split(":", 2);
      if (header.length != 2)
        throw new IOException("invalid header line " + line);
      String key = header[0].trim(), val = header[1].trim();
      request.headers.computeIfAbsent(key, _k -> new ArrayList<>()).add(val);
    }
    int remainingBytes = Integer.parseInt(
      request.headers.getOrDefault("Content-Length", List.of("0")).get(0)
    );
    char[] cbuf = new char[4096];
    StringBuilder body = new StringBuilder();
    while (remainingBytes > 0) {
      int read = in.read(cbuf, 0, Math.min(cbuf.length, remainingBytes));
      if (read == -1)
        throw new IOException("not enough bytes to read");
      var s = new String(cbuf, 0, read);
      remainingBytes -= s.getBytes(charset).length;
      body.append(s);
    }
    request.body = body.toString();
    return request;
  }
}


public class SimpleHttpClientSendTest {
  ServerSocket serverSock;
  ExecutorService executor;
  HttpClient client;

  class FakeRequestHandler implements Callable<ParsedRequest> {
    String response = "HTTP/1.1 204 No Content\r\n\r\n";
    static final String badRequestResponse = "HTTP/1.1 400 Bad Request\r\n\r\n";

    @Override
    public ParsedRequest call() throws Exception{
      Socket sock = serverSock.accept();
      sock.setSoTimeout(100);
      PrintWriter out = new PrintWriter(sock.getOutputStream(), true, StandardCharsets.UTF_8);
      try (out) {
        ParsedRequest request;
        try {
          request = ParsedRequest.fromStream(sock.getInputStream());
        }
        catch (IOException e) {
          out.print(badRequestResponse);
          throw e;
        }
        out.print(response);
        return request;
      }
    }
  }

  @BeforeMethod
  public void setupServerSock() throws IOException {
    serverSock = new ServerSocket(0);
    serverSock.setSoTimeout(100);
  }

  @AfterMethod
  public void teardownServerSock() throws IOException {
    serverSock.close();
  }

  @BeforeMethod
  public void setupClient() {
    client = HttpClient.newHttpClient();
  }

  @BeforeClass
  public void setupExecutor() {
    executor = Executors.newCachedThreadPool();
  }

  @AfterClass
  public void teardownExecutor() {
    executor.shutdownNow();
  }

  @Test
  public void send_TestHttpVersion()
      throws ExecutionException, InterruptedException, IOException {
    var reqFuture = executor.submit(new FakeRequestHandler());
    var request = new HttpRequestStub("http://127.0.0.1:" + serverSock.getLocalPort());
    client.send(request, HttpResponse.BodyHandlers.discarding());
    var receivedRequest = reqFuture.get();
    assertEquals(receivedRequest.httpVersion, "HTTP/1.1");
  }

  @DataProvider(name = "URLPath")
  public Object[][] createURLPath() {
    return new Object[][] {
        {""},
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

  @Test(dataProvider = "URLPath")
  public void send_SimplePath_RequestPathMatches(String path)
      throws IOException, InterruptedException, ExecutionException {
    var handler = new FakeRequestHandler();
    var reqFuture = executor.submit(handler);
    var request = new HttpRequestStub("http://127.0.0.1:" + serverSock.getLocalPort() + path);
    client.send(request, HttpResponse.BodyHandlers.discarding());
    var receivedRequest = reqFuture.get();
    var expected = path.length() > 0 ? path : "/";
    assertEquals(receivedRequest.path, expected);
  }

  @DataProvider(name = "URLPathWithFragment")
  public Object[][] createURLPathWithFragment() {
    return new Object[][] {
        {"/#id"},
        {"/index.html#id"},
        {"/?param=value#fragment"},
    };
  }

  @Test(dataProvider = "URLPathWithFragment")
  public void send_PathWithFragment_FragmentNotPresent(String path)
      throws IOException, InterruptedException, ExecutionException {
    var handler = new FakeRequestHandler();
    var reqFuture = executor.submit(handler);
    var request = new HttpRequestStub("http://127.0.0.1:" + serverSock.getLocalPort() + path);
    client.send(request, HttpResponse.BodyHandlers.discarding());
    var receivedRequest = reqFuture.get();
    var expected = path.substring(0, path.indexOf('#'));
    assertEquals(receivedRequest.path, expected);
  }

  @DataProvider(name = "ValidHttpMethod")
  public Object[][] createMethod() {
    return new Object[][] {
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

  @Test(dataProvider = "ValidHttpMethod")
  public void send_TestHttpMethods(String method)
      throws IOException, InterruptedException, ExecutionException {
    var handler = new FakeRequestHandler();
    var reqFuture = executor.submit(handler);
    var request = new HttpRequestStub("http://127.0.0.1:" + serverSock.getLocalPort());
    request.method = method;
    client.send(request,  HttpResponse.BodyHandlers.discarding());
    var receivedRequest = reqFuture.get();
    assertEquals(receivedRequest.method, method);
  }

  @DataProvider(name = "InvalidHttpMethod")
  public Object[][] createInvalidMethod() {
    return new Object[][] {
        {"CUSTOM"},
        {"get"},
        {"THE METHOD"}
    };
  }

  @Test(dataProvider = "InvalidHttpMethod", expectedExceptions = ProtocolException.class)
  public void send_InvalidHttpMethod_ThrowProtocolException(String method)
      throws IOException {
    var request = new HttpRequestStub("http://127.0.0.1:" + serverSock.getLocalPort());
    request.method = method;
    client.send(request, HttpResponse.BodyHandlers.discarding());
  }

  @DataProvider(name = "RequestContent")
  public Object[][] createRequestContent() {
    return new Object[][]{
        {""},
        {"Content"},
        {"Request Content"},
        {"param1=val&param2=val"},
        {"Multi\r\nline\r\ncontent"},
        {"úñìÇóÐË"}
    };
  }

  @Test(dataProvider = "RequestContent")
  public void send_TestBodySent(String body)
      throws IOException, ExecutionException, InterruptedException {
    var handler = new FakeRequestHandler();
    var reqFuture = executor.submit(handler);
    var request = new HttpRequestStub("http://127.0.0.1:" + serverSock.getLocalPort());
    request.body = new ByteArrayRequestBody(body.getBytes(StandardCharsets.UTF_8));
    client.send(request, HttpResponse.BodyHandlers.discarding());
    var receivedRequest = reqFuture.get();
    assertEquals(receivedRequest.body, body);
  }

  @Test(dataProvider = "RequestContent")
  public void send_SetBodyContent_ContentLengthPresent(String body)
      throws IOException, ExecutionException, InterruptedException {
    var reqFuture = executor.submit(new FakeRequestHandler());
    var request = new HttpRequestStub("http://127.0.0.1:" + serverSock.getLocalPort());
    request.body = new ByteArrayRequestBody(body.getBytes(StandardCharsets.UTF_8));
    client.send(request, HttpResponse.BodyHandlers.discarding());
    var receivedRequest = reqFuture.get();
    int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
    assertEquals(
        receivedRequest.headers.get("Content-Length"),
        List.of(Integer.toString(contentLength))
    );
  }

  @DataProvider(name = "Headers")
  public Object[][] createHeaderValues() {
    return new Object[][] {
        {Map.of("A", List.of("B"))},
        {Map.of("A", List.of("B", "C"))},
        {Map.of("A", List.of("B", "C", "D"))},
        {Map.of("A", List.of("B"), "X", List.of("Y"))},
        {Map.of("A", List.of("B"), "X", List.of("Y", "Z"))},
        {Map.of("A", List.of("B", "C"), "X", List.of("Y", "Z", "T"))}
    };
  }

  @Test(dataProvider = "Headers")
  public void send_SetHeaders_HeadersSent(Map<String, List<String>> map)
      throws IOException, ExecutionException, InterruptedException {
    var reqFuture = executor.submit(new FakeRequestHandler());
    var request = new HttpRequestStub("Http://127.0.0.1:" + serverSock.getLocalPort());
    request.headers = HttpHeaders.of(map);
    client.send(request, HttpResponse.BodyHandlers.discarding());
    var receivedRequest = reqFuture.get();
    System.out.println(receivedRequest.headers);
    for (var entry : map.entrySet()) {
      assertEquals(receivedRequest.headers.get(entry.getKey()), entry.getValue());
    }
  }
}
