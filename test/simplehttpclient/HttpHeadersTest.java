package simplehttpclient;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public class HttpHeadersTest {

  Map<String, List<String>> headersMap;
  HttpHeaders headers;

  @BeforeClass
  public void setupHeaders() {
    headersMap = Map.of(
        "Content-Length", List.of("10"),
        "Authorization", List.of("DE4D83EF    "),
        "Accept", List.of("application/json", "text/xml"),
        "Accept-Encoding", List.of()
    );
    headers = HttpHeaders.of(headersMap);
  }

  @Test
  public void map_AllHeaders_OnlyNonEmptyPresent() {
    assertEquals(headers.map().keySet(),
        Set.of("Content-Length", "Authorization", "Accept"));
  }

  @Test
  public void map_SimpleValue_EqualOriginal() {
    assertEquals(headers.map().get("Content-Length"), List.of("10"));
  }

  @Test
  public void map_ValueWithSpaces_TrimmedString() {
    assertEquals(headers.map().get("Authorization"), List.of("DE4D83EF"));
  }

  @Test
  public void map_MultiValue_AllEqualOriginal() {
    assertEquals(headers.map().get("Accept"), List.of("application/json", "text/xml"));
  }

  @Test
  public void map_EmptyValue_NotPresent() {
    assertFalse(headers.map().containsKey("Accept-Encoding"));
  }

  @Test
  public void firstValue_OneValuePresent_GetValue() {
    var contentLength = headers.firstValue("Content-Length");
    assertTrue(contentLength.isPresent());
    assertEquals(contentLength.get(), "10");
  }

  @Test
  public void allValues_OneValue_ListsEqual() {
    var contentLength = headers.allValues("Content-Length");
    assertEquals(contentLength, List.of("10"));
  }

  @Test
  public void firstValue_MultipleValuesPresent_GetFirstValue() {
    var accept = headers.firstValue("Accept");
    assertTrue(accept.isPresent());
    assertEquals(accept.get(), "application/json");
  }

  @Test
  public void allValues_MultipleValues_ListsEqual() {
    var accept = headers.allValues("Accept");
    assertEquals(accept, List.of("application/json", "text/xml"));
  }

  @Test
  public void firstValue_MissingKey_ValueNotPresent() {
    var connection = headers.firstValue("Connection");
    assertFalse(connection.isPresent());
  }

  @Test
  public void allValues_MissingKey_EmptyList() {
    var connection = headers.allValues("Connection");
    assertEquals(connection, List.of());
  }

  @Test
  public void firstValue_NoValues_ValueNotPresent() {
    var encoding = headers.firstValue("Accept-Encoding");
    assertFalse(encoding.isPresent());
  }

  @Test
  public void allValues_NoValues_EmptyList() {
    var encoding = headers.allValues("Accept-Encoding");
    assertEquals(encoding, List.of());
  }

  @Test
  public void firstValue_KeyIgnoreCase_ValuePresent() {
    var contentLength = headers.firstValue("content-length");
    assertTrue(contentLength.isPresent());
  }

  @Test
  public void allValues_KeyIgnoreCase_ValuePresent() {
    var contentLength = headers.allValues("content-length");
    assertEquals(contentLength, List.of("10"));
  }

  @Test(expectedExceptions = {NullPointerException.class})
  public void of_NullMap_Throw() {
    HttpHeaders.of(null);
  }

  @Test(expectedExceptions = {NullPointerException.class})
  public void of_NullKey_Throw() {
    var map = new HashMap<String, List<String>>();
    map.put(null, List.of("value"));
    HttpHeaders.of(map);
  }

  @Test(expectedExceptions = {NullPointerException.class})
  public void of_NullValue_Throw() {
    var map = new HashMap<String, List<String>>();
    map.put("Content-Length", null);
    HttpHeaders.of(map);
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void of_EmptyKey_Throw() {
    HttpHeaders.of(Map.of("", List.of("value")));
  }

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void of_DuplicateKey_Throw() {
    HttpHeaders.of(Map.of(
        "Accept", List.of("text/plain"), "accept", List.of("text/html")));
  }

  @Test
  public void equals_EmptyHeaders_Equal() {
    var headers1 = HttpHeaders.of(Map.of());
    var headers2 = HttpHeaders.of(Map.of());
    assertEquals(headers1, headers2);
  }

  @Test
  public void equals_NonHeadersType_NotEqual() {
    var headers = HttpHeaders.of(Map.of());
    var map = Map.of();
    assertNotEquals(headers, map);
  }

  @Test
  public void equals_SameKeysAndValues_Equal() {
    var headers1 = HttpHeaders.of(Map.of(
        "Accept", List.of("text/plain", "text/html"),
        "Content-Length", List.of("25")
    ));
    var headers2 = HttpHeaders.of(Map.of(
        "Accept", List.of("text/plain", "text/html"),
        "Content-Length", List.of("25")
    ));
    assertEquals(headers1, headers2);
  }

  @Test
  public void equals_DifferentCaseKeys_Equal() {
    var headers1 = HttpHeaders.of(Map.of(
        "Accept", List.of("text/plain", "text/html"),
        "Content-Length", List.of("25")
    ));
    var headers2 = HttpHeaders.of(Map.of(
        "ACCEPT", List.of("text/plain", "text/html"),
        "content-length", List.of("25")
    ));
    assertEquals(headers1, headers2);
  }

  @Test
  public void equals_SameKeysDifferentValues_NotEqual() {
    var headers1 = HttpHeaders.of(Map.of(
        "Accept", List.of("text/plain", "text/html"),
        "Content-Length", List.of("25")
    ));
    var headers2 = HttpHeaders.of(Map.of(
        "Accept", List.of("text/plain", "text/css"),
        "Content-Length", List.of("25")
    ));
    assertNotEquals(headers1, headers2);
  }
}