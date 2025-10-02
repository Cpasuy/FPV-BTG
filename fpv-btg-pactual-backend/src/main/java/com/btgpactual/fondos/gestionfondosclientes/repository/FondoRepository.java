package com.btgpactual.fondos.gestionfondosclientes.repository;

import com.btgpactual.fondos.gestionfondosclientes.model.Fondo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FondoRepository extends MongoRepository<Fondo, String> {
}
