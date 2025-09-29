package com.gumeinteligenciacomercial.orcaja.application.gateway;

import com.gumeinteligenciacomercial.orcaja.domain.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateGateway {
    List<Template> listarTodos();
}
