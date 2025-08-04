package kls.tgb.service;

import kls.tgb.dto.ExpenseDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddExpenseStateMachineService {
    public MessageButtonHolder handleInitialState(Long userTgId, ExpenseDto dto) {
        return null;
    }

    public MessageButtonHolder setExpenseName(Long userTgId, ExpenseDto dto) {
        return null;
    }

    public MessageButtonHolder setExpenseAmount(Long userTgId, ExpenseDto dto) {
        return null;
    }
}
