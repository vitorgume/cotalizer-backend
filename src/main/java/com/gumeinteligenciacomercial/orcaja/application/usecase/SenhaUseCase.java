package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoAlteracaoSenha;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoValidacaoEmailException;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
import io.netty.util.Timeout;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SenhaUseCase {

    private final EmailUseCase emailUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final CodigoAlteracaoSenhaUseCase codigoAlteracaoSenhaUseCase;

    public void solicitarNovaSenha(String idUsuario) {
        Usuario usuario = usuarioUseCase.consultarPorId(idUsuario);

        String codigoAlteracaoSenha = UUID.randomUUID().toString();

        emailUseCase.enviarAlteracaoDeSenha(usuario.getEmail(), codigoAlteracaoSenha);
        codigoAlteracaoSenhaUseCase.adicionarAoCache(usuario.getId(), codigoAlteracaoSenha);
    }


}
