package com.gumeinteligenciacomercial.orcaja.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailUseCaseTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailUseCase emailUseCase;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @Test
    void enviarCodigoVerificacaoDeveConfigurarEMandarEmailCorreto() {
        String email = "user@example.com";
        String codigo = "ABC123";

        emailUseCase.enviarCodigoVerificacao(email, codigo);

        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sent = messageCaptor.getValue();

        assertNotNull(sent, "A mensagem não deve ser nula");
        assertArrayEquals(new String[]{email}, sent.getTo(), "Destinatário deve ser o e-mail informado");
        assertEquals("Seu código de verificação", sent.getSubject(), "Assunto incorreto");
        assertEquals("Seu código de verificação é: " + codigo, sent.getText(), "Texto do e-mail incorreto");
    }

    @Test
    void enviarAlteracaoDeSenhaDeveConfigurarEMandarEmailComLink() {
        String email = "user2@example.com";
        String token = "tok123";

        emailUseCase.enviarAlteracaoDeSenha(email, token);

        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sent = messageCaptor.getValue();

        assertArrayEquals(new String[]{email}, sent.getTo(), "Destinatário deve ser o e-mail informado");
        assertEquals("Alteração de senha no Cotalizer", sent.getSubject(), "Assunto incorreto");

        String expectedLink = "http://localhost:5173/usuario/alterar/senha/" + token;
        assertEquals("Acesse o seguinte link para alterar sua senha: " + expectedLink,
                sent.getText(), "Texto do e-mail com link incorreto");
    }
}