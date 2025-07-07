package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoAlteracaoSenha;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CodigoAlteracaoSenhaUseCase {

    private final RedisTemplate<String, String> redisTemplate;
    private final long TTL_MINUTES = 30;

    public void adicionarAoCache(String idUsuario, String codigoAlteracaoSenha) {
        redisTemplate.opsForValue().set(idUsuario, codigoAlteracaoSenha, TTL_MINUTES, TimeUnit.MINUTES);
    }


    public boolean validaCodigoAlteracaoSenha(String idUsuario, String codigo) {
        String codigoSalvo = redisTemplate.opsForValue().get(idUsuario);

        if(codigoSalvo != null && codigoSalvo.equals(codigo)) {
            return true;
        } else {
            throw new CodigoInvalidoAlteracaoSenha();
        }
    }
}
