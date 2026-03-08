package config;

import core.exception.FrameworkException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// =============================================
// ConfigReader.java — Singleton Pattern
// =============================================
public final class ConfigReader {

    private static Properties properties;
    private static final String DEFAULT_ENV = "qa";

    private ConfigReader() {
    }

    static {
        loadConfig();
    }

    private static void loadConfig() {
        String env = System.getProperty("env", DEFAULT_ENV);
        String filePath = "config/" + env + ".properties";

        try (InputStream is = ConfigReader.class.getClassLoader()
                .getResourceAsStream(filePath)) {
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            throw new FrameworkException(
                    "Failed to load config for env: " + env, e);
        }

        // Allow system property overrides
        // e.g., -Dbase.url=https://override.com
        for (String key : properties.stringPropertyNames()) {
            String sysVal = System.getProperty(key);
            if (sysVal != null) {
                properties.setProperty(key, sysVal);
            }
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null)
            throw new FrameworkException("Property not found: " + key);
        return value.trim();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
