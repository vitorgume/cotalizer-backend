package com.gumeinteligenciacomercial.orcaja.application.usecase.ia.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.ConversaoJsonException;

import java.util.Map;

public class JsonMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> parseJsonToMap(String jsonContent) {
        try {
            return objectMapper.readValue(jsonContent, Map.class);
        } catch (Exception e) {
            throw new ConversaoJsonException(e);
        }
    }
}
