package simplehttpclient;

import java.net.URI;

import simplehttpclient.impl.ByteArrayRequestBody;
import simplehttpclient.impl.JQueryHttpClient;
import simplehttpclient.impl.SimpleHttpRequestBuilder;

public class Main
{
  public static void main(String[] args) throws Exception {
    var content = "Hello world";
    var body = new ByteArrayRequestBody(content.getBytes());
    var request = new SimpleHttpRequestBuilder()
//        .uri(URI.create("https://ptsv2.com/t/kaliog/post"))
        .uri(URI.create("http://localhost:8080"))
//        .header("Content-Type", "text/plain")
        .header("Foo", "foo")
        .header("Foo", "bar")
        .header("Baz", "baz")
        .GET()
        .build();
    var client = new JQueryHttpClient();
    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
  }
}
