package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.CriptografiaGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriptografiaUseCaseTest {

    @Mock
    private CriptografiaGateway criptografiaGateway;

    @InjectMocks
    private CriptografiaUseCase criptografiaUseCase;

    @Test
    void criptografarDeveDelegarParaGatewayERetornarResultado() {
        String senha = "minhaSenha";
        String hashEsperado = "$2a$10$abcdefg1234567890";
        when(criptografiaGateway.criptografar(senha)).thenReturn(hashEsperado);

        String resultado = criptografiaUseCase.criptografar(senha);

        assertEquals(hashEsperado, resultado, "Deve retornar o valor fornecido pelo gateway");
        verify(criptografiaGateway, times(1)).criptografar(senha);
        verifyNoMoreInteractions(criptografiaGateway);
    }

    @Test
    void validaSenhaQuandoGatewayValidaSenhaRetornaTrueDeveRetornarTrue() {
        String senha = "senhaUsuario";
        String senhaRepresentante = "$2a$10$abcdefg1234567890";
        when(criptografiaGateway.validarSenha(senha, senhaRepresentante)).thenReturn(true);

        boolean valido = criptografiaUseCase.validaSenha(senha, senhaRepresentante);

        assertTrue(valido, "Deve retornar true quando o gateway validar a senha");
        verify(criptografiaGateway, times(1)).validarSenha(senha, senhaRepresentante);
        verifyNoMoreInteractions(criptografiaGateway);
    }

    @Test
    void validaSenhaQuandoGatewayValidaSenhaRetornaFalseDeveRetornarFalse() {
        String senha = "senhaUsuario";
        String senhaRepresentante = "$2a$10$abcdefg1234567890";
        when(criptografiaGateway.validarSenha(senha, senhaRepresentante)).thenReturn(false);

        boolean valido = criptografiaUseCase.validaSenha(senha, senhaRepresentante);

        assertFalse(valido, "Deve retornar false quando o gateway não validar a senha");
        verify(criptografiaGateway, times(1)).validarSenha(senha, senhaRepresentante);
        verifyNoMoreInteractions(criptografiaGateway);
    }
}