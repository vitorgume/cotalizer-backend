package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories;

import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoRepository extends MongoRepository<OrcamentoEntity, String> {
    Page<OrcamentoEntity> findByIdUsuario(String idUsuario, Pageable pageable);
}
