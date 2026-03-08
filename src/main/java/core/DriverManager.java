// src/main/java/core/DriverManager.java

package core;

import org.openqa.selenium.WebDriver;

public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("🗑️ [Thread " + Thread.currentThread().getId()
                        + "] WebDriver session terminated");
            } catch (Exception e) {
                System.out.println("⚠️ [Thread " + Thread.currentThread().getId()
                        + "] WebDriver quit failed: " + e.getMessage());
            } finally {
                // ALWAYS remove from ThreadLocal
                driverThreadLocal.remove();
            }
        }
    }
}