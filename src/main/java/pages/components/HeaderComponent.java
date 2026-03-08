package pages.components;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.BasePage;
import pages.LoginPage;

// =============================================
// HeaderComponent.java — for shared UI sections
// =============================================
public class HeaderComponent extends BasePage {

    @FindBy(css = ".user-profile-dropdown")
    private WebElement profileDropdown;

    @FindBy(css = ".logout-btn")
    private WebElement logoutButton;

    @FindBy(css = ".notification-bell")
    private WebElement notificationBell;

    public LoginPage logout() {
        click(profileDropdown);
        click(logoutButton);
        return new LoginPage();
    }

    public int getNotificationCount() {
        return Integer.parseInt(getText(notificationBell));
    }
}
