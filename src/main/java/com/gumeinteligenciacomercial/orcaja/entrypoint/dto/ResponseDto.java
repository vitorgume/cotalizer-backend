package com.gumeinteligenciacomercial.orcaja.entrypoint.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResponseDto<D> {
    private D dado;
    private ErroDto erro;

    public ResponseDto(D dado) {
        this.dado = dado;
    }

    @Getter
    @Setter
    @Builder
    public static class ErroDto {
        private List<String> mensagens;
    }

    public static <D> ResponseDto<D> comErro(ErroDto erro) {
        ResponseDto<D> responseDto = new ResponseDto<>();
        responseDto.setErro(erro);
        return responseDto;
    }
}
