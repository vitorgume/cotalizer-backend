package com.gumeinteligenciacomercial.orcaja.application.gateway;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ArquivoGateway {
    String salvarPdf(String nomeArquivo, String html);

    String salvarLogo(String idUsuario, MultipartFile multipartFile);

    Resource carregarArquivo(String keyOuNomeArquivo);

    void deletarArquivo(String nomeArquivo);

    void deletarLogo(String nomeLogo);
}

