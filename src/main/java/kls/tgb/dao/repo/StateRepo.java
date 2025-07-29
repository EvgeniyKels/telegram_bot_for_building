package kls.tgb.dao.repo;

import kls.tgb.dao.entities.StateEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StateRepo  extends CrudRepository<StateEntity, Long> {
    Optional<StateEntity> findByTelegramId(Long telegramId);

    void deleteByTelegramId(Long userTgId);
}
