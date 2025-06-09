package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioJaCadastradoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.UsuarioGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioUseCase {

    private final UsuarioGateway gateway;

    public Usuario cadastrar(Usuario usuario) {
        log.info("Cadastrando novo usuário. Usuário: {}", usuario);
        Optional<Usuario> usuarioExistente = this.consultarPorCpf(usuario.getCpf());

        usuarioExistente.ifPresent(us -> {
            throw new UsuarioJaCadastradoException();
        });

        //Validar CPF/CNPJ para versão 1.0.0

        Usuario usuarioSalvo = gateway.salvar(usuario);

        log.info("Usuário salvo com sucesso. Usuário: {}", usuarioSalvo);

        return usuarioSalvo;
    }


    public Usuario consultarPorId(UUID idUsuario) {
        log.info("Consultando usuário pelo seu id. Id do usuário: {}", idUsuario);
        Optional<Usuario> usuario = gateway.consultarPorId(idUsuario);

        if(usuario.isEmpty()) {
            throw new UsuarioNaoEncontradoException();
        }

        log.info("Usuário consultado com sucesso pelo seu id. Usuário: {}", usuario.get());

        return usuario.get();
    }

    private Optional<Usuario> consultarPorCpf(String cpf) {
        return gateway.consultarPorCpf(cpf);
    }

    public void deletar(UUID idUsuario) {
        this.consultarPorId(idUsuario);
        gateway.deletar(idUsuario);
    }
}
