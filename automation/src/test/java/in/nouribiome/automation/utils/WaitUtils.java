package in.nouribiome.automation.utils;

import in.nouribiome.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {
    private WaitUtils() {}

    private static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds")));
    }

    public static WebElement visible(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement clickable(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean urlContains(WebDriver driver, String value) {
        return wait(driver).until(ExpectedConditions.urlContains(value));
    }
}