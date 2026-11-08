package in.nouribiome.automation.base;

import in.nouribiome.automation.config.ConfigReader;
import in.nouribiome.automation.driver.DriverFactory;
import in.nouribiome.automation.pages.CheckoutPage;
import in.nouribiome.automation.pages.HomePage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {
    protected WebDriver driver;
    protected HomePage homePage;
    protected CheckoutPage checkoutPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initializeDriver();
        driver = DriverFactory.getDriver();
        homePage = new HomePage(driver);
        checkoutPage = new CheckoutPage(driver);
        homePage.open(ConfigReader.get("baseUrl"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}