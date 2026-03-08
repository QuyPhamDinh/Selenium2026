package hooks;

import config.ConfigReader;
import context.ScenarioContext;
import core.DriverFactory;
import core.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

// =============================================
// Hooks.java — Cucumber Hooks
// =============================================
public class Hooks {

    private final ScenarioContext context;

    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        String browser = System.getProperty("browser", "chrome");
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver driver = new DriverFactory().createDriver(browser, headless);
        DriverManager.setDriver(driver);

        LogManager.getLogger(Hooks.class).info(
                "▶ Starting Scenario: {} | Thread: {}",
                scenario.getName(), Thread.currentThread().getId());
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();

        if (scenario.isFailed() && driver != null) {
            // Attach screenshot to Cucumber report
            byte[] screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png",
                    "failure-" + scenario.getName());

            // Also attach to Allure
            Allure.addAttachment("Failure Screenshot",
                    new ByteArrayInputStream(screenshot));
        }

        DriverManager.quitDriver();
        context.clear();

        LogManager.getLogger(Hooks.class).info(
                "■ Finished Scenario: {} | Status: {}",
                scenario.getName(), scenario.getStatus());
    }

    @Before("@database")
    public void setUpDatabase() {
        // Tagged hook — only runs for @database scenarios
        // Set up DB connection, seed data, etc.
    }
}