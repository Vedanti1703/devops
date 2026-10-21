package com.telecom.tower.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Selenium E2E tests covering the search / filter functionality.
 *
 * TC-S-01 – Create a record with a known towerId, search for it by tower ID,
 *            and verify the result row is present.
 * TC-S-02 – Search for a non-existent tower ID and verify the "no records" row.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
class SearchSeleniumTest extends BaseSeleniumTest {

    private static final String TOWER_ID = "TWR-SRCH-" + System.currentTimeMillis();

    // -----------------------------------------------------------------------
    // Helper: create a record through the UI
    // -----------------------------------------------------------------------

    private void createSearchRecord() {
        getDriver().get(baseUrl("/records/new"));
        // Wait for the form to be rendered (more reliable than urlContains)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-create-record")));

        WebElement towerIdInput = getDriver().findElement(By.id("input-tower-id"));
        towerIdInput.clear();
        towerIdInput.sendKeys(TOWER_ID);
        WebElement locationInput = getDriver().findElement(By.id("input-location"));
        locationInput.clear();
        locationInput.sendKeys("Search Test Location");
        new Select(getDriver().findElement(By.id("select-issue-type")))
                .selectByValue("ANTENNA_MISALIGNMENT");
        new Select(getDriver().findElement(By.id("select-priority")))
                .selectByValue("MEDIUM");
        WebElement techInput = getDriver().findElement(By.id("input-assigned-tech"));
        techInput.clear();
        techInput.sendKeys("tech1");
        WebElement descInput = getDriver().findElement(By.id("input-description"));
        descInput.clear();
        descInput.sendKeys("Record created for search Selenium test.");
        getDriver().findElement(By.id("btn-submit-record")).click();

        // Wait for records list to load after successful save
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-search-keyword")));
    }

    // -----------------------------------------------------------------------
    // Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("TC-S-01: Search by towerId returns the matching record row")
    void searchByTowerId_returnsMatchingRow() {
        login("tech1", "tech123");
        wait.until(ExpectedConditions.urlContains("/records"));

        // Create a record to search for
        createSearchRecord();

        // Type the unique tower ID in the keyword search box
        WebElement keywordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("input-search-keyword")));
        keywordInput.clear();
        keywordInput.sendKeys(TOWER_ID);

        getDriver().findElement(By.id("btn-search-filter")).click();

        // Wait for results to reload
        wait.until(ExpectedConditions.urlContains("keyword="));

        // The table body should contain at least one row with the tower ID
        String pageSource = getDriver().getPageSource();
        assertThat(pageSource).contains(TOWER_ID);

        // "No records" row must NOT be visible
        List<WebElement> noRecordRows = getDriver().findElements(By.id("row-no-records"));
        assertThat(noRecordRows).isEmpty();
    }

    @Test
    @DisplayName("TC-S-02: Search for non-existent towerId shows empty-state row")
    void searchNonExistentTowerId_showsNoRecordsRow() {
        login("tech1", "tech123");
        wait.until(ExpectedConditions.urlContains("/records"));

        WebElement keywordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("input-search-keyword")));
        keywordInput.clear();
        keywordInput.sendKeys("TWR-NONEXISTENT-99999");

        getDriver().findElement(By.id("btn-search-filter")).click();
        wait.until(ExpectedConditions.urlContains("keyword="));

        WebElement noRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("row-no-records")));
        assertThat(noRow.isDisplayed()).isTrue();
    }
}
