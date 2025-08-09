package kls.tgb.dto;

import kls.tgb.dto.sm.AddExpenseState;
import lombok.Data;

@Data
public class ExpenseDto {
    private Long id;
    private Long telegramId;
    private AddExpenseState state;
    private Long activeProjectId;

}
