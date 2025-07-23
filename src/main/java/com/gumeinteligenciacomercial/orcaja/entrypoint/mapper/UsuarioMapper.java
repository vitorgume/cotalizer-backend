package com.gumeinteligenciacomercial.orcaja.entrypoint.mapper;

import com.gumeinteligenciacomercial.orcaja.domain.Usuario;
import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;

public class UsuarioMapper {

    public static UsuarioDto paraDto(Usuario domain) {
        return UsuarioDto.builder()
                .id(domain.getId())
                .cpf(domain.getCpf())
                .email(domain.getEmail())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .status(domain.getStatus())
                .plano(domain.getPlano())
                .idAssinatura(domain.getIdCustomer())
                .idCustomer(domain.getIdCustomer())
                .build();
    }

    public static Usuario paraDomain(UsuarioDto dto) {
        return Usuario.builder()
                .id(dto.getId())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .senha(dto.getSenha())
                .status(dto.getStatus())
                .plano(dto.getPlano())
                .idCustomer(dto.getIdAssinatura())
                .idAssinatura(dto.getIdAssinatura())
                .build();
    }
}
