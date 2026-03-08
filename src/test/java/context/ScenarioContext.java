package context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// =============================================
// ScenarioContext.java — share state between steps
// =============================================
public class ScenarioContext {

    private final Map<String, Object> contextMap = new ConcurrentHashMap<>();

    public void set(String key, Object value) {
        contextMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) contextMap.get(key);
    }

    public boolean contains(String key) {
        return contextMap.containsKey(key);
    }

    public void clear() {
        contextMap.clear();
    }
}
