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
        wait.until(ExpectedConditions.urlContains("/records"));

        // 2. Click "New Maintenance Ticket"
        WebElement createBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-create-record")));
        createBtn.click();

        // 3. Fill in the form
        wait.until(ExpectedConditions.urlContains("/records/new"));

        getDriver().findElement(By.id("input-tower-id")).sendKeys(TOWER_ID);
        getDriver().findElement(By.id("input-location")).sendKeys("Selenium Test City");

        new Select(getDriver().findElement(By.id("select-issue-type")))
                .selectByValue("POWER_FAILURE");

        new Select(getDriver().findElement(By.id("select-priority")))
                .selectByValue("HIGH");

        getDriver().findElement(By.id("input-assigned-tech")).sendKeys("tech1");
        getDriver().findElement(By.id("input-description"))
                .sendKeys("Automated Selenium test – power failure simulation.");

        // 4. Submit
        getDriver().findElement(By.id("btn-submit-record")).click();

        // 5. Should redirect back to /records with a success flash message
        wait.until(ExpectedConditions.urlContains("/records"));

        // 6. Assert the new tower ID appears somewhere in the table
        String pageSource = getDriver().getPageSource();
        assertThat(pageSource).contains(TOWER_ID);
    }
}
