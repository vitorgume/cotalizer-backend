package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Avaliacao;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailUseCase {

    private final JavaMailSender mailSender;

    @Value("${cotalizer.email.avaliacao}")
    private final String EMAIL_AVALIACAO;

    @Value("${cotalizer.url.alteracao-email}")
    private final String URL_ALTERACAO_EMAIL;

    public EmailUseCase(
            JavaMailSender mailSender,
            @Value("${cotalizer.email.avaliacao}") String EMAIL_AVALIACAO,
            @Value("${cotalizer.url.alteracao-email}") String URL_ALTERACAO_EMAIL
    ) {
        this.mailSender = mailSender;
        this.EMAIL_AVALIACAO = EMAIL_AVALIACAO;
        this.URL_ALTERACAO_EMAIL = URL_ALTERACAO_EMAIL;
    }

    public void enviarCodigoVerificacao(String email, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Seu código de verificação");
        message.setText("Seu código de verificação é: " + codigo);
        mailSender.send(message);
    }

    public void enviarAlteracaoDeSenha(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Alteração de senha no Cotalizer");
        message.setText("Acesse o seguinte link para alterar sua senha: " + URL_ALTERACAO_EMAIL + token);
        mailSender.send(message);
    }

    public void enviarAvaliacao(Avaliacao novaAvaliacao, Usuario usuario) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(EMAIL_AVALIACAO);
        message.setSubject("Nova avaliação");
        message.setText("Usuario: " + usuario.getNome()
                + "\nNota: " + novaAvaliacao.getNota()
                + "\nMotivo: " + novaAvaliacao.getMotivoNota()
                + "\nMelhoria: " + novaAvaliacao.getSugestaoMelhoria()
        );
        mailSender.send(message);
    }
}
