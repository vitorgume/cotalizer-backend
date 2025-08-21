package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;

public class UsuarioMapper {
    public static UsuarioEntity paraEntity(Usuario domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .cpf(domain.getCpf())
                .cnpj(domain.getCnpj())
                .email(domain.getEmail())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .senha(domain.getSenha())
                .status(domain.getStatus())
                .plano(domain.getPlano())
                .idAssinatura(domain.getIdAssinatura())
                .idCustomer(domain.getIdCustomer())
                .urlLogo(domain.getUrlLogo())
                .feedback(domain.getFeedback())
                .build();
    }

    public static Usuario paraDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .cpf(entity.getCpf())
                .cnpj(entity.getCnpj())
                .email(entity.getEmail())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .senha(entity.getSenha())
                .status(entity.getStatus())
                .plano(entity.getPlano())
                .idCustomer(entity.getIdCustomer())
                .idAssinatura(entity.getIdAssinatura())
                .urlLogo(entity.getUrlLogo())
                .feedback(entity.getFeedback())
                .build();
    }
}
