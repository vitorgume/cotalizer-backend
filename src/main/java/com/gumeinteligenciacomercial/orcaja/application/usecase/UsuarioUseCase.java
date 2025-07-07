package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoValidacaoEmailException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioJaCadastradoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.UsuarioGateway;
import com.gumeinteligenciacomercial.orcaja.domain.StatusUsuario;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.domain.VerificacaoEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioUseCase {

    private final UsuarioGateway gateway;
    private final CriptografiaUseCase criptografiaUseCase;
    private final EmailUseCase emailUseCase;
    private final CodigoValidacaoUseCase codigoValidacaoUseCase;
    private final CodigoAlteracaoSenhaUseCase codigoAlteracaoSenhaUseCase;

    public Usuario cadastrar(Usuario usuario) {
        log.info("Cadastrando novo usuário. Usuário: {}", usuario);
        Optional<Usuario> usuarioExistente = this.consultarPorCpf(usuario.getCpf());

        usuarioExistente.ifPresent(us -> {
            throw new UsuarioJaCadastradoException();
        });

        usuario.setSenha(criptografiaUseCase.criptografar(usuario.getSenha()));

        if(usuario.getCpf() == null || usuario.getCnpj() == null) {
            this.validacaoEmail(usuario.getEmail());
        }

        usuario.setStatus(StatusUsuario.PENDENTE_VALIDACAO_EMAIL);

        Usuario usuarioSalvo = gateway.salvar(usuario);

        log.info("Usuário salvo com sucesso. Usuário: {}", usuarioSalvo);

        return usuarioSalvo;
    }

    public Usuario consultarPorId(String idUsuario) {
        log.info("Consultando usuário pelo seu id. Id do usuário: {}", idUsuario);
        Optional<Usuario> usuario = gateway.consultarPorId(idUsuario);

        if(usuario.isEmpty()) {
            throw new UsuarioNaoEncontradoException();
        }

        log.info("Usuário consultado com sucesso pelo seu id. Usuário: {}", usuario.get());

        return usuario.get();
    }

    public void deletar(String idUsuario) {
        this.consultarPorId(idUsuario);
        gateway.deletar(idUsuario);
    }

    public Usuario consultarPorEmail(String email) {
        log.info("Consultando usuário pelo seu email. Email: {}", email);
        Optional<Usuario> usuario = gateway.consultarPorEmail(email);

        if(usuario.isEmpty()) {
            throw new UsuarioNaoEncontradoException();
        }

        log.info("Usuário consultado com sucesso pelo seu email. Usuario: {}", usuario);

        return usuario.get();
    }

    public Usuario alterar(String id, Usuario novosDados) {
        log.info("Alterando dados do usuário. Id: {}, Novos dados: {}", id, novosDados);

        Usuario usuario = consultarPorId(id);

        usuario.setDados(novosDados);

        usuario = gateway.salvar(usuario);

        log.info("Alteração de dados do usuário concluida com sucesso. Usuario: {}", usuario);

        return usuario;
    }

    public VerificacaoEmail validarCodigoVerificacao(String email, String codigo) {

        if(codigoValidacaoUseCase.validar(email, codigo)) {
            Usuario usuario = this.consultarPorEmail(email);
            usuario.setStatus(StatusUsuario.ATIVO);
            alterar(usuario.getId(), usuario);
            return VerificacaoEmail.builder().email(email).build();
        } else {
            throw new CodigoInvalidoValidacaoEmailException();
        }
    }

    public void reenviarCodigoEmail(String email) {
        this.consultarPorEmail(email);
        this.validacaoEmail(email);
    }

    public Usuario alterarSenha(String novaSenha, String idUsuario, String codigo) {

        Usuario usuario = this.consultarPorId(idUsuario);

        if(codigoAlteracaoSenhaUseCase.validaCodigoAlteracaoSenha(usuario.getId(), codigo)) {
            String novaSenhaCriptografada = criptografiaUseCase.criptografar(novaSenha);
            usuario.setSenha(novaSenhaCriptografada);
        }

        return gateway.salvar(usuario);
    }

    private void validacaoEmail(String email) {
        String codigo = codigoValidacaoUseCase.gerarCodigo(email);
        emailUseCase.enviarCodigoVerificacao(email, codigo);
    }

    private Optional<Usuario> consultarPorCpf(String cpf) {
        return gateway.consultarPorCpf(cpf);
    }
}
