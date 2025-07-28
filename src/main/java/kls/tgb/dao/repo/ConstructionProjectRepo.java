package kls.tgb.dao.repo;

import kls.tgb.dao.entities.ConstructionProjectEntity;
import kls.tgb.dao.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ConstructionProjectRepo extends CrudRepository<ConstructionProjectEntity, Long> {
    Collection<ConstructionProjectEntity> findConstructionProjectEntitiesByUser(UserEntity userEntity);
}
