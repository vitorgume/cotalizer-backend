package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories;

import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanoRepository extends MongoRepository<PlanoEntity, String> {
    Optional<PlanoEntity> findByPadraoTrue();
}
