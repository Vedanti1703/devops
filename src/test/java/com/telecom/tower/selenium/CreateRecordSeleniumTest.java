package com.telecom.tower.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Selenium E2E tests covering maintenance record creation.
 *
 * TC-CR-01 – Log in as tech1, submit the create-record form, and verify
 *             the newly created record appears in the records list.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
class CreateRecordSeleniumTest extends BaseSeleniumTest {

    /** A unique tower ID to avoid collisions between test runs. */
    private static final String TOWER_ID = "TWR-SEL-" + System.currentTimeMillis();

    @Test
    @DisplayName("TC-CR-01: Create a record as tech1 and verify it appears in the list")
    void createRecord_appearsInList() {
        // 1. Login as technician
        login("tech1", "tech123");

        // Wait until we're on the records list page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("page-title")));

        // 2. Navigate directly to the create form URL (avoids click-timing race conditions)
        getDriver().get(baseUrl("/records/new"));

        // Wait for the form to be present and ready
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-create-record")));

        // 3. Fill in the form fields
        setInputValue("input-tower-id", TOWER_ID);
        setInputValue("input-location", "Selenium Test City");

        new Select(getDriver().findElement(By.id("select-issue-type")))
                .selectByValue("POWER_FAILURE");

        new Select(getDriver().findElement(By.id("select-priority")))
                .selectByValue("HIGH");

        setInputValue("input-assigned-tech", "tech1");
        setInputValue("input-description", "Automated Selenium test - power failure simulation.");

        // 4. Submit form via JS submit to ensure reliable form submission in headless Chrome
        jsSubmitForm("form-create-record");

        // 5. Wait until the records list page loads (indicated by the search keyword field)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-search-keyword")));
        wait.until(ExpectedConditions.urlContains("/records"));

        // 6. Assert the new tower ID appears somewhere in the table
        String pageSource = getDriver().getPageSource();
        assertThat(pageSource)
                .as("Newly created record's Tower ID should appear in the records list")
                .contains(TOWER_ID);
    }
}
