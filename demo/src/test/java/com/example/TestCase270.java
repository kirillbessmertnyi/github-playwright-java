package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class TestCase270 extends TestBase{

    @Test
    @Tag("270")
    void shouldSearchWiki() {
      page.navigate("https://www.wikipedia.org/");
      page.locator("input[name=\"search\"]").click();
      page.locator("input[name=\"search\"]").fill("playwright");
      page.locator("input[name=\"search\"]").press("Enter");
      assertEquals("https://en.wikipedia.org/wiki/Playwright", page.url());
    }
}