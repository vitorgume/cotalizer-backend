package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;

public class UsuarioMapper {

    public static UsuarioDto paraDto(Usuario domain) {
        return UsuarioDto.builder()
                .id(domain.getId())
                .cpf(domain.getCpf())
                .cnpj(domain.getCnpj())
                .email(domain.getEmail())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .status(domain.getStatus())
                .plano(domain.getPlano())
                .idAssinatura(domain.getIdAssinatura())
                .idCustomer(domain.getIdCustomer())
                .urlLogo(domain.getKeyLogo())
                .feedback(domain.getFeedback())
                .build();
    }

    public static Usuario paraDomain(UsuarioDto dto) {
        return Usuario.builder()
                .id(dto.getId())
                .cpf(dto.getCpf())
                .cnpj(dto.getCnpj())
                .email(dto.getEmail())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .senha(dto.getSenha())
                .status(dto.getStatus())
                .plano(dto.getPlano())
                .idCustomer(dto.getIdCustomer())
                .idAssinatura(dto.getIdAssinatura())
                .keyLogo(dto.getUrlLogo())
                .feedback(dto.getFeedback())
                .build();
    }
}
