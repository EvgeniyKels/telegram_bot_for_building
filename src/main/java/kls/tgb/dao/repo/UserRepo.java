package kls.tgb.dao.repo;

import kls.tgb.dao.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<UserEntity, Long> {
}