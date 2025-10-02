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
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

            // ========= normalização & regras
            Set<String> hideInCampos = new HashSet<>(Arrays.asList(
                    "itens",
                    "subtotal", "total", "valor_total", "valortotal", "valor_total_final", "valor_total_liquido", "valor_total_bruto",
                    "desconto_calculado", "descontocalculado"
            ));

            Set<String> moneyKeys = new HashSet<>(Arrays.asList(
                    "subtotal", "total", "valor_total", "valortotal", "valor_total_final", "valor_total_liquido", "valor_total_bruto"
            ));

            Set<String> discountKeys = new HashSet<>(Arrays.asList(
                    "desconto", "desconto_percentual"
            ));

            // rótulos “bonitos”
            Map<String, String> labelOverrides = new HashMap<>();
            labelOverrides.put("cliente", "Cliente"); // <— novo
            labelOverrides.put("nome_orcamento", "Nome do Orçamento");
            labelOverrides.put("nomeorcamento", "Nome do Orçamento");
            labelOverrides.put("nome_cliente", "Cliente");
            labelOverrides.put("nomecliente", "Cliente");
            labelOverrides.put("forma_pagamento", "Forma de Pagamento");
            labelOverrides.put("formapagamento", "Forma de Pagamento");
            labelOverrides.put("observacao", "Observação");
            labelOverrides.put("observacoes", "Observação"); // variação
            labelOverrides.put("prazo", "Prazo");
            labelOverrides.put("segmento", "Segmento");
            labelOverrides.put("empresa", "Empresa");
            labelOverrides.put("contato", "Contato");
            labelOverrides.put("cnpj_cpf", "CNPJ/CPF");
            labelOverrides.put("cnpj", "CNPJ");
            labelOverrides.put("cpf", "CPF");
            labelOverrides.put("desconto", "Desconto");

            // aliases → canônico (para ordenar/deduplicar)
            Map<String, String> canonical = new HashMap<>();
            canonical.put("nome_cliente", "cliente");
            canonical.put("nomecliente", "cliente");
            canonical.put("cliente", "cliente");

            canonical.put("nome_orcamento", "nome_orcamento");
            canonical.put("nomeorcamento", "nome_orcamento");

            canonical.put("observacao", "observacao");
            canonical.put("observacoes", "observacao");

            canonical.put("desconto", "desconto");
            canonical.put("desconto_percentual", "desconto");

            canonical.put("prazo", "prazo");

            // prioridade (menor = aparece primeiro)
            Map<String, Integer> priority = new HashMap<>();
            priority.put("cliente", 1);
            priority.put("nome_orcamento", 2);
            priority.put("observacao", 3);
            priority.put("desconto", 4);
            priority.put("prazo", 5);

            // ========= monta ${campos} (filtrado, formatado, ordenado)
            final class Campo {
                String canon, normKey, label, value;

                Campo(String canon, String normKey, String label, String value) {
                    this.canon = canon;
                    this.normKey = normKey;
                    this.label = label;
                    this.value = value;
                }
            }
            List<Campo> lista = new ArrayList<>();
            Set<String> seenCanon = new HashSet<>(); // evita duplicatas (ex.: cliente vs nome_cliente)

            for (Map.Entry<String, Object> entry : orcamento.entrySet()) {
                String rawKey = entry.getKey();
                Object rawVal = entry.getValue();

                // pula listas/mapas (ex.: itens)
                if (rawVal instanceof Collection || rawVal instanceof Map) continue;

                String normKey = normalizeKey(rawKey); // camel/snake → snake minúsculo

                // campos a esconder da área ${campos}
                if (hideInCampos.contains(normKey)) continue;

                // valor vazio? pula
                if (isEmptyValue(rawVal)) continue;

                // prepara valor
                String displayVal = formatDisplayValue(normKey, rawVal, moneyKeys, discountKeys);
                if (displayVal == null || displayVal.isBlank()) continue;

                // canônico para ordenar/deduplicar
                String canonKey = canonical.getOrDefault(normKey, normKey);

                // dedup por canônico (fica o primeiro válido)
                if (seenCanon.contains(canonKey)) continue;
                seenCanon.add(canonKey);

                String label = prettyLabel(normKey, labelOverrides);
                lista.add(new Campo(canonKey, normKey, label, displayVal));
            }

            // ordenação pela prioridade, depois rótulo alfabético
            lista.sort((a, b) -> {
                int pa = priority.getOrDefault(a.canon, 1000);
                int pb = priority.getOrDefault(b.canon, 1000);
                if (pa != pb) return Integer.compare(pa, pb);
                int byLabel = a.label.compareToIgnoreCase(b.label);
                if (byLabel != 0) return byLabel;
                return a.canon.compareTo(b.canon);
            });

            StringBuilder camposHtml = new StringBuilder();
            for (Campo c : lista) {
                camposHtml.append("<p><strong>")
                        .append(escapeHtml(c.label))
                        .append(":</strong> ")
                        .append(escapeHtml(c.value))
                        .append("</p>");
            }

            // ========= Itens (igual ao seu)
            StringBuilder itensHtml = new StringBuilder();
            double subtotal = 0.0;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itens = (List<Map<String, Object>>) orcamento.get("itens");
            if (itens != null) {
                for (Map<String, Object> item : itens) {
                    String descricao = String.valueOf(item.getOrDefault("descricao", item.getOrDefault("produto", "")));
                    int quantidade = (int) Math.round(parseNumberFlexible(item.getOrDefault("quantidade", "0")));
                    double valorUnit = parseNumberFlexible(item.getOrDefault("valorUnitario", item.getOrDefault("valor_unit", "0")));
                    double totalItem = quantidade * valorUnit;
                    subtotal += totalItem;

                    itensHtml.append(String.format(
                            "<tr>" +
                                    "<td><div class=\"item-name\">%s</div></td>" +
                                    "<td>%d</td>" +
                                    "<td>%s</td>" +
                                    "<td>%s</td>" +
                                    "</tr>",
                            escapeHtml(descricao),
                            quantidade,
                            escapeHtml(toBRL(valorUnit)),
                            escapeHtml(toBRL(totalItem))
                    ));
                }
            }

            // ========= Desconto/Total
            double desconto = parseDiscount(orcamento.get("desconto"), subtotal);
            double valorFinal = subtotal - desconto;

            // ========= Logo
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

    /* ===== Helpers ===== */


    private String normalizeKey(String key) {
        if (key == null) return "";
        String k = key.trim();

        k = k.replaceAll("([a-z])([A-Z])", "$1_$2");

        k = k.replaceAll("[\\s\\-]+", "_");

        k = k.toLowerCase(Locale.ROOT);
        return k;
    }


    private String prettyLabel(String normKey, Map<String, String> overrides) {
        if (overrides.containsKey(normKey)) return overrides.get(normKey);

        String[] parts = normKey.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
            if (i < parts.length - 1) sb.append(' ');
        }
        return sb.toString();
    }

    private boolean isEmptyValue(Object v) {
        if (v == null) return true;
        if (v instanceof String) return ((String) v).trim().isEmpty();
        return false;
    }

    private String formatDisplayValue(
            String normKey,
            Object rawVal,
            Set<String> moneyKeys,
            Set<String> discountKeys
    ) {
        String s = rawVal == null ? "" : String.valueOf(rawVal).trim();

        if (discountKeys.contains(normKey)) {

            if (s.endsWith("%")) return s;
            double n = parseNumberFlexible(s);
            if (Double.isNaN(n)) return s;
            if (n <= 1.0) n *= 100.0;
            return stripTrailingZeros(n) + "%";
        }

        if (moneyKeys.contains(normKey)) {

            double n = parseNumberFlexible(s);
            return toBRL(n);
        }

        return s;
    }

    private static String stripTrailingZeros(double n) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        if (nf instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) nf;
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(0);
            df.setGroupingUsed(false);
            return df.format(n);
        }
        return String.valueOf(n);
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
        int lastDot = s.lastIndexOf('.');
        int lastSep = Math.max(lastComma, lastDot);

        if (lastSep >= 0) {
            String intPart = s.substring(0, lastSep).replaceAll("[^0-9-]", ""); // remove milhares
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
