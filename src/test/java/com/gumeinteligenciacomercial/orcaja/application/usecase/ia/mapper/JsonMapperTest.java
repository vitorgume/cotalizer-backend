package com.gumeinteligenciacomercial.orcaja.application.usecase.ia.mapper;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ConversaoJsonException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonMapperTest {

    @Test
    void parseJsonToMapDeveConverterJsonSimplesParaMap() {
        String json = """
            {
              "nome": "Maria",
              "idade": 30,
              "ativo": true,
              "salario": 1234.56
            }
            """;

        Map<String, Object> result = JsonMapper.parseJsonToMap(json);

        assertEquals(4, result.size());
        assertEquals("Maria", result.get("nome"));
        assertEquals(30, result.get("idade"));
        assertEquals(true, result.get("ativo"));
        assertEquals(1234.56, ((Number) result.get("salario")).doubleValue(), 0.0001);
    }

    @Test
    void parseJsonToMapDeveConverterJsonAninhadoParaMap() {
        String json = """
            {
              "usuario": {
                "id": 1,
                "email": "joao@exemplo.com"
              },
              "roles": ["ADMIN", "USER"]
            }
            """;

        Map<String, Object> result = JsonMapper.parseJsonToMap(json);

        assertTrue(result.containsKey("usuario"));
        assertTrue(result.get("usuario") instanceof Map);
        Map<String, Object> usuario = (Map<String, Object>) result.get("usuario");
        assertEquals(1, usuario.get("id"));
        assertEquals("joao@exemplo.com", usuario.get("email"));

        assertTrue(result.containsKey("roles"));
        assertTrue(result.get("roles") instanceof java.util.List);
        java.util.List<String> roles = (java.util.List<String>) result.get("roles");
        assertIterableEquals(java.util.List.of("ADMIN", "USER"), roles);
    }

    @Test
    void parseJsonToMapComJsonInvalidoDeveLancarConversaoJsonException() {
        String invalidJson = "{ nome: 'Sem aspas duplas' }"; // sintaxe inválida

        ConversaoJsonException ex = assertThrows(
                ConversaoJsonException.class,
                () -> JsonMapper.parseJsonToMap(invalidJson)
        );
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof com.fasterxml.jackson.core.JsonParseException);
    }
}