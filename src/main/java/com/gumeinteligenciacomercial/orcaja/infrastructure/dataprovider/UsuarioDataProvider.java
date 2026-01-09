package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.UsuarioGateway;
import com.gumeinteligenciacomercial.orcaja.domain.TipoPlano;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.UsuarioMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.UsuarioRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioDataProvider implements UsuarioGateway {

    private final UsuarioRepository repository;
    private final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar usuário.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar usuário pelo seu id.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_EMAIL = "Erro ao consultar usuário pelo seu email.";
    private final String MENSAGEM_ERRO_LISTAR_USUARIOS = "Erro ao listar usuários.";
    private final String MENSAGEM_ERRO_LISTAR_PLANO_PADRAO = "Erro ao listar usuário pelo plano padrão.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE = "Erro ao consultar usuário pelo telefone.";

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
    public Optional<Usuario> consultarPorEmail(String email) {
        Optional<UsuarioEntity> usuarioEntity;

        try {
            usuarioEntity = repository.findByEmail(email);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_EMAIL, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_EMAIL, ex.getCause());
        }

        return usuarioEntity.map(UsuarioMapper::paraDomain);
    }

    @Override
    public List<Usuario> listar() {
        List<UsuarioEntity> usuarios;

        try {
            usuarios = repository.findAll();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_USUARIOS, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_USUARIOS, ex.getCause());
        }

        return usuarios.stream().map(UsuarioMapper::paraDomain).toList();
    }

    @Override
    public List<Usuario> listarPlanoGratis() {
        List<UsuarioEntity> usuarios;

        try {
            usuarios = repository.findByPlanoTipoPlano(TipoPlano.GRATIS);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_PLANO_PADRAO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_PLANO_PADRAO, ex.getCause());
        }


        return usuarios.stream().map(UsuarioMapper::paraDomain).toList();
    }

    @Override
    public Optional<Usuario> consultarPorTelefone(String telefone) {
        Optional<UsuarioEntity> usuario;

        try {
            usuario = repository.findByTelefone(telefone);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex.getCause());
        }

        return usuario.map(UsuarioMapper::paraDomain);
    }
}
