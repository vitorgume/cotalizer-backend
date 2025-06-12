package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Login {
    private String email;
    private String senha;
    private String token;
}
