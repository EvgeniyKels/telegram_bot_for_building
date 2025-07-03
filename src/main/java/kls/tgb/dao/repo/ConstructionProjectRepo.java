package kls.tgb.dao.repo;

import kls.tgb.dao.entities.ConstructionProjectEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstructionProjectRepo extends CrudRepository<ConstructionProjectEntity, Long> {
}
