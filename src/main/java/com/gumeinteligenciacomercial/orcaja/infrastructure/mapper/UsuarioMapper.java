package com.gumeinteligenciacomercial.orcaja.infrastructure.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;

public class UsuarioMapper {
    public static UsuarioEntity paraEntity(Usuario domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .senha(domain.getSenha())
                .status(domain.getStatus())
                .plano(PlanoMapper.paraEntity(domain.getPlano()))
                .idAssinatura(domain.getIdAssinatura())
                .idCustomer(domain.getIdCustomer())
                .urlLogo(domain.getUrlLogo())
                .feedback(domain.getFeedback())
                .quantidadeOrcamentos(domain.getQuantidadeOrcamentos())
                .dataCriacao(domain.getDataCriacao())
                .tipoCadastro(domain.getTipoCadastro())
                .onboarding(domain.getOnboarding())
                .build();
    }

    public static Usuario paraDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .senha(entity.getSenha())
                .status(entity.getStatus())
                .plano(PlanoMapper.paraDomain(entity.getPlano()))
                .idCustomer(entity.getIdCustomer())
                .idAssinatura(entity.getIdAssinatura())
                .urlLogo(entity.getUrlLogo())
                .feedback(entity.getFeedback())
                .quantidadeOrcamentos(entity.getQuantidadeOrcamentos())
                .dataCriacao(entity.getDataCriacao())
                .tipoCadastro(entity.getTipoCadastro())
                .onboarding(entity.getOnboarding())
                .build();
    }
}
