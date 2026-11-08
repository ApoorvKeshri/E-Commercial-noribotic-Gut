package in.nouribiome.automation.utils;

import in.nouribiome.automation.config.ConfigReader;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {
    private ScreenshotUtils() {}

    public static byte[] capture(WebDriver driver, String testName) {
        if (driver == null) return new byte[0];
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path directory = Path.of(ConfigReader.get("screenshotDirectory"));
            Files.createDirectories(directory);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS"));
            String safeName = testName.replaceAll("[^a-zA-Z0-9-_]", "_");
            Files.write(directory.resolve(safeName + "-" + timestamp + ".png"), screenshot);
            return screenshot;
        } catch (IOException | RuntimeException exception) {
            System.err.println("Could not save failure screenshot: " + exception.getMessage());
            return new byte[0];
        }
    }
}