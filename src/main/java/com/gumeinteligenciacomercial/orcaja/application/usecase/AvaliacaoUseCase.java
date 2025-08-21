package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Avaliacao;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvaliacaoUseCase {

    private final EmailUseCase emailUseCase;
    private final UsuarioUseCase usuarioUseCase;

    public Avaliacao enviar(Avaliacao novaAvaliacao) {
        Usuario usuario = usuarioUseCase.consultarPorId(novaAvaliacao.getIdUsuario());

        emailUseCase.enviarAvaliacao(novaAvaliacao, usuario);

        usuario.setFeedback(true);
        usuarioUseCase.alterar(usuario.getId(), usuario);

        return novaAvaliacao;
    }
}
