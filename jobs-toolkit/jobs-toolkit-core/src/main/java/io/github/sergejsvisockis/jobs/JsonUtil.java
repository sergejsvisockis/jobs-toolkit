package io.github.sergejsvisockis.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utility class for JSON serialization and deserialization.
 * Provides methods to convert objects to JSON strings, bytes, and vice versa.
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    /**
     * Converts an object to its JSON string representation.
     *
     * @param object the object to convert
     * @return the JSON string representation of the object
     */
    public static String toJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert object to JSON", e);
        }
    }

    /**
     * Converts a JSON string to an object of the specified class.
     *
     * @param json  the JSON string to convert
     * @param clazz the class of the object to convert to
     * @param <T>   the type of the object
     * @return the object represented by the JSON string
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert JSON to object", e);
        }
    }

    /**
     * Converts an object to its JSON byte array representation.
     *
     * @param object the object to convert
     * @return the JSON byte array representation of the object
     */
    public static byte[] toBytes(Object object) {
        try {
            return getObjectMapper().writeValueAsBytes(object);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert object to JSON bytes", e);
        }
    }

    /**
     * Converts a JSON byte array to an object of the specified class.
     *
     * @param bytes the JSON byte array to convert
     * @param clazz the class of the object to convert to
     * @param <T>   the type of the object
     * @return the object represented by the JSON byte array
     */
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
