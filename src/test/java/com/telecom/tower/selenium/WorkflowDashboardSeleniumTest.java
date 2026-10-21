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
 * Selenium E2E tests covering the workflow status transitions and the KPI dashboard.
 *
 * TC-W-01 – Full lifecycle: tech1 creates OPEN → starts work (IN_PROGRESS)
 *            → marks completed (COMPLETED) → sup1 verifies (VERIFIED)
 *            → admin closes (CLOSED).
 * TC-W-02 – The KPI dashboard is reachable and displays at least the
 *            "stat-total-count" element.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
class WorkflowDashboardSeleniumTest extends BaseSeleniumTest {

    private static final String TOWER_ID = "TWR-WF-" + System.currentTimeMillis();

    // -----------------------------------------------------------------------
    // Helper: create a record via the UI (as tech1, already logged in)
    // -----------------------------------------------------------------------

    private String createRecordAndGetDetailUrl() {
        getDriver().get(baseUrl("/records/new"));
        wait.until(ExpectedConditions.urlContains("/records/new"));

        getDriver().findElement(By.id("input-tower-id")).sendKeys(TOWER_ID);
        getDriver().findElement(By.id("input-location")).sendKeys("Workflow Test Site");
        new Select(getDriver().findElement(By.id("select-issue-type")))
                .selectByValue("FIBER_CUT");
        new Select(getDriver().findElement(By.id("select-priority")))
                .selectByValue("CRITICAL");
        getDriver().findElement(By.id("input-assigned-tech")).sendKeys("tech1");
        getDriver().findElement(By.id("input-description"))
                .sendKeys("Workflow Selenium test – fiber cut simulation.");
        getDriver().findElement(By.id("btn-submit-record")).click();

        // After save we're back on /records – find the row for our tower and click View
        wait.until(ExpectedConditions.urlContains("/records"));

        // Search for the newly created record to get its detail URL
        WebElement kwInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("input-search-keyword")));
        kwInput.clear();
        kwInput.sendKeys(TOWER_ID);
        getDriver().findElement(By.id("btn-search-filter")).click();
        wait.until(ExpectedConditions.urlContains("keyword="));

        // Click the first "View" button in the results table
        WebElement viewBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("#tbody-records a.btn-outline-primary")));
        String detailUrl = viewBtn.getAttribute("href");
        viewBtn.click();
        wait.until(ExpectedConditions.urlContains("/records/"));
        return detailUrl;
    }

    // -----------------------------------------------------------------------
    // TC-W-01: Full lifecycle workflow test
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("TC-W-01: Full workflow OPEN→IN_PROGRESS→COMPLETED→VERIFIED→CLOSED")
    void fullWorkflowTransition_allStatuses() {

        // ── Step 1: tech1 creates record (status = OPEN) ──────────────────
        login("tech1", "tech123");
        createRecordAndGetDetailUrl();

        // Status should be OPEN
        WebElement statusBadge = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("detail-status")));
        assertThat(statusBadge.getText()).isEqualTo("OPEN");
        statusBadge = null; // reset to avoid stale references below

        // ── Step 2: tech1 clicks "Start Work" → IN_PROGRESS ───────────────
        WebElement startBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-start-work")));
        startBtn.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("detail-status"), "IN_PROGRESS"));
        assertThat(getDriver().findElement(By.id("detail-status")).getText())
                .isEqualTo("IN_PROGRESS");

        // ── Step 3: tech1 clicks "Mark Completed" → COMPLETED ─────────────
        WebElement completeBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-complete-work")));
        String currentDetailUrl = getDriver().getCurrentUrl();
        completeBtn.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("detail-status"), "COMPLETED"));
        assertThat(getDriver().findElement(By.id("detail-status")).getText())
                .isEqualTo("COMPLETED");

        // ── Step 4: Log out and re-login as sup1 ─────────────────────────
        getDriver().get(baseUrl("/logout"));
        wait.until(ExpectedConditions.urlContains("/login"));
        login("sup1", "sup123");

        // Navigate back to the same record detail page
        getDriver().get(currentDetailUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("detail-status")));
        assertThat(getDriver().findElement(By.id("detail-status")).getText())
                .isEqualTo("COMPLETED");

        // ── Step 5: sup1 verifies work → VERIFIED ────────────────────────
        WebElement verifyBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-approve-verify")));
        verifyBtn.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("detail-status"), "VERIFIED"));
        assertThat(getDriver().findElement(By.id("detail-status")).getText())
                .isEqualTo("VERIFIED");

        // ── Step 6: Log out and re-login as admin ────────────────────────
        getDriver().get(baseUrl("/logout"));
        wait.until(ExpectedConditions.urlContains("/login"));
        login("admin", "admin123");

        // Navigate back to the same record detail page
        getDriver().get(currentDetailUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("detail-status")));
        assertThat(getDriver().findElement(By.id("detail-status")).getText())
                .isEqualTo("VERIFIED");

        // ── Step 7: admin closes ticket → CLOSED ─────────────────────────
        WebElement closeBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-close-record")));
        closeBtn.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("detail-status"), "CLOSED"));
        assertThat(getDriver().findElement(By.id("detail-status")).getText())
                .isEqualTo("CLOSED");

        // "Ticket closed" message should appear instead of action buttons
        WebElement closedMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("msg-ticket-closed")));
        assertThat(closedMsg.isDisplayed()).isTrue();
    }

    // -----------------------------------------------------------------------
    // TC-W-02: Dashboard page is accessible and shows KPI stats
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("TC-W-02: KPI dashboard loads and displays total-ticket count")
    void dashboard_isAccessibleAndShowsStats() {
        login("admin", "admin123");

        getDriver().get(baseUrl("/dashboard"));
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // The dashboard title element should be visible
        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("dashboard-title")));
        assertThat(title.getText()).containsIgnoringCase("Dashboard");

        // The KPI stat cards must be present
        assertThat(getDriver().findElement(By.id("stat-total-count")).isDisplayed()).isTrue();
        assertThat(getDriver().findElement(By.id("stat-pending-count")).isDisplayed()).isTrue();

        // "View All Records" navigation button must be clickable
        WebElement navBtn = getDriver().findElement(By.id("btn-dashboard-to-records"));
        assertThat(navBtn.isDisplayed()).isTrue();
        navBtn.click();
        wait.until(ExpectedConditions.urlContains("/records"));
    }
}
