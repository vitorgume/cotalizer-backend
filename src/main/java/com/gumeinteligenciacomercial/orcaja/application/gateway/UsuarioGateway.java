package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioGateway {
    Usuario salvar(Usuario usuario);

    Optional<Usuario> consultarPorId(String idUsuario);

    Optional<Usuario> consultarPorEmail(String email);

    List<Usuario> listar();
}
