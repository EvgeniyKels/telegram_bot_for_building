package kls.tgb.dao.repo;

import kls.tgb.dao.entities.ExpenseCategoryEntity;
import org.springframework.data.repository.CrudRepository;

public interface ExpenseCategoryRepo extends CrudRepository<ExpenseCategoryEntity, Long> {
}
