package com.gumeinteligenciacomercial.orcaja.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailUseCase {

    private final JavaMailSender mailSender;

    public void enviarCodigoVerificacao(String email, String codigo) {


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Seu código de verificação");
        message.setText("Seu código de verificação é: " + codigo);
        mailSender.send(message);
    }
}
