package com.codemie.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class ThemeToggleTest extends BaseUiTest {

    @Test
    void themeToggleButton_isPresent_withAccessibleLabel() {
        driver.get(baseUrl);
        WebElement toggle = driver.findElement(By.cssSelector("button.theme-toggle"));
        assertEquals("Toggle theme", toggle.getAttribute("aria-label"),
            "Toggle button must have aria-label='Toggle theme'");
        assertTrue(toggle.isDisplayed(), "Toggle button must be visible");
    }

    @Test
    void themeToggle_changesDataThemeAttribute_onClick() {
        driver.get(baseUrl);
        WebElement html = driver.findElement(By.tagName("html"));
        String initialTheme = html.getAttribute("data-theme");
        assertNotNull(initialTheme, "data-theme attribute should be set on page load");

        driver.findElement(By.cssSelector("button.theme-toggle")).click();

        String afterClickTheme = html.getAttribute("data-theme");
        assertNotEquals(initialTheme, afterClickTheme,
            "data-theme must change after clicking toggle. Was: " + initialTheme);
    }

    @Test
    void themeToggle_persistsSelectionToLocalStorage() {
        driver.get(baseUrl);
        driver.findElement(By.cssSelector("button.theme-toggle")).click();

        String activeTheme = driver.findElement(By.tagName("html")).getAttribute("data-theme");
        Object stored = ((JavascriptExecutor) driver)
            .executeScript("return localStorage.getItem('theme');");

        assertEquals(activeTheme, stored,
            "localStorage['theme'] must match the applied data-theme");
    }

    @Test
    void themeToggle_restoresPersistedTheme_onReload() {
        driver.get(baseUrl);
        driver.findElement(By.cssSelector("button.theme-toggle")).click();
        String themeAfterToggle = driver.findElement(By.tagName("html")).getAttribute("data-theme");

        driver.navigate().refresh();
        String themeAfterReload = driver.findElement(By.tagName("html")).getAttribute("data-theme");

        assertEquals(themeAfterToggle, themeAfterReload,
            "Theme must be restored from localStorage after page reload");
    }

    @Test
    void themeToggle_togglesBackAndForth() {
        driver.get(baseUrl);
        WebElement toggle = driver.findElement(By.cssSelector("button.theme-toggle"));
        WebElement html = driver.findElement(By.tagName("html"));

        String first = html.getAttribute("data-theme");
        toggle.click();
        String second = html.getAttribute("data-theme");
        toggle.click();
        String third = html.getAttribute("data-theme");

        assertNotEquals(first, second, "Theme should change on first click");
        assertEquals(first, third, "Theme should return to initial after two clicks");
    }
}
