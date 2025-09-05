package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ErroEnviarParaIaException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.IaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.MessagePromptIaDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.OpenIaResponseDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.PromptDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.mapper.JsonMapper;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IaUseCase {

    private final PromptUseCase promptUseCase;
    private final IaGateway gateway;

    public Map<String, Object> gerarOrcamento(String conteudoOriginal) {
        log.info("Gerando orçamento com a IA. Conteudo: {}", conteudoOriginal);

        Map<String, Object> objetoFormatado = enviarParaIa(conteudoOriginal, 0);

        log.info("Orçamento gerado com sucesso. Orçamento: {}", objetoFormatado);

        return objetoFormatado;
    }

    private Map<String, Object> enviarParaIa(String conteudo, int limite) {
        try {
            PromptDto prompt = this.promptBuilder(conteudo);
            OpenIaResponseDto responseIa = gateway.enviarMensagem(prompt);
            return JsonMapper.parseJsonToMap(
                    responseIa.getChoices().getFirst().getMessage().getContent()
            );
        } catch (Exception ex) {
            if (limite < 3) {
                return enviarParaIa(conteudo, limite + 1);
            } else {
                throw new ErroEnviarParaIaException();
            }
        }
    }

    private PromptDto promptBuilder(String conteudoOriginal) {
        log.info("Criando prompt para IA. Conteúdo: {}", conteudoOriginal);

        Prompt prompt = promptUseCase.buscarAtivo();

        MessagePromptIaDto mensagemSystem = MessagePromptIaDto.builder()
                .role("system")
                .content(prompt.getConteudo())
                .build();

        MessagePromptIaDto mensagemUser = MessagePromptIaDto.builder()
                .role("user")
                .content(conteudoOriginal)
                .build();

        PromptDto promptUser = PromptDto.builder()
                .model(prompt.getModelIa())
                .messages(List.of(mensagemSystem, mensagemUser))
                .build();

        log.info("Prompt criado com sucesso.");

        return promptUser;
    }

}
