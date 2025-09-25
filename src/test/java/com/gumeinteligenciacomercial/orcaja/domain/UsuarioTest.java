package com.gumeinteligenciacomercial.orcaja.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void deveAlterarDadosDoUsuario() {
        Usuario novosDados = Usuario.builder()
                .nome("Novo nome")
                .email("Novo email")
                .telefone("0000000000001")
                .status(StatusUsuario.INATIVO)
                .plano(Plano.builder().id("58e84e1b-b19f-4df0-bc72-a8209fbfaf1d").build())
                .feedback(true)
                .onboarding(true)
                .build();

        Usuario usuario = Usuario.builder()
                .nome("nome")
                .email("email")
                .telefone("0000000000000")
                .status(StatusUsuario.ATIVO)
                .plano(Plano.builder().id("58e84e1b-b19f-4df0-bc72-a8209fbfaf1d").build())
                .feedback(false)
                .onboarding(false)
                .build();

        usuario.setDados(novosDados);

        Assertions.assertEquals(usuario.getNome(), novosDados.getNome());
        Assertions.assertEquals(usuario.getEmail(), novosDados.getEmail());
        Assertions.assertEquals(usuario.getTelefone(), novosDados.getTelefone());
        Assertions.assertEquals(usuario.getStatus(), novosDados.getStatus());
        Assertions.assertEquals(usuario.getPlano(), novosDados.getPlano());
        Assertions.assertTrue(usuario.getFeedback());
        Assertions.assertTrue(usuario.getOnboarding());
    }
}