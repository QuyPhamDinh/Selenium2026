// src/test/java/runners/CrossBrowserRunner.java

package runners;

import hooks.BrowserHolder;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
        features = "src/test/resources/features/parallel",
        glue = {"stepdefinitions", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cross-browser.html",
                "json:target/cucumber-reports/cucumber.json",
                "rerun:target/failed_scenarios.txt",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@parallel-test"
)
public class CrossBrowserRunner extends AbstractTestNGCucumberTests {

    private String browser;

    @BeforeTest
    public void setUpBrowser(ITestContext context) {
        // Read directly from XML — no @Parameters annotation
        String xmlBrowser = context.getCurrentXmlTest().getParameter("browser");

        if (xmlBrowser != null && !xmlBrowser.isEmpty()) {
            this.browser = xmlBrowser;
        } else {
            // Fallback: CMD → default
            this.browser = System.getProperty("browser", "chrome");
        }

        System.out.println("🌐 [Thread " + Thread.currentThread().getId()
                + "] CrossBrowserRunner @BeforeTest: browser=" + this.browser
                + " (from XML parameter)");
    }

    @Override
    @Test(groups = "cucumber", dataProvider = "scenarios")
    public void runScenario(PickleWrapper pickleWrapper,
                            FeatureWrapper featureWrapper) {
        // Set browser for THIS thread before Cucumber hooks fire
        BrowserHolder.set(this.browser);

        System.out.println("🔗 [Thread " + Thread.currentThread().getId()
                + "] runScenario: BrowserHolder set to " + this.browser);

        super.runScenario(pickleWrapper, featureWrapper);
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}