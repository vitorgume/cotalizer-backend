package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.OrcamentoNaoEncontradoException;
import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoException;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CampoPersonalizadoDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.OrcamentoTradicionalDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ProdutoOrcamentoDto;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArquivoUseCase {
    private static final String BASE_PATH = "C:/Users/vitor/orcaja";

    private static final String BASE_API_FILE = "http://localhost:8080/arquivos/acessar/";
    private final OrcamentoUseCase orcamentoUseCase;
    private final OrcamentoTradicionalUseCase orcamentoTradicionalUseCase;

    public Orcamento salvarArquivo(Orcamento novoOrcamento) {
        log.info("Gerenado pdf do orçamento. Orçamento: {}", novoOrcamento);

        String urlArquivo;
        try {
            Map<String, Object> orcamento = novoOrcamento.getOrcamentoFormatado();
            String nomeArquivo = gerarNomeArquivo() + ".pdf";
            String caminhoParaSalvar = Paths.get(BASE_PATH, nomeArquivo).toString();

            String html = this.gerarHtml(orcamento);

            try (OutputStream outputStream = new FileOutputStream(caminhoParaSalvar)) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);
            }

            urlArquivo = BASE_API_FILE + nomeArquivo;
        } catch (Exception e) {
            log.error("Erro ao gerar pdf.", e);
            throw new ArquivoException("Erro ao gerar PDF", e);
        }

        Orcamento orcamento = orcamentoUseCase.consultarPorId(novoOrcamento.getId());
        orcamento.setUrlArquivo(urlArquivo);
        Orcamento orcamentoSalvo = orcamentoUseCase.alterar(orcamento.getId(), orcamento);

        log.info("Pdf do orçamento gerado com sucesso. Orçamento: {}", orcamentoSalvo);

        return orcamentoSalvo;
    }

    public OrcamentoTradicional salvarArquivoTradicional(OrcamentoTradicional novoOrcamento) {
        log.info("Gerando pdf do orçamento tradicional. Orçamento: {}", novoOrcamento);

        String urlArquivo;
        try {
            String nomeArquivo = gerarNomeArquivo() + ".pdf";
            String caminhoParaSalvar = Paths.get(BASE_PATH, nomeArquivo).toString();

            String html = this.gerarHtmlTradicional(novoOrcamento);

            try (OutputStream outputStream = new FileOutputStream(caminhoParaSalvar)) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);
            }

            urlArquivo = BASE_API_FILE + nomeArquivo;
        } catch (Exception e) {
            log.error("Erro ao gerar pdf.", e);
            throw new ArquivoException("Erro ao gerar PDF", e);
        }

        OrcamentoTradicional orcamento = orcamentoTradicionalUseCase.consultarPorId(novoOrcamento.getId());
        orcamento.setUrlArquivo(urlArquivo);
        OrcamentoTradicional orcamentoSalvo = orcamentoTradicionalUseCase.alterar(orcamento.getId(), orcamento);

        log.info("Pdf do orçamento tradicional gerado com sucesso. Orçamento: {}", orcamentoSalvo);

        return orcamentoSalvo;
    }

    private String gerarHtml(Map<String, Object> orcamento) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/template_orcamento.html");
        String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
        String dataFormatada = LocalDateTime.now().format(formatter);

        StringBuilder camposHtml = new StringBuilder();
        for (Map.Entry<String, Object> entry : orcamento.entrySet()) {
            String chave = formatarChave(entry.getKey());
            Object valor = entry.getValue();

            if (entry.getKey().equalsIgnoreCase("itens") || valor instanceof List)
                continue;

            camposHtml.append("<p><strong>")
                    .append(chave)
                    .append(":</strong> ")
                    .append(valor != null ? valor.toString() : "")
                    .append("</p>");
        }

        StringBuilder itensHtml = new StringBuilder();
        double subtotal = 0;
        List<Map<String, Object>> itens = (List<Map<String, Object>>) orcamento.get("itens");

        for (Map<String, Object> item : itens) {
            String descricao = String.valueOf(item.getOrDefault("descricao", item.getOrDefault("produto", "")));
            int quantidade = Integer.parseInt(item.getOrDefault("quantidade", "0").toString());
            double valorUnitario = Double.parseDouble(item.getOrDefault("valorUnitario", item.getOrDefault("valor_unit", "0")).toString());
            double totalItem = quantidade * valorUnitario;
            subtotal += totalItem;

            itensHtml.append(String.format("""
            <tr>
                <td><strong>%s</strong></td>
                <td>%d</td>
                <td>R$ %.2f</td>
                <td>R$ %.2f</td>
            </tr>
        """, descricao, quantidade, valorUnitario, totalItem));
        }

        double desconto = orcamento.get("desconto") != null ? Double.parseDouble(orcamento.get("desconto").toString()) : 0.0;
        double valorFinal = subtotal - (subtotal * desconto / 100);

        String htmlFinal = htmlTemplate
                .replace("${data}", dataFormatada)
                .replace("${campos}", camposHtml.toString())
                .replace("${itens}", itensHtml.toString())
                .replace("${subtotal}", String.format("R$ %.2f", subtotal))
                .replace("${desconto}", String.format("R$ %.2f", subtotal * desconto / 100))
                .replace("${total}", String.format("R$ %.2f", valorFinal));

        return htmlFinal;
    }

    private String gerarHtmlTradicional(OrcamentoTradicional novoOrcamento) throws IOException {
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("templates/template_orcamento_tradicional.html");
        if (is == null) throw new IllegalStateException("Template não encontrado!");

        String htmlTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String data     = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));

        StringBuilder camposPers = new StringBuilder();
        if (novoOrcamento.getCamposPersonalizados() != null) {
            for (CampoPersonalizado cp : novoOrcamento.getCamposPersonalizados()) {
                camposPers.append("<p><strong>")
                        .append(escapeHtml(cp.getTitulo()))
                        .append(":</strong> ")
                        .append(escapeHtml(cp.getValor()))
                        .append("</p>");
            }
        }

        StringBuilder itensHtml = new StringBuilder();
        double subtotal = 0;
        if (novoOrcamento.getProdutos() != null) {
            for (ProdutoOrcamento p : novoOrcamento.getProdutos()) {
                String desc  = escapeHtml(p.getDescricao());
                int qtd      = p.getQuantidade();
                double vu    = p.getValor().doubleValue();
                double total = qtd * vu;
                subtotal += total;

                itensHtml.append(String.format("""
                <tr>
                  <td>%s</td>
                  <td style="text-align:center">%d</td>
                  <td style="text-align:right">R$ %.2f</td>
                  <td style="text-align:right">R$ %.2f</td>
                </tr>
            """, desc, qtd, vu, total));
            }
        }

        String subtotalStr = String.format("R$ %.2f", subtotal);
        String totalStr    = subtotalStr; // sem desconto aqui

        return htmlTemplate
                .replace("${id}",               escapeHtml(novoOrcamento.getId()))
                .replace("${data}",             data)
                .replace("${cliente}",          escapeHtml(novoOrcamento.getCliente()))
                .replace("${cnpjCpf}",          escapeHtml(novoOrcamento.getCnpjCpf()))
                .replace("${observacoes}",      escapeHtml(novoOrcamento.getObservacoes()))
                .replace("${campos_personalizados}", camposPers.toString())
                .replace("${itens}",                itensHtml.toString())
                .replace("${subtotal}",             subtotalStr)
                .replace("${total}",                totalStr);
    }

    private String escapeHtml(String s) {
        return s == null ? "" : s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"","&quot;");
    }



    private String formatarChave(String chave) {
        return Arrays.stream(chave.split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));
    }


    private String gerarNomeArquivo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "ARQ-" + uuid.substring(0, 5);
    }
}

