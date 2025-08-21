package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SenhaUseCase {

    private final EmailUseCase emailUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final CodigoAlteracaoSenhaUseCase codigoAlteracaoSenhaUseCase;

    public void solicitarNovaSenha(String emailUsuario) {
        Usuario usuario = usuarioUseCase.consultarPorEmail(emailUsuario);

        String codigoAlteracaoSenha = UUID.randomUUID().toString();

        emailUseCase.enviarAlteracaoDeSenha(usuario.getEmail(), codigoAlteracaoSenha);
        codigoAlteracaoSenhaUseCase.adicionarAoCache(usuario.getId(), codigoAlteracaoSenha);
    }


}
