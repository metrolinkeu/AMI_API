package com.metrolink.ami_api.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        try {
            return jsonNode != null ? objectMapper.writeValueAsString(jsonNode) : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JsonNode to String", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String jsonString) {
        try {
            return jsonString != null ? objectMapper.readTree(jsonString) : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting String to JsonNode", e);
        }
    }

    // Implementación de equals que compara los JsonNode como cadenas JSON
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JsonNodeConverter that = (JsonNodeConverter) o;
        return this.convertToDatabaseColumn(that.convertToEntityAttribute((String) o))
                .equals(this.convertToDatabaseColumn((JsonNode) o));
    }

    @Override
    public int hashCode() {
        return this.hashCode();
    }
}
