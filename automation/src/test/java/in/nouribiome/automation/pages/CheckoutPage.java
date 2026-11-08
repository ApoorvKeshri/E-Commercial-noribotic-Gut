package in.nouribiome.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {
    private static final By MODAL = By.id("modal");
    private static final By CHECKOUT_TITLE = By.id("checkoutTitle");
    private static final By SUMMARY_QUANTITY = By.id("summaryQty");
    private static final By SUMMARY_TOTAL = By.id("summaryTotal");
    private static final By ORDER_FORM = By.id("orderForm");
    private static final By NAME = By.name("name");
    private static final By EMAIL = By.name("email");
    private static final By PHONE = By.name("phone");
    private static final By ADDRESS = By.name("address");
    private static final By PAY_BUTTON = By.id("payButton");
    private static final By CLOSE_BUTTON = By.cssSelector("#modal .close");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return "flex".equals(visible(MODAL).getCssValue("display"));
    }

    public String getTitle() {
        return text(CHECKOUT_TITLE);
    }

    public String getSummaryQuantity() {
        return text(SUMMARY_QUANTITY);
    }

    public String getSummaryTotal() {
        return text(SUMMARY_TOTAL);
    }

    public boolean isCustomerFormValid() {
        return (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", visible(ORDER_FORM));
    }

    public boolean areRequiredFieldsConfigured() {
        return visible(NAME).getAttribute("required") != null
                && visible(EMAIL).getAttribute("required") != null
                && visible(PHONE).getAttribute("required") != null
                && visible(ADDRESS).getAttribute("required") != null;
    }

    public String getPayButtonText() {
        return text(PAY_BUTTON);
    }

    public void close() {
        click(CLOSE_BUTTON);
    }
}