package simplehttpclient.impl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

public class HttpHeadersBuilderTest {

  HttpHeadersBuilder builder;

  @BeforeMethod
  public void setupBuilder() {
    builder = new HttpHeadersBuilder();
  }

  @Test
  public void addHeader_NewKey_CreateKeyValue() {
    builder.addHeader("Accept", "text/css");
    var headers = builder.build();
    assertEquals(headers.allValues("Accept"), List.of("text/css"));
  }

  @Test
  public void addHeader_ExistingKey_AppendValue() {
    builder.addHeader("Accept", "text/css");
    builder.addHeader("Accept", "text/plain");
    var headers = builder.build();
    assertEquals(headers.allValues("Accept"), List.of("text/css", "text/plain"));
  }

  @Test
  public void addHeader_DifferentKey_CreateKeyValue() {
    builder.addHeader("Accept", "text/css");
    builder.addHeader("Content-Length", "5");
    var headers = builder.build();
    assertEquals(headers.allValues("Accept"), List.of("text/css"));
    assertEquals(headers.allValues("Content-Length"), List.of("5"));
  }

  @Test
  public void addHeader_ExistingKeyCaseInsensitive_AppendValue() {
    builder.addHeader("Accept", "text/css");
    builder.addHeader("accept", "text/html");
    builder.addHeader("ACCEPT", "text/plain");
    var headers = builder.build();
    assertEquals(headers.allValues("Accept"), List.of("text/css", "text/html", "text/plain"));
  }

  @Test
  public void setHeader_NewKey_CreateKeyValue() {
    builder.setHeader("Accept", "text/css");
    var headers = builder.build();
    assertEquals(headers.allValues("Accept"), List.of("text/css"));
  }

  @Test
  public void setHeader_ExistingKey_ReplaceValue() {
    builder.addHeader("Accept", "text/css");
    builder.setHeader("Accept", "text/plain");
    var headers = builder.build();
    assertEquals(headers.allValues("Accept"), List.of("text/plain"));
  }

  @Test
  public void setHeader_ExistingKeyCaseInsensitive_ReplaceValue() {
    builder.addHeader("Accept", "text/css");
    builder.setHeader("accept", "text/plain");
    var headers = builder.build();
    assertEquals(headers.allValues("Accept"), List.of("text/plain"));
  }

  @Test
  public void clear_ClearHeaders_NoKeys() {
    builder.addHeader("Accept", "text/plain");
    builder.clear();
    var headers = builder.build();
    assertEquals(headers.map().keySet(), Set.of());
  }
}