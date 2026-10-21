package com.telecom.tower.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
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
 * Starts the application on a random port, launches a headless Chrome
 * instance, and provides common helper methods for login and URL building.
 *
 * The {@link ScreenshotOnFailureExtension} is registered on every subclass
 * via {@code @ExtendWith} so that a PNG is saved to target/screenshots/
 * whenever any test method fails.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSeleniumTest {

    @LocalServerPort
    private int port;

    /** The WebDriver instance shared within each test method. */
    private WebDriver driver;

    /** A convenience wait configured to a short implicit timeout. */
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

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
     * Returns the WebDriver so that the {@link ScreenshotOnFailureExtension}
     * can reach it via reflection (it calls {@code getDriver()}).
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Builds a full URL for the given path (e.g. "/login" → "http://localhost:PORT/login").
     */
    protected String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    /**
     * Navigates to the login page and submits the given credentials.
     * After a successful login the browser will be on /records.
     */
    protected void login(String username, String password) {
        driver.get(baseUrl("/login"));

        WebElement usernameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("input-username")));
        usernameInput.clear();
        usernameInput.sendKeys(username);

        WebElement passwordInput = driver.findElement(By.id("input-password"));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        driver.findElement(By.id("btn-login-submit")).click();

        // Wait until we leave the login page
        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/login")));
    }
}
