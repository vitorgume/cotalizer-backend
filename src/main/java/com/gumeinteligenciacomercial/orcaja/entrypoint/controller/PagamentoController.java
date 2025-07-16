package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.application.usecase.PagamentoUseCase;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.CardPaymentDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.PaymentResponseDto;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoUseCase pagamentoUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<PaymentResponseDto>> pagar(@RequestBody CardPaymentDto cardPaymentDTO) {
        PaymentResponseDto resultado = pagamentoUseCase.pagar(cardPaymentDTO);
        ResponseDto<PaymentResponseDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }
}
