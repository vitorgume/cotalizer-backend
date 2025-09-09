package com.gumeinteligenciacomercial.orcaja.application.usecase.cpf_cnpj;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CNPJValidatorTest {

    private CNPJValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new CNPJValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void nullDeveSerInvalido() {
        assertFalse(validator.isValid(null, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "123",
            "123456789012345",
            "ABCDEFGHIJKLMN",
            "12345678AB9012"
    })
    void formatosInvalidosDevemRetornarFalse(String entrada) {
        assertFalse(validator.isValid(entrada, context),
                () -> "Esperava false para: " + entrada);
    }

    @Test
    void checksumIncorretoDeveRetornarFalse() {
        String cnpjInvalido = "04252011000111";
        assertFalse(validator.isValid(cnpjInvalido, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "04252011000110",
            "11444777000161"
    })
    void cpjnsValidosDevemRetornarTrue(String cnpjValido) {
        assertTrue(validator.isValid(cnpjValido, context),
                () -> "Esperava true para: " + cnpjValido);
    }

    @Test
    void cnpjValido_quandoPrimeiroDigitoVerificadorEhZero_retornaTrue() {
        assertTrue(validator.isValid("07150842375903", context));
    }

    @Test
    void cnpjValido_quandoSegundoDigitoVerificadorEhZero_retornaTrue() {
        assertTrue(validator.isValid("82421948924190", context));
    }

}