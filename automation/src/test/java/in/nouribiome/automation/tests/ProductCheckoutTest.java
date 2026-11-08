package in.nouribiome.automation.tests;

import in.nouribiome.automation.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ProductCheckoutTest extends BaseTest {
    @DataProvider(name = "validQuantities")
    public Object[][] validQuantities() {
        return new Object[][]{{1, "₹799"}, {2, "₹1,598"}, {3, "₹2,397"}};
    }

    @Test(dataProvider = "validQuantities",
            description = "Verify quantity and checkout total for supported purchase quantities")
    public void shouldCalculateCheckoutSummary(int targetQuantity, String expectedTotal) {
        homePage.scrollToProduct();

        for (int i = 1; i < targetQuantity; i++) {
            homePage.increaseQuantity();
        }

        Assert.assertEquals(homePage.getQuantity(), targetQuantity,
                "Product quantity should update.");

        homePage.clickBuyNow();

        Assert.assertTrue(checkoutPage.isDisplayed(),
                "Checkout modal should open.");
        Assert.assertEquals(checkoutPage.getSummaryQuantity(),
                String.valueOf(targetQuantity));
        Assert.assertEquals(checkoutPage.getSummaryTotal(), expectedTotal);
    }

    @Test(description = "Verify quantity cannot exceed 10")
    public void shouldNotAllowQuantityAboveTen() {
        homePage.scrollToProduct();

        for (int i = 0; i < 12; i++) {
            homePage.increaseQuantity();
        }

        Assert.assertEquals(homePage.getQuantity(), 10);
    }

    @Test(description = "Verify quantity cannot go below 1")
    public void shouldNotAllowQuantityBelowOne() {
        homePage.scrollToProduct();

        for (int i = 0; i < 3; i++) {
            homePage.decreaseQuantity();
        }

        Assert.assertEquals(homePage.getQuantity(), 1);
    }

    @Test(description = "Verify checkout fields are required before payment")
    public void shouldRequireCustomerDeliveryDetails() {
        homePage.scrollToProduct();
        homePage.clickBuyNow();

        Assert.assertTrue(checkoutPage.isDisplayed());
        Assert.assertTrue(checkoutPage.areRequiredFieldsConfigured(),
                "Name, email, phone and address must be required.");
        Assert.assertFalse(checkoutPage.isCustomerFormValid(),
                "Empty customer form should fail browser validation.");
        Assert.assertEquals(checkoutPage.getPayButtonText(), "CONTINUE TO RAZORPAY");
    }
}