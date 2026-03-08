package core;


import config.ConfigReader;
import core.exception.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

// =============================================
// DriverFactory.java — Factory Pattern
// =============================================
public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    public WebDriver createDriver(String browser, boolean isHeadless) {
        WebDriver driver = null;

        switch (BrowserType.valueOf(browser.toUpperCase())) {
            case CHROME -> {
                ChromeOptions options = new ChromeOptions();
                if (isHeadless) options.addArguments("--headless=new");
                options.addArguments("--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--window-size=1920,1080");
                driver = new ChromeDriver(options);
            }
            case FIREFOX -> {
                FirefoxOptions options = new FirefoxOptions();
                if (isHeadless) options.addArguments("--headless");
                driver = new FirefoxDriver(options);
            }
            case EDGE -> {
                EdgeOptions options = new EdgeOptions();
                if (isHeadless) options.addArguments("--headless=new");
                driver = new EdgeDriver(options);
            }
            // Remote/Grid Execution
            case REMOTE -> {
                ChromeOptions options = new ChromeOptions();
                if (isHeadless) options.addArguments("--headless=new");
                try {
                    driver = new RemoteWebDriver(
                            new URL(ConfigReader.get("grid.url")), options);
                } catch (MalformedURLException e) {
                    throw new FrameworkException("Invalid Grid URL", e);
                }
            }
            default -> throw new FrameworkException(
                    "Unsupported browser: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(
                Long.parseLong(ConfigReader.get("implicit.wait"))));
        driver.manage().window().maximize();

        log.info("Browser [{}] launched successfully on thread: {}",
                browser, Thread.currentThread().getId());
        return driver;
    }
}