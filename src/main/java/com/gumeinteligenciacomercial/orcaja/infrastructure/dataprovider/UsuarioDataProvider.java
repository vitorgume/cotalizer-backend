package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.UsuarioGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.UsuarioMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioDataProvider implements UsuarioGateway {

    private final UsuarioRepository repository;
    private final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar usuário.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar usuário pelo seu id.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_CPF = "Erro ao consultar usuário pelo seu cpf.";
    private final String MENSAGEM_ERRO_DELETAR_PELO_ID = "Erro ao deletar usuário pelo seu id.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_EMAIL = "Erro ao consultar usuário pelo seu email.";

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioEntity usuarioEntity = UsuarioMapper.paraEntity(usuario);

        try {
            usuarioEntity = repository.save(usuarioEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR, ex.getCause());
        }

        return UsuarioMapper.paraDomain(usuarioEntity);
    }

    @Override
    public Optional<Usuario> consultarPorId(String idUsuario) {
        Optional<UsuarioEntity> usuarioEntity;

        try {
            usuarioEntity = repository.findById(idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return usuarioEntity.map(UsuarioMapper::paraDomain);
    }

    @Override
    public Optional<Usuario> consultarPorCpf(String cpf) {
        Optional<UsuarioEntity> usuarioEntity;

        try {
            usuarioEntity = repository.findByCpf(cpf);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_CPF, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_CPF, ex.getCause());
        }

        return usuarioEntity.map(UsuarioMapper::paraDomain);
    }

    @Override
    public void deletar(String idUsuario) {
        try {
            repository.deleteById(idUsuario);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR_PELO_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR_PELO_ID, ex.getCause());
        }
    }

    @Override
    public Optional<Usuario> consultarPorEmail(String email) {
        Optional<UsuarioEntity> usuarioEntity;

        try {
            usuarioEntity = repository.findByEmail(email);
        } catch (Exception ex) {
            log.info(MENSAGEM_ERRO_CONSULTAR_POR_EMAIL, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_EMAIL, ex.getCause());
        }

        return usuarioEntity.map(UsuarioMapper::paraDomain);
    }
}
