package com.gumeinteligenciacomercial.orcaja.infrastructure.repositories;

import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PromptEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromptRepository extends MongoRepository<PromptEntity, String> {
    Optional<PromptEntity> findByIdAndAtivoTrue(String idPrompt);
}
