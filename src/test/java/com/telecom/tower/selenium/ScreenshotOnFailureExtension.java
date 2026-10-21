package com.telecom.tower.selenium;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * JUnit 5 extension that captures a PNG screenshot to
 * {@code target/screenshots/<testName>.png} whenever a test FAILS.
 *
 * <p>Implements {@link AfterTestExecutionCallback} (not {@code TestWatcher}) so
 * that the callback fires <em>before</em> {@code @AfterEach} — which is where
 * the WebDriver is quit. Using {@code TestWatcher.testFailed()} would fire
 * <em>after</em> {@code @AfterEach}, making the driver session already dead.
 */
public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        // Only capture on failure
        if (!context.getExecutionException().isPresent()) {
            return;
        }

        Object testInstance = context.getRequiredTestInstance();
        try {
            Method getDriverMethod = testInstance.getClass().getMethod("getDriver");
            WebDriver driver = (WebDriver) getDriverMethod.invoke(testInstance);

            if (driver instanceof TakesScreenshot takesScreenshot) {
                byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);

                String screenshotsDir = "target/screenshots";
                Files.createDirectories(Paths.get(screenshotsDir));

                // Sanitise the display name to make a safe filename
                String testName = context.getDisplayName()
                        .replaceAll("[^a-zA-Z0-9._-]", "_");
                File screenshotFile = new File(screenshotsDir, testName + ".png");

                try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
                    fos.write(screenshotBytes);
                }

                System.out.println("[SELENIUM] Failure screenshot saved to: "
                        + screenshotFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("[SELENIUM] Failed to capture failure screenshot: "
                    + e.getMessage());
        }
    }
}
