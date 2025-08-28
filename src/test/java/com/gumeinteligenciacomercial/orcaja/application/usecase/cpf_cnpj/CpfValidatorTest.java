package com.gumeinteligenciacomercial.orcaja.application.usecase.cpf_cnpj;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CpfValidatorTest {
    private CpfValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new CpfValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void nullDeveSerInvalido() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void vazioDeveSerInvalido() {
        assertFalse(validator.isValid("", context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567890",
            "123456789012",
            "abcdefghijk",
            "123.456.789-0"
    })
    void formatosInvalidosDevemRetornarFalse(String entrada) {
        assertFalse(validator.isValid(entrada, context),
                () -> "Esperava false para formato inválido: " + entrada);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000",
            "11111111111",
            "22222222222",
            "99999999999"
    })
    void todosDigitosIguaisDevemSerInvalidos(String repetido) {
        assertFalse(validator.isValid(repetido, context),
                () -> "Esperava false para repetição de dígitos: " + repetido);
    }

    @Test
    void checksumIncorretoDeveRetornarFalse() {
        String cpfInvalido = "52998224724";
        assertFalse(validator.isValid(cpfInvalido, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",
            "529.982.247-25"
    })
    void cpfsValidosDevemRetornarTrue(String cpfValido) {
        assertTrue(validator.isValid(cpfValido, context),
                () -> "Esperava true para CPF válido: " + cpfValido);
    }
}