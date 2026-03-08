package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

// Separate runner that ONLY picks up failures
@CucumberOptions(
        features = "@target/failed_scenarios.txt",   // ← reads failed
        glue = {"com.company.tests.stepdefinitions",
                "com.company.tests.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/rerun-report.html",
                "json:target/cucumber-reports/rerun.json"
        }
)
public class FailedRerunRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
