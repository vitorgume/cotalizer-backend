package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.StatusUsuario;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private Usuario usuarioDomain;
    private UsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        usuarioDomain = Usuario.builder()
                .id("id-teste")
                .nome("Nome teste")
                .email("Email teste")
                .telefone("554400000000")
                .cpf("12345678987")
                .cnpj("")
                .senha("senha-teste-123")
                .status(StatusUsuario.ATIVO)
                .plano(Plano.GRATIS)
                .idCustomer("id-customer-teste")
                .idAssinatura("id-assinatura-teste")
                .urlLogo("url-logo-teste")
                .build();

        usuarioDto = UsuarioDto.builder()
                .id("id-teste 2")
                .nome("Nome teste 2")
                .email("Email teste 2")
                .telefone("554400000002")
                .cpf("12345678988")
                .cnpj("")
                .senha("senha-teste-124")
                .status(StatusUsuario.ATIVO)
                .plano(Plano.GRATIS)
                .idCustomer("id-customer-teste-2")
                .idAssinatura("id-assinatura-teste-2")
                .urlLogo("url-logo-teste 2")
                .build();
    }

    @Test
    void deveRetornarDto() {
        UsuarioDto usuarioTeste = UsuarioMapper.paraDto(usuarioDomain);

        Assertions.assertEquals(usuarioDomain.getId(), usuarioTeste.getId());
        Assertions.assertEquals(usuarioDomain.getNome(), usuarioTeste.getNome());
        Assertions.assertEquals(usuarioDomain.getEmail(), usuarioTeste.getEmail());
        Assertions.assertEquals(usuarioDomain.getTelefone(), usuarioTeste.getTelefone());
        Assertions.assertEquals(usuarioDomain.getCpf(), usuarioTeste.getCpf());
        Assertions.assertEquals(usuarioDomain.getCnpj(), usuarioTeste.getCnpj());
        Assertions.assertNull(usuarioTeste.getSenha());
        Assertions.assertEquals(usuarioDomain.getStatus(), usuarioTeste.getStatus());
        Assertions.assertEquals(usuarioDomain.getIdCustomer(), usuarioTeste.getIdCustomer());
        Assertions.assertEquals(usuarioDomain.getIdAssinatura(), usuarioTeste.getIdAssinatura());
        Assertions.assertEquals(usuarioDomain.getUrlLogo(), usuarioTeste.getUrlLogo());
    }

    @Test
    void deveRetornarDomain() {
        Usuario usuarioTeste = UsuarioMapper.paraDomain(usuarioDto);

        Assertions.assertEquals(usuarioDto.getId(), usuarioTeste.getId());
        Assertions.assertEquals(usuarioDto.getNome(), usuarioTeste.getNome());
        Assertions.assertEquals(usuarioDto.getEmail(), usuarioTeste.getEmail());
        Assertions.assertEquals(usuarioDto.getTelefone(), usuarioTeste.getTelefone());
        Assertions.assertEquals(usuarioDto.getCpf(), usuarioTeste.getCpf());
        Assertions.assertEquals(usuarioDto.getCnpj(), usuarioTeste.getCnpj());
        Assertions.assertEquals(usuarioDto.getSenha(), usuarioTeste.getSenha());
        Assertions.assertEquals(usuarioDto.getStatus(), usuarioTeste.getStatus());
        Assertions.assertEquals(usuarioDto.getIdCustomer(), usuarioTeste.getIdCustomer());
        Assertions.assertEquals(usuarioDto.getIdAssinatura(), usuarioTeste.getIdAssinatura());
        Assertions.assertEquals(usuarioDto.getUrlLogo(), usuarioTeste.getUrlLogo());
    }
}