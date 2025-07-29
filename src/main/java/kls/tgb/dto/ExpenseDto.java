package kls.tgb.dto;

import kls.tgb.dto.sm.AddExpenseState;
import lombok.Data;

@Data
public class ExpenseDto {
    private final long id;
    private final Long chatId;
    private final AddExpenseState state;
}
