package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.StatusUsuario;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private Usuario usuarioDomain;
    private UsuarioEntity usuarioEntity;

    @BeforeEach
    void setUp() {
        usuarioDomain = Usuario.builder()
                .id("id-teste")
                .nome("Nome teste")
                .email("Email teste")
                .telefone("554400000000")
                .senha("senha-teste-123")
                .status(StatusUsuario.ATIVO)
                .plano(Plano.builder().id("58e84e1b-b19f-4df0-bc72-a8209fbfaf1d").limite(5).build())
                .idCustomer("id-customer-teste")
                .idAssinatura("id-assinatura-teste")
                .urlLogo("url-logo-teste")
                .feedback(true)
                .onboarding(true)
                .build();


        usuarioEntity = UsuarioEntity.builder()
                .id("id-teste-2")
                .nome("Nome teste 2")
                .email("Email teste 2")
                .telefone("554400000001")
                .senha("senha-teste-312")
                .status(StatusUsuario.INATIVO)
                .plano(PlanoEntity.builder().id("58e84e1b-b19f-4df0-bc72-a8209fbfaf1d").limite(5).build())
                .idCustomer("id-customer-teste-2")
                .idAssinatura("id-assinatura-teste-2")
                .urlLogo("url-logo-teste-1")
                .feedback(false)
                .onboarding(false)
                .build();
    }

    @Test
    void deveRetornarEntity() {
        UsuarioEntity usuarioTeste = UsuarioMapper.paraEntity(usuarioDomain);

        Assertions.assertEquals(usuarioDomain.getId(), usuarioTeste.getId());
        Assertions.assertEquals(usuarioDomain.getNome(), usuarioTeste.getNome());
        Assertions.assertEquals(usuarioDomain.getEmail(), usuarioTeste.getEmail());
        Assertions.assertEquals(usuarioDomain.getTelefone(), usuarioTeste.getTelefone());
        Assertions.assertEquals(usuarioDomain.getSenha(), usuarioTeste.getSenha());
        Assertions.assertEquals(usuarioDomain.getStatus(), usuarioTeste.getStatus());
        Assertions.assertEquals(usuarioDomain.getIdCustomer(), usuarioTeste.getIdCustomer());
        Assertions.assertEquals(usuarioDomain.getIdAssinatura(), usuarioTeste.getIdAssinatura());
        Assertions.assertEquals(usuarioDomain.getUrlLogo(), usuarioTeste.getUrlLogo());
        Assertions.assertTrue(usuarioTeste.getFeedback());
        Assertions.assertTrue(usuarioTeste.getOnboarding());
    }

    @Test
    void deveRetornarDomain() {
        Usuario usuarioTeste = UsuarioMapper.paraDomain(usuarioEntity);

        Assertions.assertEquals(usuarioEntity.getId(), usuarioTeste.getId());
        Assertions.assertEquals(usuarioEntity.getNome(), usuarioTeste.getNome());
        Assertions.assertEquals(usuarioEntity.getEmail(), usuarioTeste.getEmail());
        Assertions.assertEquals(usuarioEntity.getTelefone(), usuarioTeste.getTelefone());
        Assertions.assertEquals(usuarioEntity.getSenha(), usuarioTeste.getSenha());
        Assertions.assertEquals(usuarioEntity.getStatus(), usuarioTeste.getStatus());
        Assertions.assertEquals(usuarioEntity.getIdCustomer(), usuarioTeste.getIdCustomer());
        Assertions.assertEquals(usuarioEntity.getIdAssinatura(), usuarioTeste.getIdAssinatura());
        Assertions.assertEquals(usuarioEntity.getUrlLogo(), usuarioTeste.getUrlLogo());
        Assertions.assertFalse(usuarioTeste.getFeedback());
        Assertions.assertFalse(usuarioTeste.getOnboarding());
    }
}