package com.gumeinteligenciacomercial.orcaja.entrypoint.controller.middleware;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.*;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class HndlerMIddleware {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> exceptionHandler(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(ArquivoException.class)
    public ResponseEntity<ResponseDto> arquivoExceptionHandler(ArquivoException exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(ConversaoJsonException.class)
    public ResponseEntity<ResponseDto> conversaoJsonExceptionHandler(ConversaoJsonException exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(CredenciasIncorretasException.class)
    public ResponseEntity<ResponseDto> credenciaisIncorretasException(CredenciasIncorretasException exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(OrcamentoNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> orcamentoNaoEncontradoExceptionHandler(OrcamentoNaoEncontradoException exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(UsuarioJaCadastradoException.class)
    public ResponseEntity<ResponseDto> usuarioJaCadastradoExceptionHandler(UsuarioJaCadastradoException exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> usuarioNaoEnconradoExceptionHandler(UsuarioNaoEncontradoException exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(CodigoInvalidoAlteracaoSenha.class)
    public ResponseEntity<ResponseDto> codigoInvalidoAlteracaoSenhaHandler(CodigoInvalidoAlteracaoSenha exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(CodigoInvalidoValidacaoEmailException.class)
    public ResponseEntity<ResponseDto> codigoInvalidoValidacaoEmailHandler(CodigoInvalidoValidacaoEmailException exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(LimiteOrcamentosPlano.class)
    public ResponseEntity<ResponseDto> limiteOrcamentoPlanoHandler(LimiteOrcamentosPlano exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(DataProviderException.class)
    public ResponseEntity<ResponseDto> dataProviderExceptionHandelr(DataProviderException exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder().mensagens(List.of(exception.getMessage())).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

}
