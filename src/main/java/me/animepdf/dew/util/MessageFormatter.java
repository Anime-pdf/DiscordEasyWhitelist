package me.animepdf.dew.util;

import java.util.HashMap;
import java.util.Map;

public class MessageFormatter {
    private final Map<String, String> placeholders = new HashMap<>();

    public MessageFormatter set(String key, Object value) {
        if (value != null) {
            this.placeholders.put("{" + key + "}", value.toString());
        }
        return this;
    }

    public String apply(String message) {
        if (message == null) return "";
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        // remove unused placeholders
        result = result.replaceAll("\\{[a-zA-Z0-9_]+}", "");
        return result;
    }

    public static MessageFormatter create() {
        return new MessageFormatter();
    }
}
