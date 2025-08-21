package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoException;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoNaoEncontrado;
import com.gumeinteligenciacomercial.orcaja.application.gateway.ArquivoGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArquivoUseCase {

    private final OrcamentoIaUseCase orcamentoIaUseCase;
    private final OrcamentoTradicionalUseCase orcamentoTradicionalUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final ArquivoGateway gateway;
    private final HtmlUseCase htmlUseCase;
    private static final Path BASE_DIR = Paths.get("C:/Users/vitor/orcaja");

    public Orcamento salvarArquivo(Orcamento novoOrcamento) {
        log.info("Gerenado pdf do orçamento. Orçamento: {}", novoOrcamento);

        String html = htmlUseCase.gerarHtml(novoOrcamento.getOrcamentoFormatado(), novoOrcamento.getUsuarioId());

        String urlArquivo = gateway.salvarPdf(this.gerarNomeArquivo(), html);

        Orcamento orcamento = orcamentoIaUseCase.consultarPorId(novoOrcamento.getId());
        orcamento.setUrlArquivo(urlArquivo);
        Orcamento orcamentoSalvo = orcamentoIaUseCase.alterar(orcamento.getId(), orcamento);

        log.info("Pdf do orçamento gerado com sucesso. Orçamento: {}", orcamentoSalvo);

        return orcamentoSalvo;
    }

    public OrcamentoTradicional salvarArquivoTradicional(OrcamentoTradicional novoOrcamento) {
        log.info("Gerando pdf do orçamento tradicional. Orçamento: {}", novoOrcamento);

        String html = htmlUseCase.gerarHtmlTradicional(novoOrcamento);

        String urlArquivo = gateway.salvarPdf(this.gerarNomeArquivo(), html);

        OrcamentoTradicional orcamento = orcamentoTradicionalUseCase.consultarPorId(novoOrcamento.getId());
        orcamento.setUrlArquivo(urlArquivo);
        OrcamentoTradicional orcamentoSalvo = orcamentoTradicionalUseCase.alterar(orcamento.getId(), orcamento);

        log.info("Pdf do orçamento tradicional gerado com sucesso. Orçamento: {}", orcamentoSalvo);

        return orcamentoSalvo;
    }

    public String cadastrarLogo(String idUsuario, MultipartFile multipartFile) {
        Usuario usuario = usuarioUseCase.consultarPorId(idUsuario);

        String logoPathRelativo = gateway.salvarLogo(usuario.getId(), multipartFile);

        usuario.setUrlLogo(logoPathRelativo);
        usuarioUseCase.alterar(usuario.getId(), usuario);

        return logoPathRelativo;
    }


    private String gerarNomeArquivo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "ARQ-" + uuid.substring(0, 5);
    }

    public Resource acessarArquivo(String nomeArquivo) {
        try {
            Path arquivoPath = BASE_DIR.resolve(nomeArquivo).normalize();
            Resource resource = new UrlResource(arquivoPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ArquivoNaoEncontrado();
            }
            return resource;
        } catch (MalformedURLException | NullPointerException e) {
            log.error("Erro ao acessar arquivo: {}", nomeArquivo, e);
            throw new ArquivoException("Erro ao acessar arquivo: " + nomeArquivo, e);
        }
    }

    public Resource downloadArquivo(String nomeArquivo) {
        return acessarArquivo(nomeArquivo);
    }
}

