package com.codemie.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class CalculatorSmokeTest extends BaseUiTest {

    private String getScreenValue() {
        return (String) ((JavascriptExecutor) driver)
            .executeScript("return document.getElementById('screen').value;");
    }

    private void clickKey(String dataValue) {
        driver.findElement(
            By.cssSelector("[data-action='digit'][data-value='" + dataValue + "']")).click();
    }

    private void clickOperator(String op) {
        driver.findElement(
            By.cssSelector("[data-action='operator'][data-value='" + op + "']")).click();
    }

    @Test
    void pageLoads_withInitialValueZero() {
        driver.get(baseUrl);
        assertEquals("0", getScreenValue(), "Initial display should be 0");
    }

    @Test
    void calculatorTitle_isPresent() {
        driver.get(baseUrl);
        WebElement title = driver.findElement(By.cssSelector(".title"));
        assertEquals("Calculator", title.getText().trim());
    }

    @Test
    void basicAddition_oneAndTwo_equalsThree() {
        driver.get(baseUrl);
        clickKey("1");
        clickOperator("+");
        clickKey("2");
        driver.findElement(By.cssSelector("[data-action='equals']")).click();
        assertEquals("3", getScreenValue(), "1 + 2 should equal 3");
    }

    @Test
    void basicSubtraction_fiveMinusThree_equalsTwo() {
        driver.get(baseUrl);
        clickKey("5");
        clickOperator("-");
        clickKey("3");
        driver.findElement(By.cssSelector("[data-action='equals']")).click();
        assertEquals("2", getScreenValue(), "5 - 3 should equal 2");
    }

    @Test
    void basicMultiplication_threeByFour_equalsTwelve() {
        driver.get(baseUrl);
        clickKey("3");
        clickOperator("*");
        clickKey("4");
        driver.findElement(By.cssSelector("[data-action='equals']")).click();
        assertEquals("12", getScreenValue(), "3 * 4 should equal 12");
    }

    @Test
    void basicDivision_eightByTwo_equalsFour() {
        driver.get(baseUrl);
        clickKey("8");
        clickOperator("/");
        clickKey("2");
        driver.findElement(By.cssSelector("[data-action='equals']")).click();
        assertEquals("4", getScreenValue(), "8 / 2 should equal 4");
    }

    @Test
    void clearButton_resetsDisplay() {
        driver.get(baseUrl);
        clickKey("9");
        driver.findElement(By.cssSelector("[data-action='clear']")).click();
        assertEquals("0", getScreenValue(), "Clear should reset display to 0");
    }

    @Test
    void divisionByZero_showsError() {
        driver.get(baseUrl);
        clickKey("5");
        clickOperator("/");
        clickKey("0");
        driver.findElement(By.cssSelector("[data-action='equals']")).click();
        String result = getScreenValue();
        assertTrue(result.equalsIgnoreCase("Error") || result.contains("Infinity"),
            "Division by zero should show Error, got: " + result);
    }

    @Test
    void calculatorOperations_unaffectedByThemeToggle() {
        driver.get(baseUrl);
        clickKey("7");
        clickOperator("+");
        clickKey("3");

        // Toggle theme mid-calculation
        driver.findElement(By.cssSelector("button.theme-toggle")).click();

        driver.findElement(By.cssSelector("[data-action='equals']")).click();
        assertEquals("10", getScreenValue(),
            "Calculator result must be correct after theme toggle");
    }
}
