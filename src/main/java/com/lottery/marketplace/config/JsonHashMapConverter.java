package com.lottery.marketplace.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class JsonHashMapConverter implements AttributeConverter<Map<String, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> stringMap) {
        try {
            return objectMapper.writeValueAsString(stringMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Map<String, String> to JSON", e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        JavaType type = objectMapper.getTypeFactory().constructParametricType(HashMap.class, String.class, String.class);
        try {
            return objectMapper.readValue(s, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to Map<String, String>", e);
        }
    }
}