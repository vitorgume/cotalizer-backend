package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoValidacaoEmailException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioJaCadastradoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.UsuarioNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.UsuarioGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.StatusUsuario;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.domain.VerificacaoEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

        if(usuario.getCpf().isBlank() || usuario.getCnpj().isBlank()) {
            this.validacaoEmail(usuario.getEmail());
        }

        usuario.setStatus(StatusUsuario.PENDENTE_VALIDACAO_EMAIL);
        usuario.setPlano(Plano.GRATIS);
        usuario.setQuantidadeOrcamentos(0);
        usuario.setDataCriacao(LocalDateTime.now());

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

    public Usuario inativar(String idUsuario) {
        log.info("Inativando usuario. Id usuario: {}", idUsuario);

        Usuario usuario = this.consultarPorId(idUsuario);

        usuario.setStatus(StatusUsuario.INATIVO);

        usuario = gateway.salvar(usuario);

        log.info("Inativação de usuário concluida com sucesso. Usuario: {}", usuario);

        return usuario;
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


        if(!novosDados.getEmail().equals(usuario.getEmail())) {
            this.validacaoEmail(novosDados.getEmail());
            usuario.setStatus(StatusUsuario.PENDENTE_VALIDACAO_EMAIL);
        }

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

    public Usuario alterarSenha(String novaSenha, String codigo) {
        String idUsuario = codigoAlteracaoSenhaUseCase.validaCodigoAlteracaoSenha(codigo);

        Usuario usuario = this.consultarPorId(idUsuario);

        if(idUsuario != null) {
            String novaSenhaCriptografada = criptografiaUseCase.criptografar(novaSenha);
            usuario.setSenha(novaSenhaCriptografada);
        }

        return gateway.salvar(usuario);
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void ajustarQuantidadeOrcamentoMensal() {
        List<Usuario> usuarios = this.listar();

        usuarios.forEach(usuario -> {
            usuario.setQuantidadeOrcamentos(0);
            this.alterar(usuario.getId(), usuario);
        });
    }

    private List<Usuario> listar() {
        return gateway.listar();
    }


    private void validacaoEmail(String email) {
        String codigo = codigoValidacaoUseCase.gerarCodigo(email);
        emailUseCase.enviarCodigoVerificacao(email, codigo);
    }

    private Optional<Usuario> consultarPorCpf(String cpf) {
        return gateway.consultarPorCpf(cpf);
    }
}
