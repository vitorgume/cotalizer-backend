package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities;

import com.gumeinteligenciacomercial.orcaja.entrypoint.dto.UsuarioDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "Orcamento")
@Table(name = "orcamentos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrcamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_orcamento")
    private UUID id;
    private String conteudo;
    private LocalDate dataCriacao;
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;
}
