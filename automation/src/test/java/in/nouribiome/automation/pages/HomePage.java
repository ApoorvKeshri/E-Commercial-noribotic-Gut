package in.nouribiome.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private static final By BRAND_LOGO = By.cssSelector("nav .logo");
    private static final By HERO_HEADING = By.cssSelector(".hero h1");
    private static final By PRODUCT_SECTION = By.id("product");
    private static final By QUANTITY = By.id("qty");
    private static final By INCREASE_QUANTITY = By.cssSelector(".qty button[aria-label='Increase quantity']");
    private static final By DECREASE_QUANTITY = By.cssSelector(".qty button[aria-label='Decrease quantity']");
    private static final By BUY_NOW = By.xpath("//button[normalize-space()='BUY NOW']");
    private static final By NAV_LINKS = By.cssSelector(".navlinks");
    private static final By FAQ_FIRST_SUMMARY = By.cssSelector(".faq details:first-child summary");
    private static final By FAQ_FIRST_DETAILS = By.cssSelector(".faq details:first-child");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open(String baseUrl) {
        driver.get(baseUrl);
        return this;
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getBrandText() {
        return text(BRAND_LOGO);
    }

    public String getHeroHeading() {
        return text(HERO_HEADING);
    }

    public void scrollToProduct() {
        driver.findElement(PRODUCT_SECTION).getLocation();
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});",
                        driver.findElement(PRODUCT_SECTION));
    }

    public void increaseQuantity() {
        click(INCREASE_QUANTITY);
    }

    public void decreaseQuantity() {
        click(DECREASE_QUANTITY);
    }

    public int getQuantity() {
        return Integer.parseInt(text(QUANTITY));
    }

    public void clickBuyNow() {
        click(BUY_NOW);
    }

    public boolean areNavigationLinksHidden() {
        return !driver.findElement(NAV_LINKS).isDisplayed();
    }

    public void openFirstFaq() {
        click(FAQ_FIRST_SUMMARY);
    }

    public boolean isFirstFaqExpanded() {
        return driver.findElement(FAQ_FIRST_DETAILS).getAttribute("open") != null;
    }
}