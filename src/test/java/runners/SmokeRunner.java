package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

// =============================================
// Tag-Specific Runners
// =============================================
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.company.tests.stepdefinitions",
                "com.company.tests.hooks"},
        tags = "@smoke",
        plugin = {"pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"}
)
public class SmokeRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}