package io.github.sergejsvisockis.jobs;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonUtilTest {

    @Test
    void shouldConvertObjectToJsonString() {
        // given
        TestObject testObject = new TestObject("test", 123);

        // when
        String result = JsonUtil.toJson(testObject);

        // then
        assertNotNull(result);
        assertEquals("{\"name\":\"test\",\"value\":123}", result);
    }

    @Test
    void shouldThrowExceptionWhenToJsonFails() {
        // given
        Object problematicObject = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Simulated failure");
            }
        };

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> JsonUtil.toJson(problematicObject));

        // then
        assertEquals("Failed to convert object to JSON", exception.getMessage());
    }

    @Test
    void shouldConvertJsonStringToObject() {
        // given
        String json = "{\"name\":\"test\",\"value\":123}";

        // when
        TestObject testObject = JsonUtil.fromJson(json, TestObject.class);

        // then
        assertNotNull(testObject);
        assertEquals("test", testObject.getName());
        assertEquals(123, testObject.getValue());
    }

    @Test
    void shouldThrowExceptionWhenFromJsonFails() {
        // given
        String invalidJson = "{invalid json}";

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> JsonUtil.fromJson(invalidJson, TestObject.class));

        // then
        assertEquals("Failed to convert JSON to object", exception.getMessage());
    }

    @Test
    void shouldConvertObjectToJsonBytes() {
        // given
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");

        // when
        byte[] result = JsonUtil.toBytes(map);

        // then
        assertNotNull(result);
        String json = new String(result);
        assertEquals("{\"key\":\"value\"}", json);
    }

    @Test
    void shouldThrowExceptionWhenToBytesFailsWithNonSerializableObject() {
        // given
        Object problematicObject = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Simulated failure");
            }
        };

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> JsonUtil.toBytes(problematicObject));

        // then
        assertEquals("Failed to convert object to JSON bytes", exception.getMessage());
    }

    @Test
    void shouldConvertJsonBytesToObject() {
        // given
        byte[] bytes = "{\"name\":\"test\",\"value\":123}".getBytes();

        // when
        TestObject testObject = JsonUtil.fromBytes(bytes, TestObject.class);

        // then
        assertNotNull(testObject);
        assertEquals("test", testObject.getName());
        assertEquals(123, testObject.getValue());
    }

    @Test
    void shouldThrowExceptionWhenFromBytesFailsWithInvalidJson() {
        // given
        byte[] invalidBytes = "{invalid json}".getBytes();

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> JsonUtil.fromBytes(invalidBytes, TestObject.class));

        // then
        assertEquals("Failed to convert JSON to object", exception.getMessage());
    }

    private static class TestObject {
        private String name;
        private int value;

        public TestObject() {
        }

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
