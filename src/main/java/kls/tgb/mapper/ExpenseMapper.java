package kls.tgb.mapper;

import kls.tgb.dao.entities.ExpenseEntity;
import kls.tgb.dto.ExpenseDto;
import org.mapstruct.Mapper;
import org.telegram.telegrambots.meta.api.objects.User;

@Mapper(componentModel = "spring")
public abstract class ExpenseMapper {

    public abstract ExpenseDto fromTgUserToExpenseDto(User telegramUser);

    public abstract ExpenseEntity fromExpenseDtoToExpenseEntity(ExpenseDto expenseDto);
}
