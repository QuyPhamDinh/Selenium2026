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
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class Hooks {

    private static final Logger log = LogManager.getLogger(Hooks.class);
    private final ScenarioContext context;

    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        long threadId = Thread.currentThread().getId();

        // Priority: CMD argument → config file → default "chrome"
        String browser = getBrowser();
        boolean headless = getHeadless();

        System.out.println("🔧 [Thread " + threadId + "] Launching "
                + browser + " (headless=" + headless + ") for: "
                + scenario.getName());

        WebDriver driver = new DriverFactory().createDriver(browser, headless);
        DriverManager.setDriver(driver);

        System.out.println("✅ [Thread " + threadId + "] Browser launched for: "
                + scenario.getName());

        log.info("▶ Starting Scenario: {} | Thread: {} | Browser: {} | Headless: {}",
                scenario.getName(), threadId, browser, headless);
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        long threadId = Thread.currentThread().getId();
        WebDriver driver = DriverManager.getDriver();

        if (scenario.isFailed() && driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png",
                    "failure-" + scenario.getName());

            Allure.addAttachment("Failure Screenshot",
                    new ByteArrayInputStream(screenshot));

            System.out.println("📸 [Thread " + threadId
                    + "] Screenshot captured for: " + scenario.getName());
        }

        System.out.println("🔧 [Thread " + threadId + "] Closing browser for: "
                + scenario.getName());

        DriverManager.quitDriver();
        context.clear();

        System.out.println("✅ [Thread " + threadId + "] Browser closed for: "
                + scenario.getName() + " | Status: " + scenario.getStatus());

        log.info("■ Finished Scenario: {} | Thread: {} | Status: {}",
                scenario.getName(), threadId, scenario.getStatus());
    }

    @Before("@database")
    public void setUpDatabase() {
        // Tagged hook — only runs for @database scenarios
    }

    /**
     * Resolves browser from:
     * 1. CMD: -Dbrowser=firefox       (highest priority)
     * 2. Config: browser=chrome       (from qa.properties)
     * 3. Default: "chrome"            (fallback)
     */
    private String getBrowser() {
        String cmdBrowser = System.getProperty("browser");
        if (cmdBrowser != null && !cmdBrowser.isEmpty()) {
            return cmdBrowser;
        }

        try {
            return ConfigReader.get("browser");
        } catch (Exception e) {
            return "chrome";
        }
    }

    /**
     * Resolves headless from:
     * 1. CMD: -Dheadless=true         (highest priority)
     * 2. Config: headless=false       (from qa.properties)
     * 3. Default: false               (fallback)
     */
    private boolean getHeadless() {
        String cmdHeadless = System.getProperty("headless");
        if (cmdHeadless != null && !cmdHeadless.isEmpty()) {
            return Boolean.parseBoolean(cmdHeadless);
        }

        try {
            return ConfigReader.getBoolean("headless");
        } catch (Exception e) {
            return false;
        }
    }
}