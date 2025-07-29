package kls.tgb.dao.repo;

import kls.tgb.dao.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByTelegramId(Long telegramId);

    boolean existsByTelegramId(Long userTgId);
}