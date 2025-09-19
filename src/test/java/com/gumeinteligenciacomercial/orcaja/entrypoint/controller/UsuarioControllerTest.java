package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.usecase.CodigoAlteracaoSenhaUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.CodigoValidacaoUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.CriptografiaUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.EmailUseCase;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.StatusUsuario;
import com.gumeinteligenciacomercial.orcaja.domain.TipoCadastro;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.AlteracaoSenhaDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=5a6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private CriptografiaUseCase criptografiaUseCase;

    @MockitoBean
    private EmailUseCase emailUseCase;

    @MockitoBean
    private CodigoValidacaoUseCase codigoValidacaoUseCase;

    @MockitoBean
    private CodigoAlteracaoSenhaUseCase codigoAlteracaoSenhaUseCase;

    @Captor
    private ArgumentCaptor<UsuarioEntity> usuarioCaptor;

    private UsuarioDto usuarioDto;
    private UsuarioEntity usuario;

    @BeforeEach
    void setUp() {
        usuarioDto = UsuarioDto.builder()
                .id("id-teste-2")
                .nome("Nome teste 2")
                .email("Email teste 2")
                .telefone("554400000002")
                .senha("senha-teste-124")
                .status(StatusUsuario.ATIVO)
                .plano(Plano.GRATIS)
                .idCustomer("id-customer-teste-2")
                .idAssinatura("id-assinatura-teste-2")
                .urlLogo("url-logo-teste 2")
                .tipoCadastro(TipoCadastro.TRADICIONAL)
                .build();

        usuario = UsuarioEntity.builder()
                .id("id-teste-2")
                .nome("Nome teste 2")
                .email("Email teste 2")
                .telefone("554400000001")
                .senha("senha-teste-312")
                .status(StatusUsuario.INATIVO)
                .plano(Plano.PLUS)
                .idCustomer("id-customer-teste-2")
                .idAssinatura("id-assinatura-teste-2")
                .urlLogo("url-logo-teste-1")
                .tipoCadastro(TipoCadastro.TRADICIONAL)
                .build();
    }

    @Test
    void deveCadastrarComSucesso() throws Exception {
        usuarioDto.setId(null);

        Mockito.when(criptografiaUseCase.criptografar(anyString())).thenReturn("Senhacriptografada");
        Mockito.when(codigoValidacaoUseCase.gerarCodigo(anyString())).thenReturn("codigovalidacaoemail");
        Mockito.doNothing().when(emailUseCase).enviarCodigoVerificacao(anyString(), anyString());
        Mockito.when(usuarioRepository.save(usuarioCaptor.capture())).thenReturn(usuario);

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/usuarios/cadastro/" + usuario.getId()))
                .andExpect(jsonPath("$.dado.nome").value(usuario.getNome()));

        Assertions.assertEquals(usuarioDto.getNome(), usuarioCaptor.getValue().getNome());
        Assertions.assertEquals(StatusUsuario.PENDENTE_VALIDACAO_EMAIL, usuarioCaptor.getValue().getStatus());
        Assertions.assertEquals(Plano.GRATIS, usuarioCaptor.getValue().getPlano());

        Mockito.verify(criptografiaUseCase).criptografar(anyString());
        Mockito.verify(usuarioRepository).save(any());
    }

    @Test
    void deveConsultarPorIdComSucesso() throws Exception {
        Mockito.when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/usuarios/" + usuarioDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(usuarioDto.getId()));

        Mockito.verify(usuarioRepository).findById(anyString());
    }

    @Test
    void deveReenviarCodigoComSucesso() throws Exception {

        Mockito.when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        Mockito.when(codigoValidacaoUseCase.gerarCodigo(anyString())).thenReturn("Novo codigo teste");
        Mockito.doNothing().when(emailUseCase).enviarCodigoVerificacao(anyString(), anyString());

        mockMvc.perform(post("/usuarios/reenvio/codigo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isNoContent());

        Mockito.verify(usuarioRepository).findByEmail(anyString());
    }

    @Test
    void deveInativarComSucesso() throws Exception {

        Mockito.when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.save(usuarioCaptor.capture())).thenReturn(usuario);

        mockMvc.perform(put("/usuarios/inativar/" + usuarioDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(usuarioDto.getId()));

        Assertions.assertEquals(StatusUsuario.INATIVO, usuarioCaptor.getValue().getStatus());

        Mockito.verify(usuarioRepository).findById(anyString());
        Mockito.verify(usuarioRepository).save(any());
    }

    @Test
    void deveAlterarComSucesso() throws Exception {

        Mockito.when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        Mockito.when(codigoValidacaoUseCase.gerarCodigo(anyString())).thenReturn("Novo código teste");
        Mockito.doNothing().when(emailUseCase).enviarCodigoVerificacao(anyString(), anyString());
        Mockito.when(usuarioRepository.save(usuarioCaptor.capture())).thenReturn(usuario);

        mockMvc.perform(put("/usuarios/" + usuarioDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(usuarioDto.getId()));

        Mockito.verify(usuarioRepository).findById(anyString());
        Mockito.verify(usuarioRepository).save(any());
    }

    @Test
    void deveAlterarSenha() throws Exception {
        AlteracaoSenhaDto alteracaoSenhaDto = AlteracaoSenhaDto.builder()
                .novaSenha("Nova senha teste")
                .codigo("Codigo teste nova senha")
                .build();

        Mockito.when(codigoAlteracaoSenhaUseCase.validaCodigoAlteracaoSenha(anyString())).thenReturn(usuario.getId());
        Mockito.when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        Mockito.when(criptografiaUseCase.criptografar(anyString())).thenReturn("Nova senha criptografada");
        Mockito.when(usuarioRepository.save(usuarioCaptor.capture())).thenReturn(usuario);

        mockMvc.perform(patch("/usuarios/alterar/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alteracaoSenhaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value(usuarioDto.getId()));

        Assertions.assertNotEquals(usuario.getSenha(), usuarioCaptor.getValue().getSenha());

        Mockito.verify(codigoAlteracaoSenhaUseCase).validaCodigoAlteracaoSenha(anyString());
        Mockito.verify(usuarioRepository).findById(anyString());
        Mockito.verify(criptografiaUseCase).criptografar(anyString());
        Mockito.verify(usuarioRepository).save(any());

    }
}