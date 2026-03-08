// src/test/java/runners/ParallelTestRunner.java

package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features/parallel",
        glue = {"stepdefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/parallel-test.html"
        },
        tags = "@parallel-test"
)
public class ParallelTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)   // ← enables parallel scenarios
    public Object[][] scenarios() {
        return super.scenarios();
    }
}