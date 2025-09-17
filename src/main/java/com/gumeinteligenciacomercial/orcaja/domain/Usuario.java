package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String senha;
    private StatusUsuario status;
    private Plano plano;
    private String idCustomer;
    private String idAssinatura;
    private String urlLogo;
    private Boolean feedback;
    private Integer quantidadeOrcamentos;
    private LocalDateTime dataCriacao;
    private TipoCadastro tipoCadastro;

    public void setDados(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.telefone = usuario.getTelefone();
        this.status = usuario.getStatus();
        this.plano = usuario.getPlano();
        this.idCustomer = usuario.getIdCustomer();
        this.idAssinatura = usuario.getIdAssinatura();
        this.urlLogo = usuario.getUrlLogo();
        this.feedback = usuario.getFeedback();
        this.quantidadeOrcamentos = usuario.getQuantidadeOrcamentos();
    }

    public void somarOrcamentos() {
        setQuantidadeOrcamentos(this.quantidadeOrcamentos + 1);
    }
}
