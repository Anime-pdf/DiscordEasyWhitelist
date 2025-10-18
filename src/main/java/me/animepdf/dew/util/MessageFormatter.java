package me.animepdf.dew.util;

public class MessageFormatter {
    public static String format(String message, Object... keyValues) {
        if (message == null) return "";
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Key-value pairs must be even");
        }

        String result = message;
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = keyValues[i].toString();
            Object value = keyValues[i + 1];
            if (value != null) {
                result = result.replace("{" + key + "}", value.toString());
            }
        }

        result = result.replaceAll("\\{[a-zA-Z0-9_]+}", "");
        return result;
    }
}
