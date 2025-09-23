package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ErroEnviarParaIaException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.IaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.MessagePromptIaDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.OpenIaResponseDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.PromptDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.mapper.JsonMapper;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IaUseCase {

    private final PromptUseCase promptUseCase;
    private final IaGateway gateway;

    @Value("${app.id.prompt.ia.gerador-orcamento}")
    private final String ID_PROMPT_IA_GERADOR_ORCAMENTO;

    @Value("${app.id.prompt.ia.interpretador-prompt}")
    private final String ID_PROMPT_IA_INTERPRETADOR_PROMPT;

    public IaUseCase(
            PromptUseCase promptUseCase,
            IaGateway gateway,
            @Value("${app.id.prompt.ia.gerador-orcamento}") String ID_PROMPT_IA_GERADOR_ORCAMENTO,
            @Value("${app.id.prompt.ia.interpretador-prompt}") String ID_PROMPT_IA_INTERPRETADOR_PROMPT
    ) {
        this.promptUseCase = promptUseCase;
        this.gateway = gateway;
        this.ID_PROMPT_IA_GERADOR_ORCAMENTO = ID_PROMPT_IA_GERADOR_ORCAMENTO;
        this.ID_PROMPT_IA_INTERPRETADOR_PROMPT = ID_PROMPT_IA_INTERPRETADOR_PROMPT;
    }

    public Map<String, Object> gerarOrcamento(String conteudoOriginal) {
        log.info("Gerando orçamento com a IA. Conteudo: {}", conteudoOriginal);

        Map<String, Object> objetoFormatado = enviarParaIa(conteudoOriginal, 0, false);

        log.info("Orçamento gerado com sucesso. Orçamento: {}", objetoFormatado);

        return objetoFormatado;
    }

    private Map<String, Object> enviarParaIa(String conteudo, int limite, boolean iaSecundaria) {
        try {
            PromptDto prompt = this.promptBuilder(conteudo, ID_PROMPT_IA_GERADOR_ORCAMENTO);
            OpenIaResponseDto responseIa = gateway.enviarMensagem(prompt);
            return JsonMapper.parseJsonToMap(
                    responseIa.getChoices().getFirst().getMessage().getContent()
            );
        } catch (Exception ex) {
            if (limite < 3) {
                return enviarParaIa(conteudo, limite + 1, false);
            } else if (!iaSecundaria) {
                return this.enviarParaIaSecundaria(conteudo, limite);
            } else {
                throw new ErroEnviarParaIaException();
            }
        }
    }

    private Map<String, Object> enviarParaIaSecundaria(String conteudo, int limite) {
        PromptDto prompt = this.promptBuilder(conteudo, ID_PROMPT_IA_INTERPRETADOR_PROMPT);
        OpenIaResponseDto responseIa = gateway.enviarMensagem(prompt);
        return enviarParaIa(responseIa.getChoices().getFirst().getMessage().getContent(), limite, true);
    }

    private PromptDto promptBuilder(String conteudoOriginal, String idPrompt) {
        log.info("Criando prompt para IA. Conteúdo: {}", conteudoOriginal);

        Prompt prompt = promptUseCase.buscarPorIdAtivo(idPrompt);

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
