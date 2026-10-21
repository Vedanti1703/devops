package com.telecom.tower.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

/**
 * Base class for all Selenium end-to-end tests.
 *
 * <p>Starts the application on a random port, launches a headless Chrome
 * instance, and provides common helper methods for login and URL building.
 *
 * <p><strong>Driver timing strategy:</strong> implicit wait is deliberately set
 * to 0. Mixing implicit and explicit ({@link WebDriverWait}) waits causes
 * non-deterministic behaviour.  All synchronisation is done via explicit waits.
 *
 * <p>The {@link ScreenshotOnFailureExtension} (registered in each subclass via
 * {@code @ExtendWith}) fires via {@code AfterTestExecutionCallback} — before
 * {@code @AfterEach} — so the driver is still alive when the screenshot is taken.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSeleniumTest {

    @LocalServerPort
    private int port;

    /** Shared WebDriver instance for the current test method. */
    private WebDriver driver;

    /** Explicit wait with a generous timeout for slower CI environments. */
    protected WebDriverWait wait;

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @BeforeEach
    void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        // Implicit wait MUST be 0 – mixing with explicit waits causes races.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    // -----------------------------------------------------------------------
    // Helpers exposed to subclasses
    // -----------------------------------------------------------------------

    /**
     * Returns the WebDriver so the {@link ScreenshotOnFailureExtension} can
     * reach it via reflection ({@code getDriver()}).
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Builds a full URL for a given path, e.g. "/login" →
     * "http://localhost:PORT/login".
     */
    protected String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    /**
     * Navigates to the login page and submits the given credentials.
     * Blocks until the browser has left the login page entirely.
     */
    protected void login(String username, String password) {
        driver.get(baseUrl("/login"));

        setInputValue("input-username", username);
        setInputValue("input-password", password);

        // Use JS to submit the login form – avoids click-timing issues in headless mode.
        jsSubmitForm("form-login");

        // Wait until the browser has navigated away from the login page.
        wait.until(d -> !d.getCurrentUrl().contains("/login"));
    }

    /**
     * Submits any form via JavaScript's {@code form.submit()}.
     *
     * <p>This is more reliable than {@code button.click()} in headless Chrome
     * because it bypasses potential issues where a click event is swallowed by
     * overlapping elements or the form is not fully interactive yet.
     * The CSRF hidden input added by Thymeleaf is included automatically.
     *
     * @param formId the HTML {@code id} attribute of the {@code <form>} element
     */
    protected void jsSubmitForm(String formId) {
        WebElement form = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id(formId)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].submit();", form);
    }

    /**
     * Clicks an element via JavaScript execution.
     *
     * @param element the WebElement to click
     */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Locates an element and clicks it via JavaScript execution.
     *
     * @param locator the By locator for the element to click
     */
    protected void jsClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Sets the value of a text input or textarea by id. Focuses, clears, sends keys,
     * and ensures the DOM value is accurately set.
     *
     * @param id the HTML element id
     * @param value the text value to enter
     */
    protected void setInputValue(String id, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        element.click();
        element.clear();
        element.sendKeys(value);
        if (!value.equals(element.getAttribute("value"))) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];" +
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                    "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    element, value);
        }
    }
}
