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
        redisTemplate.opsForValue().set("codigo:" + codigoAlteracaoSenha, idUsuario, TTL_MINUTES, TimeUnit.MINUTES);
    }


    public String validaCodigoAlteracaoSenha(String codigo) {
        String idUsuario = redisTemplate.opsForValue().get("codigo:" + codigo);

        if(idUsuario != null) {
            return idUsuario;
        } else {
            throw new CodigoInvalidoAlteracaoSenha();
        }
    }
}
