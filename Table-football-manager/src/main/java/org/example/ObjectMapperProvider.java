package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProvider {

    static ObjectMapper objectMapper;

    private ObjectMapperProvider() {
    }

    public static ObjectMapper getInstance() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }
}
