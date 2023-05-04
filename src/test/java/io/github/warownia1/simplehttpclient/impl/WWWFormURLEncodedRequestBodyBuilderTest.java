package io.github.warownia1.simplehttpclient.impl;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.testng.Assert.assertEquals;

public class WWWFormURLEncodedRequestBodyBuilderTest {

  @DataProvider(name = "SingleParameter")
  public Object[][] singleParameter() {
    return new Object[][] {
        {entry("snack", "chocolate"), "snack=chocolate"},
        {entry("snack", "apricot jelly"), "snack=apricot+jelly"},
        {entry("snack", "apple=orange"), "snack=apple%3Dorange"},
        {entry("1!=2", "true"), "1%21%3D2=true"},
        {entry("pound", "#"), "pound=%23"},
        {entry("percent", "%"), "percent=%25"},
        {entry("blank", ""), "blank="},
        {entry("fake=param&real", "is_here"), "fake%3Dparam%26real=is_here"},
        {entry("is healthy", "no"), "is+healthy=no"},
        {entry("2+2*2", "8"), "2%2B2%2A2=8"},
        {entry("unicode", "üǊιć∅ᵈé"), "unicode=%C3%BC%C7%8A%CE%B9%C4%87%E2%88%85%E1%B5%88%C3%A9"},
        {entry("üǊιć∅ᵈé", "unicode"), "%C3%BC%C7%8A%CE%B9%C4%87%E2%88%85%E1%B5%88%C3%A9=unicode"}
    };
  }

  @Test(dataProvider = "SingleParameter")
  public void build_SingleParameter(Map.Entry<String, String> entry, String expectedBody) {
    var builder = newBuilder();
    builder.append(entry.getKey(), entry.getValue());
    var body = builder.build();
    assertEquals(body.getBytes(), expectedBody.getBytes(UTF_8));
  }

  @DataProvider(name = "ASCIICharacter")
  public Object[] asciiCharacter() {
      var chars = new Object[0x80];
      for (char c = 0x00; c < chars.length; c++) {
        chars[c] = c;
      }
      return chars;
  }

  @Test(dataProvider = "ASCIICharacter")
  public void build_EveryCharacter_SpecialEncoded(char ch) {
    var builder = newBuilder();
    builder.append(String.format("char-%02X", (int)ch), String.valueOf(ch));
    var body = builder.build();
    String expected = String.format("char-%02X=", (int)ch);
    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '.' || ch == '_' || ch == '-') {
      expected += ch;
    }
    else if (ch == ' ') {
      expected += '+';
    }
    else {
      expected += String.format("%%%02X", (int)ch);
    }
    assertEquals(body.getBytes(), expected.getBytes(UTF_8));
  }

  @DataProvider(name = "MultipleParameters")
  public Object[][] multipleParameters() {
    return new Object[][]{
        {
            List.of(entry("inflate", "yes"),
                entry("registration", "completed"),
                entry("orientation", "vertical")),
            "inflate=yes&registration=completed&orientation=vertical"
        },
        {
            List.of(entry("fruit", "apple"),
                entry("fruit", "blueberry"),
                entry("vegetable", "tomato"),
                entry("fruit", "lemon")),
            "fruit=apple&fruit=blueberry&vegetable=tomato&fruit=lemon"
        }
    };
  }

  @Test(dataProvider = "MultipleParameters")
  public void build_MultiParameters(List<Map.Entry<String, String>> entries, String expectedBody) {
    var builder = newBuilder();
    for (var entry : entries) {
      builder.append(entry.getKey(), entry.getValue());
    }
    var body = builder.build();
    assertEquals(body.getBytes(), expectedBody.getBytes(UTF_8));
  }

  @Test
  public void build_NoParameters_EmptyBody() {
    var builder = newBuilder();
    var body = builder.build();
    assertEquals(body.getBytes(), new byte[0]);
  }

  public static WWWFormURLEncodedRequestBodyBuilder newBuilder() {
    return new WWWFormURLEncodedRequestBodyBuilder();
  }

  private static Map.Entry<String, String> entry(String key, String value) {
    return new AbstractMap.SimpleImmutableEntry<>(key, value);
  }
}