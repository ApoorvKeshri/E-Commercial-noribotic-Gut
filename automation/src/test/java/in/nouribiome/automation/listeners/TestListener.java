package in.nouribiome.automation.listeners;

import in.nouribiome.automation.driver.DriverFactory;
import in.nouribiome.automation.utils.ScreenshotUtils;
import io.qameta.allure.Attachment;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            byte[] screenshot = ScreenshotUtils.capture(driver, result.getName());
            if (screenshot.length > 0) attachScreenshot(screenshot);
            System.err.println("FAILED: " + result.getName() + " — " + result.getThrowable());
        } catch (Exception exception) {
            System.err.println("Failure diagnostics unavailable: " + exception.getMessage());
        }
    }

    @Attachment(value = "Failure screenshot", type = "image/png")
    public byte[] attachScreenshot(byte[] screenshot) {
        return screenshot;
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("PASSED: " + result.getName());
    }
}