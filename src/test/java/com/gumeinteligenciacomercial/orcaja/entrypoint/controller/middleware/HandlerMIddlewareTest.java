package com.gumeinteligenciacomercial.orcaja.entrypoint.controller.middleware;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.*;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=SECRET_KEY_TEST"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import({HandlerMIddleware.class, HandlerMIddlewareTest.TestController.class})
class HandlerMIddlewareTest {

    @Autowired
    private MockMvc mockMvc;

    @Controller
    static class TestController {
        @GetMapping("/test/runtime")
        public void runtime() {
            throw new RuntimeException("runtime error");
        }
        @GetMapping("/test/arquivo")
        public void arquivo() {
            throw new ArquivoException("arquivo failure", null);
        }
        @GetMapping("/test/conversao")
        public void conversao() {
            throw new ConversaoJsonException(null);
        }
        @GetMapping("/test/credenciais")
        public void credenciais() {
            throw new CredenciasIncorretasException();
        }
        @GetMapping("/test/orcamento/{id}")
        public void orcamento(@PathVariable String id) {
            throw new OrcamentoNaoEncontradoException();
        }
        @GetMapping("/test/usuario-ja")
        public void usuarioJa() {
            throw new UsuarioJaCadastradoException();
        }
        @GetMapping("/test/usuario-nao/{id}")
        public void usuarioNao(@PathVariable String id) {
            throw new UsuarioNaoEncontradoException();
        }
        @GetMapping("/test/cod-alt")
        public void codAlt() {
            throw new CodigoInvalidoAlteracaoSenha();
        }
        @GetMapping("/test/cod-val")
        public void codVal() {
            throw new CodigoInvalidoValidacaoEmailException();
        }
        @GetMapping("/test/limite")
        public void limite() {
            throw new LimiteOrcamentosPlanoException();
        }
        @GetMapping("/test/data-provider")
        public void dataProvider() {
            throw new DataProviderException("data provider fail", null);
        }
    }

    @Test
    void runtimeExceptionGera500() throws Exception {
        mockMvc.perform(get("/test/runtime"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("runtime error"));
    }

    @Test
    void arquivoExceptionGera500() throws Exception {
        mockMvc.perform(get("/test/arquivo"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("arquivo failure"));
    }

    @Test
    void conversaoJsonExceptionGera500() throws Exception {
        mockMvc.perform(get("/test/conversao"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("Erro ao converter texto para json"));
    }

    @Test
    void credenciaisIncorretasGera400() throws Exception {
        mockMvc.perform(get("/test/credenciais"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("Credências incorretas."));
    }

    @Test
    void orcamentoNaoEncontradoGera404() throws Exception {
        mockMvc.perform(get("/test/orcamento/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("Orçamento não encontrado pelo seu id."));
    }

    @Test
    void usuarioJaCadastradoGera400() throws Exception {
        mockMvc.perform(get("/test/usuario-ja"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("Usuário já cadastrado com esse cpf."));
    }

    @Test
    void usuarioNaoEncontradoGera404() throws Exception {
        mockMvc.perform(get("/test/usuario-nao/abc"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("Usuário não encontrado."));
    }

    @Test
    void codigoAlteracaoSenhaInvalidoGera400() throws Exception {
        mockMvc.perform(get("/test/cod-alt"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("Código para alteração de senha inválido"));
    }

    @Test
    void codigoValidacaoEmailInvalidoGera400() throws Exception {
        mockMvc.perform(get("/test/cod-val"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("Código para validação de email inválido."));
    }

    @Test
    void limiteOrcamentosPlanoGera400() throws Exception {
        mockMvc.perform(get("/test/limite"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("Limite de orçamento atingindo para o plano do usuário."));
    }

    @Test
    void dataProviderExceptionGera500() throws Exception {
        mockMvc.perform(get("/test/data-provider"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro.mensagens[0]").value("data provider fail"));
    }
}