package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.domain.Orcamento;
import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoException;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
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
    private static final String BASE_API_FILE = "http://localhost:8080/arquivos/";
    private final OrcamentoUseCase orcamentoUseCase;

    public Orcamento salvarArquivo(Orcamento novoOrcamento) {
        log.info("Gerenado pdf do orçamento. Orçamento: {}", novoOrcamento);

        String urlArquivo;
        try {
            Map<String, Object> orcamento = novoOrcamento.getOrcamentoFormatado();
            String nomeArquivo = gerarNomeArquivo() + ".pdf";
            String caminhoParaSalvar = Paths.get(BASE_PATH, nomeArquivo).toString();

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(caminhoParaSalvar));
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 12);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
            String dataFormatada = LocalDateTime.now().format(formatter);

            document.add(new Paragraph("Orçamento", titulo));
            document.add(new Paragraph("Data de geração: " + dataFormatada, normal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Cliente: " + orcamento.getOrDefault("cliente", ""), normal));
            document.add(new Paragraph("Data de entrega: " + orcamento.getOrDefault("dataEntrega", ""), normal));
            document.add(new Paragraph("Desconto: " + orcamento.getOrDefault("desconto", "0") + "%", normal));
            document.add(new Paragraph("Observações: " + orcamento.getOrDefault("observacoes", ""), normal));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Itens:", titulo));
            List<Map<String, Object>> itens = (List<Map<String, Object>>) orcamento.get("itens");
            for (Map<String, Object> item : itens) {
                document.add(new Paragraph(
                        "- " + item.get("descricao") + " | Qtde: " + item.get("quantidade") +
                                " | Unitário: R$ " + item.get("valorUnitario")
                ));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total: R$ " + orcamento.getOrDefault("total", "0"), titulo));

            document.close();

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

    private String gerarNomeArquivo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "ARQ-" + uuid.substring(0, 5);
    }
}

