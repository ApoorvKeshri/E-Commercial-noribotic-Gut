package in.nouribiome.automation.pages;

import in.nouribiome.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class BasePage {
    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    protected WebElement visible(By locator) {
        return WaitUtils.visible(driver, locator);
    }

    protected WebElement clickable(By locator) {
        return WaitUtils.clickable(driver, locator);
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }
}