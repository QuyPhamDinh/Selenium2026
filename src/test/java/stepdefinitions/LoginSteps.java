package stepdefinitions;

import config.ConfigReader;
import context.ScenarioContext;
import core.DriverManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.DashboardPage;
import pages.LoginPage;

// =============================================
// LoginSteps.java
// =============================================
public class LoginSteps {

    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    // PicoContainer DI — shared state between step files
    private final ScenarioContext context;

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        DriverManager.getDriver().get(ConfigReader.get("base.url") + "/login");
        loginPage = new LoginPage();
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWith(String username, String password) {
        dashboardPage = loginPage.loginAs(username, password);
        context.set("currentUser", username);
    }

    @Then("I should be redirected to the dashboard")
    public void iShouldBeRedirectedToDashboard() {
        Assert.assertTrue(dashboardPage.isPageLoaded(),
                "Dashboard page did not load");
    }

    @Then("I should see welcome message {string}")
    public void iShouldSeeWelcomeMessage(String expectedMsg) {
        Assert.assertEquals(dashboardPage.getWelcomeMessage(), expectedMsg);
    }

    @Then("I should see error message {string}")
    public void iShouldSeeErrorMessage(String expectedMsg) {
        // loginPage still because login failed
        loginPage = new LoginPage();
        Assert.assertEquals(loginPage.getErrorMessage(), expectedMsg);
    }
}
