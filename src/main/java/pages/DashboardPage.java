package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.components.HeaderComponent;

// =============================================
// DashboardPage.java
// =============================================
public class DashboardPage extends BasePage {

    @FindBy(css = ".welcome-msg")
    private WebElement welcomeMessage;

    @FindBy(css = ".nav-menu")
    private WebElement navMenu;

    private HeaderComponent header;

    public DashboardPage() {
        super();
        this.header = new HeaderComponent();
    }

    public String getWelcomeMessage() {
        return getText(welcomeMessage);
    }

    public boolean isPageLoaded() {
        waitForUrlContains("/dashboard");
        return isDisplayed(welcomeMessage);
    }

    public HeaderComponent getHeader() {
        return header;
    }
}
