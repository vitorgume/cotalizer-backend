package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioGateway {
    Usuario salvar(Usuario usuario);

    Optional<Usuario> consultarPorId(UUID idUsuario);

    Optional<Usuario> consultarPorCpf(String cpf);

    void deletar(UUID idUsuario);
}
