package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;


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
    private String cpf;
    private String cnpj;
    private String senha;
    private StatusUsuario status;
    private Plano plano;
    private String idCustomer;
    private String idAssinatura;
    private String keyLogo;
    private Boolean feedback;

    public void setDados(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.telefone = usuario.getTelefone();
        this.status = usuario.getStatus();
        this.cpf = usuario.getCpf();
        this.cnpj = usuario.getCnpj();
        this.plano = usuario.getPlano();
        this.idCustomer = usuario.getIdCustomer();
        this.idAssinatura = usuario.getIdAssinatura();
        this.keyLogo = usuario.getKeyLogo();
        this.feedback = usuario.getFeedback();
    }
}
