package com.telecom.tower.selenium;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScreenshotOnFailureExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Object testInstance = context.getRequiredTestInstance();
        try {
            Method getDriverMethod = testInstance.getClass().getMethod("getDriver");
            WebDriver driver = (WebDriver) getDriverMethod.invoke(testInstance);

            if (driver instanceof TakesScreenshot) {
                byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String screenshotsDir = "target/screenshots";
                Files.createDirectories(Paths.get(screenshotsDir));

                String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9._-]", "_");
                File screenshotFile = new File(screenshotsDir, testName + ".png");

                try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
                    fos.write(screenshotBytes);
                }

                System.out.println("[SELENIUM] Failure screenshot saved to: " + screenshotFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("[SELENIUM] Failed to capture failure screenshot: " + e.getMessage());
        }
    }
}
