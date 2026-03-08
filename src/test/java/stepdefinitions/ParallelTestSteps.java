package stepdefinitions;


import io.cucumber.java.en.When;

public class ParallelTestSteps {

    @When("I run a 5 second task for {string}")
    public void iRunA5SecondTask(String scenarioName) throws InterruptedException {

        long threadId = Thread.currentThread().getId();
        String threadName = Thread.currentThread().getName();

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("▶ STARTED: " + scenarioName);
        System.out.println("  Thread ID: " + threadId);
        System.out.println("  Thread Name: " + threadName);
        System.out.println("═══════════════════════════════════════════════");

        for (int second = 1; second <= 5; second++) {
            System.out.println(
                    "⏱ [" + scenarioName + "] " +
                            "Second " + second + "/5 | " +
                            "Thread ID: " + threadId + " | " +
                            "Time: " + java.time.LocalTime.now()
            );
            Thread.sleep(1000);
        }

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("✅ FINISHED: " + scenarioName);
        System.out.println("  Thread ID: " + threadId);
        System.out.println("═══════════════════════════════════════════════");
    }
}
