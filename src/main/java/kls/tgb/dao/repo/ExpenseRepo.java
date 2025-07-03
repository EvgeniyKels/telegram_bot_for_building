package kls.tgb.dao.repo;

import kls.tgb.dao.entities.ExpenseEntity;
import org.springframework.data.repository.CrudRepository;

public interface ExpenseRepo extends CrudRepository<ExpenseEntity, Long> {
}
