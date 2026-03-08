package stepdefinitions;


import config.ConfigReader;
import core.DriverManager;
import io.cucumber.java.en.When;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ParallelTestSteps {

    @When("I run a 5 second task for {string}")
    public void iRunA5SecondTask(String scenarioName) throws InterruptedException {


        WebDriver driver = DriverManager.getDriver();
        long threadId = Thread.currentThread().getId();

        // Get actual browser name from driver
        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        String browserName = caps.getBrowserName().toUpperCase();

        driver.get(ConfigReader.get("base.url"));

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("▶ STARTED: " + scenarioName);
        System.out.println("  Browser: " + browserName);
        System.out.println("  Thread ID: " + threadId);
        System.out.println("  Page Title: " + driver.getTitle());
        System.out.println("═══════════════════════════════════════════════");

        for (int second = 1; second <= 5; second++) {

            String jsTitle = "document.title = '"
                    + scenarioName + " [" + browserName + "] - Second "
                    + second + "/5';";
            ((JavascriptExecutor) driver).executeScript(jsTitle);

            System.out.println(
                    "⏱ [" + scenarioName + "] " +
                            "Sec " + second + "/5 | " +
                            "Thread " + threadId + " | " +
                            browserName + " | " +
                            java.time.LocalTime.now()
            );

            Thread.sleep(1000);
        }

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("✅ FINISHED: " + scenarioName + " [" + browserName + "]");
        System.out.println("  Thread ID: " + threadId);
        System.out.println("═══════════════════════════════════════════════");
    }
}
