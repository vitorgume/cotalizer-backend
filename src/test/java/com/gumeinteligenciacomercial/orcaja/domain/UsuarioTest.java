package com.gumeinteligenciacomercial.orcaja.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void deveAlterarDadosDoUsuario() {
        Usuario novosDados = Usuario.builder()
                .nome("Novo nome")
                .email("Novo email")
                .telefone("0000000000001")
                .status(StatusUsuario.INATIVO)
                .cpf("0000000000001")
                .cnpj("3333333333331")
                .plano(Plano.PLUS)
                .feedback(true)
                .build();

        Usuario usuario = Usuario.builder()
                .nome("nome")
                .email("email")
                .telefone("0000000000000")
                .status(StatusUsuario.ATIVO)
                .cpf("0000000000000")
                .cnpj("3333333333330")
                .plano(Plano.GRATIS)
                .feedback(false)
                .build();

        usuario.setDados(novosDados);

        Assertions.assertEquals(usuario.getNome(), novosDados.getNome());
        Assertions.assertEquals(usuario.getEmail(), novosDados.getEmail());
        Assertions.assertEquals(usuario.getTelefone(), novosDados.getTelefone());
        Assertions.assertEquals(usuario.getStatus(), novosDados.getStatus());
        Assertions.assertEquals(usuario.getCpf(), novosDados.getCpf());
        Assertions.assertEquals(usuario.getCnpj(), novosDados.getCnpj());
        Assertions.assertEquals(usuario.getPlano(), novosDados.getPlano());
        Assertions.assertTrue(usuario.getFeedback());
    }
}