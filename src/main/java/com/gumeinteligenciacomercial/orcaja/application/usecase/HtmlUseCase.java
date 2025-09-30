package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ArquivoException;
import com.gumeinteligenciacomercial.orcaja.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HtmlUseCase {

    private final UsuarioUseCase usuarioUseCase;

    public String gerarHtml(Map<String, Object> orcamento, String idUsuario, String nomeTemplate) {
        String templatePath = "templates/" + nomeTemplate + ".html";

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (in == null) {
                throw new ArquivoException("Template não encontrado: " + templatePath, null);
            }

            String htmlTemplate = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
            String dataFormatada = LocalDateTime.now().format(formatter);

            // ===== Campos (tudo exceto 'itens')
            StringBuilder camposHtml = new StringBuilder();
            for (Map.Entry<String, Object> entry : orcamento.entrySet()) {
                String key = entry.getKey();
                Object valor = entry.getValue();
                if ("itens".equalsIgnoreCase(key) || valor instanceof List) continue;

                String chaveFmt = formatarChave(key);
                String valorFmt = valor != null ? escapeHtml(String.valueOf(valor)) : "";

                camposHtml.append("<p><strong>")
                        .append(escapeHtml(chaveFmt))
                        .append(":</strong> ")
                        .append(valorFmt)
                        .append("</p>");
            }

            // ===== Itens
            StringBuilder itensHtml = new StringBuilder();
            double subtotal = 0.0;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itens = (List<Map<String, Object>>) orcamento.get("itens");
            if (itens != null) {
                for (Map<String, Object> item : itens) {
                    String descricao = String.valueOf(
                            item.getOrDefault("descricao",
                                    item.getOrDefault("produto", ""))
                    );
                    int quantidade = (int) Math.round(parseNumberFlexible(item.getOrDefault("quantidade", "0")));
                    double valorUnit = parseNumberFlexible(
                            item.getOrDefault("valorUnitario", item.getOrDefault("valor_unit", "0"))
                    );
                    double totalItem = quantidade * valorUnit;
                    subtotal += totalItem;

                    itensHtml.append("""
                    <tr>
                      <td><strong>%s</strong></td>
                      <td>%d</td>
                      <td>%s</td>
                      <td>%s</td>
                    </tr>
                """.formatted(
                            escapeHtml(descricao),
                            quantidade,
                            toBRL(valorUnit),
                            toBRL(totalItem)
                    ));
                }
            }

            // ===== Desconto (valor absoluto; se vier "5%" vira subtotal*5/100)
            double desconto = parseDiscount(orcamento.get("desconto"), subtotal);
            double valorFinal = subtotal - desconto;

            // ===== Logo (data URI se possível)
            Usuario usuario = usuarioUseCase.consultarPorId(idUsuario);
            String rawLogo = Optional.ofNullable(usuario.getUrlLogo()).map(String::trim).orElse("");
            String logoSrc = Optional.ofNullable(toDataUri(rawLogo)).orElse("");

            String htmlFinal = htmlTemplate
                    .replace("${logo_src}", escapeHtml(logoSrc))
                    .replace("${id}", escapeHtml(UUID.randomUUID().toString()))
                    .replace("${data}", escapeHtml(dataFormatada))
                    .replace("${campos}", camposHtml.toString())
                    .replace("${itens}", itensHtml.toString())
                    .replace("${subtotal}", escapeHtml(toBRL(subtotal)))
                    .replace("${desconto}", escapeHtml(toBRL(desconto)))
                    .replace("${total}", escapeHtml(toBRL(valorFinal)));

            return htmlFinal;

        } catch (Exception ex) {
            log.error("Erro ao gerar html para orçamento com IA.", ex);
            throw new ArquivoException("Erro ao gerar html para orçamento com IA.", ex);
        }
    }

    public String gerarHtmlTradicional(OrcamentoTradicional novoOrcamento) {

        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("templates/" + novoOrcamento.getTemplate().getNomeArquivo() + "_tradicional.html");
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

            String rawLogo = Optional.ofNullable(usuario.getUrlLogo()).map(String::trim).orElse("");
            String logoSrc = toDataUri(rawLogo);

            if (logoSrc.isBlank()) {
                logoSrc = "";
            }

            return htmlTemplate
                    .replace("${logo_src}", logoSrc)
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

    private String toDataUri(String url) {
        if (url == null) return "";
        url = url.trim();
        if (url.isEmpty()) return "";

        if (url.startsWith("data:")) return url;

        try {
            var req = java.net.http.HttpRequest.newBuilder(java.net.URI.create(url))
                    .timeout(java.time.Duration.ofSeconds(5))
                    .GET()
                    .build();

            var client = java.net.http.HttpClient.newHttpClient();
            var resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray());

            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                var bytes = resp.body();
                String ctype = resp.headers().firstValue("Content-Type").orElse("image/png");
                String b64 = java.util.Base64.getEncoder().encodeToString(bytes);
                return "data:" + ctype + ";base64," + b64;
            } else {
                log.warn("Falha ao baixar logo {}: status {}", url, resp.statusCode());
                return "";
            }
        } catch (Exception e) {
            log.warn("Falha ao embutir logo em base64 a partir de '{}'", url, e);
            return "";
        }
    }

    private double parseNumberFlexible(Object raw) {
        if (raw == null) return 0.0;
        if (raw instanceof Number n) return n.doubleValue();

        String s = raw.toString().trim();
        if (s.isEmpty()) return 0.0;

        // remove tudo que não for dígito, ponto, vírgula, sinal
        s = s.replaceAll("[^0-9,.-]", "");

        // Se houver vírgula e ponto, assume que o ÚLTIMO separador é o decimal.
        int lastComma = s.lastIndexOf(',');
        int lastDot   = s.lastIndexOf('.');
        int lastSep   = Math.max(lastComma, lastDot);

        if (lastSep >= 0) {
            String intPart  = s.substring(0, lastSep).replaceAll("[^0-9-]", ""); // remove milhares
            String fracPart = s.substring(lastSep + 1).replaceAll("[^0-9]", "");
            s = intPart + "." + fracPart;
        } else {
            // só vírgulas OU só pontos? Normalize vírgula para ponto
            s = s.replace(',', '.');
        }

        if (s.equals(".") || s.equals("-") || s.equals("-.") || s.isEmpty()) return 0.0;
        return Double.parseDouble(s);
    }

    private double parseDiscount(Object raw, double subtotal) {
        if (raw == null) return 0.0;

        String str = raw.toString().trim();
        boolean isPercent = str.endsWith("%");
        if (isPercent) {
            // pega só a parte numérica e calcula sobre subtotal
            double p = parseNumberFlexible(str);
            return subtotal * (p / 100.0);
        } else {
            return parseNumberFlexible(str);
        }
    }

    private static String toBRL(double v) {
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        return nf.format(v);
    }

}
