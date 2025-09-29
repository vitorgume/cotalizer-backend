package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.gateway.TemplateGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateUseCase {

    private final TemplateGateway gateway;

    public List<Template> listarTodos() {
        return gateway.listarTodos();
    }

}
