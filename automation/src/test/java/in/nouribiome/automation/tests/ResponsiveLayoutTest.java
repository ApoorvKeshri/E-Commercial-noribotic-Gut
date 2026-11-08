package in.nouribiome.automation.tests;

import in.nouribiome.automation.base.BaseTest;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ResponsiveLayoutTest extends BaseTest {
    @Test(description = "Verify mobile navigation is hidden at phone viewport width")
    public void shouldUseMobileNavigationLayout() {
        driver.manage().window().setSize(new Dimension(390, 844));
        Assert.assertTrue(homePage.areNavigationLinksHidden(),
                "Desktop navigation links should be hidden on a narrow viewport.");
    }
}