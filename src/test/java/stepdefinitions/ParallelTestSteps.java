package stepdefinitions;


import core.DriverManager;
import io.cucumber.java.en.When;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class ParallelTestSteps {

    @When("I run a 5 second task for {string}")
    public void iRunA5SecondTask(String scenarioName) throws InterruptedException {

        WebDriver driver = DriverManager.getDriver();
        long threadId = Thread.currentThread().getId();

        // Navigate to a page
        driver.get("https://www.google.com");

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("▶ STARTED: " + scenarioName);
        System.out.println("  Thread ID: " + threadId);
        System.out.println("  Page Title: " + driver.getTitle());
        System.out.println("═══════════════════════════════════════════════");

        for (int second = 1; second <= 5; second++) {

            // Do something in the browser each second
            String jsTitle = "document.title = '"
                    + scenarioName + " - Second " + second + "/5';";
            ((JavascriptExecutor) driver).executeScript(jsTitle);

            String currentTitle = driver.getTitle();

            System.out.println(
                    "⏱ [" + scenarioName + "] " +
                            "Second " + second + "/5 | " +
                            "Thread ID: " + threadId + " | " +
                            "Title: " + currentTitle + " | " +
                            "Time: " + java.time.LocalTime.now()
            );

            Thread.sleep(1000);
        }

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("✅ FINISHED: " + scenarioName);
        System.out.println("  Thread ID: " + threadId);
        System.out.println("  Final URL: " + driver.getCurrentUrl());
        System.out.println("═══════════════════════════════════════════════");
    }
}
