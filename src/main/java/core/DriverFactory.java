// src/main/java/core/DriverFactory.java

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

public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    public WebDriver createDriver(String browser, boolean isHeadless) {
        // Check if running on Grid
        boolean isRemote = isRemoteExecution();
        String gridUrl = getGridUrl();

        WebDriver driver;

        switch (BrowserType.valueOf(browser.toUpperCase())) {

            case CHROME -> {
                ChromeOptions options = new ChromeOptions();
                if (isHeadless) options.addArguments("--headless=new");
                options.addArguments(
                        "--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--window-size=1920,1080"
                );

                if (isRemote) {
                    driver = createRemoteDriver(gridUrl, options);
                } else {
                    driver = new ChromeDriver(options);
                }
            }

            case FIREFOX -> {
                FirefoxOptions options = new FirefoxOptions();
                if (isHeadless) options.addArguments("--headless");

                if (isRemote) {
                    driver = createRemoteDriver(gridUrl, options);
                } else {
                    driver = new FirefoxDriver(options);
                }
            }

            case EDGE -> {
                EdgeOptions options = new EdgeOptions();
                if (isHeadless) options.addArguments("--headless=new");

                if (isRemote) {
                    driver = createRemoteDriver(gridUrl, options);
                } else {
                    driver = new EdgeDriver(options);
                }
            }

            default -> throw new FrameworkException(
                    "Unsupported browser: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        log.info("Browser [{}] launched | Remote: {} | Thread: {}",
                browser, isRemote, Thread.currentThread().getId());

        return driver;
    }

    private RemoteWebDriver createRemoteDriver(String gridUrl,
                                               org.openqa.selenium.Capabilities options) {
        try {
            log.info("Connecting to Grid: {}", gridUrl);
            return new RemoteWebDriver(new URL(gridUrl), options);
        } catch (MalformedURLException e) {
            throw new FrameworkException("Invalid Grid URL: " + gridUrl, e);
        }
    }

    /**
     * Check if running remotely:
     * 1. CMD: -Dremote=true
     * 2. Config: remote=true
     * 3. Default: false (local)
     */
    private boolean isRemoteExecution() {
        String cmdRemote = System.getProperty("remote");
        if (cmdRemote != null) {
            return Boolean.parseBoolean(cmdRemote);
        }
        try {
            return ConfigReader.getBoolean("remote");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get Grid URL:
     * 1. CMD: -Dgrid.url=http://...
     * 2. Config: grid.url=http://...
     * 3. Default: http://localhost:4444/wd/hub
     */
    private String getGridUrl() {
        String cmdGridUrl = System.getProperty("grid.url");
        if (cmdGridUrl != null && !cmdGridUrl.isEmpty()) {
            return cmdGridUrl;
        }
        try {
            return ConfigReader.get("grid.url");
        } catch (Exception e) {
            return "http://localhost:4444/wd/hub";
        }
    }
}