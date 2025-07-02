package com.gumeinteligenciacomercial.orcaja.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginGoogle {
    private HttpHeaders headers;
    private HttpStatus httpStatus;
}
