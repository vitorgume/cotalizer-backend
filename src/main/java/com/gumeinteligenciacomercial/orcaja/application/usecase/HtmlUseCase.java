package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoException;
import com.gumeinteligenciacomercial.orcaja.domain.CampoPersonalizado;
import com.gumeinteligenciacomercial.orcaja.domain.OrcamentoTradicional;
import com.gumeinteligenciacomercial.orcaja.domain.ProdutoOrcamento;
import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HtmlUseCase {

    private final UsuarioUseCase usuarioUseCase;

    public String gerarHtml(Map<String, Object> orcamento, String idUsuario) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/template_orcamento.html");

        try {
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

            Usuario usuario = usuarioUseCase.consultarPorId(idUsuario);

            final String backendBaseUrl = "https://cotalizer-backend.onrender.com";
            String logoPath = usuario.getUrlLogo();
            String logoUrl = resolveLogoUrl(logoPath, backendBaseUrl);

            String htmlFinal = htmlTemplate
                    .replace("${logoSrc}", logoUrl)
                    .replace("${data}", dataFormatada)
                    .replace("${campos}", camposHtml.toString())
                    .replace("${itens}", itensHtml.toString())
                    .replace("${subtotal}", String.format("R$ %.2f", subtotal))
                    .replace("${desconto}", String.format("R$ %.2f", subtotal * desconto / 100))
                    .replace("${total}", String.format("R$ %.2f", valorFinal));

            return htmlFinal;
        } catch (Exception ex) {
            log.error("Erro ao gerar html para orçamento com IA.", ex);
            throw new ArquivoException("Erro ao gerar html para orçamento com IA.", ex);
        }

    }

    public String gerarHtmlTradicional(OrcamentoTradicional novoOrcamento) {
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("templates/template_orcamento_tradicional.html");
        if (is == null) throw new IllegalStateException("Template não encontrado!");

        try (is) {
            String htmlTemplate = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String data = LocalDateTime.now()
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
                    String desc = escapeHtml(p.getDescricao());
                    int qtd = p.getQuantidade();
                    double vu = p.getValor().doubleValue();
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
            String totalStr = subtotalStr;

            Usuario usuario = usuarioUseCase.consultarPorId(novoOrcamento.getIdUsuario());

            final String backendBaseUrl = "https://cotalizer-backend.onrender.com";
            String logoPath = usuario.getUrlLogo();
            String logoUrl = resolveLogoUrl(logoPath, backendBaseUrl);

            return htmlTemplate
                    .replace("${logo_src}", logoUrl)
                    .replace("${id}", escapeHtml(novoOrcamento.getId()))
                    .replace("${data}", data)
                    .replace("${cliente}", escapeHtml(novoOrcamento.getCliente()))
                    .replace("${cnpjCpf}", escapeHtml(novoOrcamento.getCnpjCpf()))
                    .replace("${observacoes}", escapeHtml(novoOrcamento.getObservacoes()))
                    .replace("${campos_personalizados}", camposPers.toString())
                    .replace("${itens}", itensHtml.toString())
                    .replace("${subtotal}", subtotalStr)
                    .replace("${total}", totalStr);
        } catch (Exception ex) {
            log.error("Erro ao gerar html tradicional.", ex);
            throw new ArquivoException("Erro ao gerar html tradicional.", ex);
        }
    }


    private String escapeHtml(String s) {
        return s == null ? "" : s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String formatarChave(String chave) {
        return Arrays.stream(chave.split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));
    }

    private static boolean isAbsoluteUrl(String s) {
        String u = s.toLowerCase();
        return u.startsWith("http://") || u.startsWith("https://") || u.startsWith("data:");
    }

    private String resolveLogoUrl(String logoPath, String backendBaseUrl) {
        if (logoPath == null || logoPath.isBlank()) return "";
        String lp = logoPath.trim();

        if (isAbsoluteUrl(lp)) {
            return lp; // já é uma URL completa
        }
        // caminho relativo servido pelo backend (ex.: "/arquivos/acessar/...")
        if (lp.startsWith("/")) {
            // garante uma única barra na junção
            return backendBaseUrl.endsWith("/") ? backendBaseUrl.substring(0, backendBaseUrl.length() - 1) + lp
                    : backendBaseUrl + lp;
        }
        // fallback: trate como relativo sem barra
        return backendBaseUrl.endsWith("/") ? backendBaseUrl + lp : backendBaseUrl + "/" + lp;
    }

}
