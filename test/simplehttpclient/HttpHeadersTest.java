package simplehttpclient;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

public class HttpHeadersTest {

  Map<String, List<String>> headersMap;
  HttpHeaders headers;

  @BeforeClass
  public void setupHeaders() {
    headersMap = Map.of(
        "Content-Length", List.of("10"),
        "Authorization", List.of("DE4D83EF"),
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
  public void map_MultiValue_AllEqualOriginal() {
    assertEquals(headers.map().get("Accept"), List.of("application/json", "text/xml"));
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


  @DataProvider(name = "ValueTrimming")
  public Object[][] createValueTrimmingHeaders() {
    var values = List.of("gzip", " gzip", "gzip ", "  gzip",
        "gzip  ", " gzip ", "\tgzip", "gzip\t", "\tgzip\t", " \tgzip",
        "gzip \t", "gzip\t ", "\rgzip", "gzip\r", "\ngzip", "gzip\n",
        "gzip\r\n");
    var args = new Object[values.size()][];
    int i = 0;
    for (var value : values) {
      args[i++] = new Object[]{Map.of("Accept-Encoding", List.of(value))};
    }
    return args;
  }

  @Test(dataProvider = "ValueTrimming")
  public void firstValue_ValueContainsSpaces_ValueTrimmed(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertTrue(headers.firstValue("Accept-Encoding").isPresent());
    assertEquals(headers.firstValue("Accept-Encoding").get(), "gzip");
  }

  @Test(dataProvider = "ValueTrimming")
  public void allValues_ValueContainsSpaces_ValueTrimmed(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertEquals(headers.allValues("Accept-Encoding"), List.of("gzip"));
  }

  @Test(dataProvider = "ValueTrimming")
  public void map_ValueContainsSpaces_ValueTrimmed(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertEquals(headers.map().get("Accept-Encoding"), List.of("gzip"));
  }

  @DataProvider(name = "KeyTrimming")
  public Object[][] createKeyTrimmingHeaders() {
    var keys = List.of("Accept", " Accept", "Accept ",
        "  Accept", "Accept  ", "\tAccept", "Accept\t", "\rAccept", "Accept\r",
        "Accept\n", "Accept\n", "Accept\r\n", "Accept \t");
    var args = new Object[keys.size()][];
    int i = 0;
    for (var key : keys) {
      args[i++] = new Object[]{Map.of(key, List.of("text/plain"))};
    }
    return args;
  }

  @Test(dataProvider = "KeyTrimming")
  public void firstValue_KeyContainsSpace_KeyTrimmed(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertTrue(headers.firstValue("Accept").isPresent());
    assertEquals(headers.firstValue("Accept").get(), "text/plain");
  }

  @Test(dataProvider = "KeyTrimming")
  public void allValues_KeyContainsSpace_KeyTrimmed(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertEquals(headers.allValues("Accept"), List.of("text/plain"));
  }

  @Test(dataProvider = "KeyTrimming")
  public void map_KeyContainsSpace_KeyTrimmed(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertTrue(headers.map().containsKey("Accept"));
    assertEquals(headers.map().get("Accept"), List.of("text/plain"));
  }

  @DataProvider(name = "CaseInsensitiveHeaders")
  public Object[][] createCaseInsensitiveHeaders() {
    return new Object[][]{
        {Map.of("Content-Length", List.of("25"))},
        {Map.of("content-length", List.of("25"))},
        {Map.of("CONTENT-LENGTH", List.of("25"))},
        {Map.of("ContEnt-LenGtH", List.of("25"))},
        {Map.of("conTent-lENGTH", List.of("25"))}};
  }

  @Test(dataProvider = "CaseInsensitiveHeaders")
  public void firstValue_HeaderNameIgnoresCase(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    for (String key : List.of("Content-Length", "content-length",
        "CONTENT-LENGTH", "CoNtENt-LengtH")) {
      assertTrue(headers.firstValue(key).isPresent());
      assertEquals(headers.firstValue(key).get(), "25");
    }
  }

  @Test(dataProvider = "CaseInsensitiveHeaders")
  public void allValues_HeaderNameIgnoresCase(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    for (String key : List.of("Content-Length", "content-length",
        "CONTENT-LENGTH", "CoNtENt-LengtH")) {
      assertEquals(headers.allValues(key), List.of("25"));
    }
  }

  @Test(dataProvider = "CaseInsensitiveHeaders")
  public void map_MapKeysIgnoreCase(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    for (String key : List.of("Content-Length", "content-length",
        "CONTENT-LENGTH", "CoNtENt-LengtH")) {
      assertTrue(headers.map().containsKey(key));
      assertEquals(headers.map().get(key), List.of("25"));
    }
  }

  @DataProvider(name = "EmptyListValue")
  public Object[][] createNoValueHeaders() {
    return new Object[][]{
        {Map.of("Accept", List.of())},
        {Map.of("Accept", Collections.emptyList())},
        {Map.of("Accept", List.of(), "Encoding", List.of())}};
  }

  @Test(dataProvider = "EmptyListValue")
  public void firstValue_NoValues_ValueNotPresent(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertFalse(headers.firstValue("Accept").isPresent());
  }

  @Test(dataProvider = "EmptyListValue")
  public void allValues_NoValues_EmptyList(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertEquals(headers.allValues("Accept"), List.of());
  }

  @Test(dataProvider = "EmptyListValue")
  public void map_NoValues_EmptyMap(Map<String, List<String>> map) {
    HttpHeaders headers = HttpHeaders.of(map);
    assertFalse(headers.map().containsKey("Accept"));
    assertEquals(headers.map().size(), 0);
    assertNull(headers.map().get("Accept"));
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

  @DataProvider(name = "EmptyName")
  public Object[][] createEmptyNameHeaders() {
    return new Object[][]{
        {Map.of("", List.of("V"))},
        {Map.of(" ", List.of("V"))},
        {Map.of("  ", List.of("V"))},
        {Map.of("\t", List.of("V"))},
        {Map.of("\t\t", List.of("V"))},
        {Map.of(" \t", List.of("V"))},
        {Map.of("\t ", List.of("V"))}};
  }

  @Test(dataProvider = "EmptyName", expectedExceptions = IllegalArgumentException.class)
  public void of_EmptyKey_ThrowIAE(Map<String, List<String>> map) {
    HttpHeaders.of(map);
  }

  @DataProvider(name = "DuplicateNames")
  public Object[][] createDuplicateNamesHeaders() {
    return new Object[][] {
        {Map.of("Connection", List.of("close"),
            "connection", List.of("keep-alive"))},
        {Map.of("Connection", List.of(),
            "connection", List.of())},
        {Map.of("Connection", List.of("close"),
            "connection", List.of())},
        {Map.of("Connection", List.of(),
            "connection", List.of("keep-alive"))},
        {Map.of("Connection", List.of("close"),
            "Accept-Encoding", List.of("deflate"),
            "accept-encoding", List.of("deflate"))},
        {Map.of("Accept-Encoding", List.of("deflate"),
            "Accept-Encoding ", List.of("gzip"))},
        {Map.of("\nAccept-Encoding", List.of("deflate"),
            "Accept-Encoding\t", List.of("gzip"))}
    };
  }

  @Test(dataProvider = "DuplicateNames", expectedExceptions = IllegalArgumentException.class)
  public void of_DuplicateKey_Throw(Map<String, List<String>> map) {
    HttpHeaders.of(map);
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
  public void equals_CaseInsensitiveNames_AllEqual() {
    var maps = List.of(
        Map.of("Content-Length", List.of("25")),
        Map.of("content-length", List.of("25")),
        Map.of("CONTENT-LENGTH", List.of("25")),
        Map.of("ContEnt-LenGtH", List.of("25")),
        Map.of("conTent-lENGTH", List.of("25")));
    for (var map1 : maps) {
      HttpHeaders headers1 = HttpHeaders.of(map1);
      for (var map2 : maps) {
        HttpHeaders headers2 = HttpHeaders.of(map2);
        assertEquals(headers1, headers2);
      }
    }
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