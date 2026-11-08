package in.nouribiome.automation.tests;

import in.nouribiome.automation.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {
    @Test(description = "Verify storefront title, brand and hero content")
    public void shouldDisplayNouriBiomeHomePage() {
        Assert.assertTrue(homePage.getPageTitle().contains("NouriBiome"),
                "Browser title should identify NouriBiome.");
        Assert.assertTrue(homePage.getBrandText().toLowerCase().contains("nouri"),
                "Brand logo should be visible.");
        Assert.assertTrue(homePage.getHeroHeading().contains("Feel good"),
                "Hero heading should be visible.");
    }

    @Test(description = "Verify FAQ accordion expands")
    public void shouldExpandFirstFaq() {
        homePage.openFirstFaq();
        Assert.assertTrue(homePage.isFirstFaqExpanded(),
                "First FAQ details element should be expanded.");
    }
}