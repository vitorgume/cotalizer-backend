package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories;

import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.OrcamentoTradicionalEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoTradicionalRepository extends MongoRepository<OrcamentoTradicionalEntity, String> {
}
