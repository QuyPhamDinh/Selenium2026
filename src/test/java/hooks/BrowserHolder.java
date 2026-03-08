// src/test/java/hooks/BrowserHolder.java

package hooks;

public class BrowserHolder {

    private static final ThreadLocal<String> browserThread = new ThreadLocal<>();

    public static void set(String browser) {
        browserThread.set(browser);
    }

    public static String get() {
        return browserThread.get();
    }

    public static void clear() {
        browserThread.remove();
    }
}