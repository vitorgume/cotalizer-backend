package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoValidacaoEmailException;
import com.gumeinteligenciacomercial.orcaja.domain.VerificacaoEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CodigoValidacaoUseCase {

    private final RedisTemplate<String, String> redisTemplate;
    private final long TTL_MINUTES = 10;

    public String gerarCodigo(String email) {
        String codigo = gerarCodigoVerificacao();

        redisTemplate.opsForValue().set(email, codigo, TTL_MINUTES, TimeUnit.MINUTES);

        return codigo;
    }

    public boolean validar(String email, String codigoInformado) {
        String codigoSalvo = redisTemplate.opsForValue().get(email);

        if(codigoSalvo != null && codigoSalvo.equals(codigoInformado)) {
            return true;
        } else {
            throw new CodigoInvalidoValidacaoEmailException();
        }
    }

    private String gerarCodigoVerificacao() {
        return String.valueOf(new Random().nextInt(900_000) + 100_000);
    }

}
