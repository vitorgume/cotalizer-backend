package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquvioException;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArquivoUseCase {

    private static final String BASE_PATH = "/temp/orcamentos/";

    public String salvarArquivo(Map<String, Object> orcamento) {
        try {
            String caminhoCompleto = BASE_PATH + gerarNomeArquivo() + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(caminhoCompleto));
            document.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Orçamento", titulo));
            document.add(new Paragraph("Data de geração: " + LocalDateTime.now(), normal));
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

            return caminhoCompleto;

        } catch (Exception e) {
            throw new ArquvioException("Erro ao gerar PDF", e);
        }
    }

    private String gerarNomeArquivo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "ARQ-" + uuid.substring(0, 5);
    }
}
