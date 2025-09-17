package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;

public class UsuarioMapper {

    public static UsuarioDto paraDto(Usuario domain) {
        return UsuarioDto.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .status(domain.getStatus())
                .plano(domain.getPlano())
                .idAssinatura(domain.getIdAssinatura())
                .idCustomer(domain.getIdCustomer())
                .urlLogo(domain.getUrlLogo())
                .feedback(domain.getFeedback())
                .quantidadeOrcamentos(domain.getQuantidadeOrcamentos())
                .dataCriacao(domain.getDataCriacao())
                .tipoCadastro(domain.getTipoCadastro())
                .build();
    }

    public static Usuario paraDomain(UsuarioDto dto) {
        return Usuario.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .senha(dto.getSenha())
                .status(dto.getStatus())
                .plano(dto.getPlano())
                .idCustomer(dto.getIdCustomer())
                .idAssinatura(dto.getIdAssinatura())
                .urlLogo(dto.getUrlLogo())
                .feedback(dto.getFeedback())
                .quantidadeOrcamentos(dto.getQuantidadeOrcamentos())
                .dataCriacao(dto.getDataCriacao())
                .tipoCadastro(dto.getTipoCadastro())
                .build();
    }
}
