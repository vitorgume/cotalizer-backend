package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gumeinteligenciacomercial.orcaja.application.usecase.cpf_cnpj.CNPJ;
import com.gumeinteligenciacomercial.orcaja.application.usecase.cpf_cnpj.CPF;
import com.gumeinteligenciacomercial.orcaja.domain.Plano;
import com.gumeinteligenciacomercial.orcaja.domain.StatusUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioDto {

    @JsonProperty("id")
    private String id;

    @NotBlank(message = "O nome é obrigatório")
    @JsonProperty("nome")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @JsonProperty("telefone")
    private String telefone;

    @Pattern(
            regexp = "^\\d{14}$",
            message = "O CNPJ deve conter exatamente 14 dígitos numéricos, sem formatação"
    )
    @JsonProperty("cpf")
    @CPF
    private String cpf;

    @JsonProperty("cnpj")
    @CNPJ
    private String cnpj;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).+$",
            message = "A senha deve conter pelo menos uma letra, um número e um caractere especial.")
    @JsonProperty("senha")
    private String senha;

    @JsonProperty("status")
    private StatusUsuario status;

    @JsonProperty("plano")
    private Plano plano;

    @JsonProperty("id_customer")
    private String idCustomer;

    @JsonProperty("id_assinatura")
    private String idAssinatura;

    @JsonProperty("url_logo")
    private String urlLogo;

    @JsonProperty("feedback")
    private Boolean feedback;

    @JsonProperty("quantidade_orcamentos")
    private Integer quantidadeOrcamentos;

    @JsonProperty("data_criacao")
    private LocalDateTime dataCriacao;
}
