package io.github.sergejsvisockis.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonUtil {

    private JsonUtil() {
    }

    public static String toJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert object to JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert JSON to object", e);
        }
    }

    public static byte[] toBytes(Object object) {
        try {
            return getObjectMapper().writeValueAsBytes(object);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert object to JSON bytes", e);
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(bytes, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert JSON to object", e);
        }
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

}
