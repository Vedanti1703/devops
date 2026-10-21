package com.telecom.tower.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Selenium E2E tests covering the login flow.
 *
 * TC-L-01 – Valid login as tech1 lands on the maintenance records page.
 * TC-L-02 – Invalid credentials show an error message on the login page.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
class LoginSeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("TC-L-01: Valid login redirects to records page")
    void validLogin_redirectsToRecordsPage() {
        login("tech1", "tech123");

        // After login Spring Security redirects to /records (defaultSuccessUrl)
        wait.until(ExpectedConditions.urlContains("/records"));

        assertThat(getDriver().getCurrentUrl()).contains("/records");
        // The page should contain the known heading element
        WebElement heading = getDriver().findElement(By.id("page-title"));
        assertThat(heading.getText()).containsIgnoringCase("Maintenance Records");
    }

    @Test
    @DisplayName("TC-L-02: Invalid credentials show login error alert")
    void invalidLogin_showsErrorAlert() {
        getDriver().get(baseUrl("/login"));

        WebElement usernameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("input-username")));
        usernameInput.sendKeys("bad_user");
        getDriver().findElement(By.id("input-password")).sendKeys("wrong_pass");
        getDriver().findElement(By.id("btn-login-submit")).click();

        // Spring Security appends ?error on failure → failureUrl("/login?error=true")
        wait.until(ExpectedConditions.urlContains("/login"));

        WebElement errorAlert = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("alert-login-error")));
        assertThat(errorAlert.getText()).containsIgnoringCase("Invalid username or password");
    }
}
