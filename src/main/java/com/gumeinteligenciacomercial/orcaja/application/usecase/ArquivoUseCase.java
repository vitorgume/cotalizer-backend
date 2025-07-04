package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoException;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArquivoUseCase {

    private static final String BASE_PATH = "C:/Users/vitor/orcaja";
    private static final String BASE_API_FILE = "http://localhost:8080/arquivos/acessar/";
    private final OrcamentoUseCase orcamentoUseCase;

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
            throw new ArquivoException("Erro ao gerar PDF", e);
        }

        Orcamento orcamento = orcamentoUseCase.consultarPorId(novoOrcamento.getId());
        orcamento.setUrlArquivo(urlArquivo);
        Orcamento orcamentoSalvo = orcamentoUseCase.alterar(orcamento.getId(), orcamento);

        log.info("Pdf do orçamento gerado com sucesso. Orçamento: {}", orcamentoSalvo);

        return orcamentoSalvo;
    }

    private String gerarHtml(Map<String, Object> orcamento) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/template_orcamento.html");
        String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
        String dataFormatada = LocalDateTime.now().format(formatter);

        StringBuilder itensHtml = new StringBuilder();
        List<Map<String, Object>> itens = (List<Map<String, Object>>) orcamento.get("itens");

        for (Map<String, Object> item : itens) {
            String descricao = (String) item.getOrDefault("descricao", "");
            Integer quantidade = Integer.parseInt(item.get("quantidade").toString());
            Double valorUnitario = Double.parseDouble(item.get("valorUnitario").toString());
            Double subtotal = valorUnitario * quantidade;

            itensHtml.append(String.format("""
                        <tr>
                            <td>%s</td>
                            <td>%d</td>
                            <td>R$ %.2f</td>
                            <td>R$ %.2f</td>
                        </tr>
                    """, descricao, quantidade, valorUnitario, subtotal));
        }

        String htmlFinal = htmlTemplate
                .replace("${data}", dataFormatada)
                .replace("${cliente}", (String) orcamento.getOrDefault("cliente", ""))
                .replace("${dataEntrega}", (String) orcamento.getOrDefault("dataEntrega", ""))
                .replace("${desconto}", orcamento.getOrDefault("desconto", "0").toString())
                .replace("${total}", orcamento.getOrDefault("total", "0.00").toString())
                .replace("${observacoes}", (String) orcamento.getOrDefault("observacoes", ""))
                .replace("${itens}", itensHtml.toString());

        return htmlFinal;
    }


    private String gerarNomeArquivo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "ARQ-" + uuid.substring(0, 5);
    }
}

