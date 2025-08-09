package kls.tgb.service;

import kls.tgb.dto.ExpenseDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.AddExpenseState;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.exception.UserNotExistsException;
import kls.tgb.util.SerializeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static kls.tgb.dto.sm.AddExpenseState.SET_EXPENSE_AMOUNT;
import static kls.tgb.dto.sm.AddExpenseState.SET_EXPENSE_NAME;
import static kls.tgb.util.SerializeUtil.convertObjectToByteArray;
import static kls.tgb.util.StringConstants.*;

@Slf4j
@Service
@AllArgsConstructor
@Qualifier(EXPENSE_COMMAND_SERVICE)
public class AddExpenseStateMachineService {

    private final DbService dbService;

    public MessageButtonHolder handleInitialState(StateDto stateByTgID) {
        if (dbService.isUserExists(stateByTgID.getTelegramId())) {
            UserDto userDto = SerializeUtil.convertByteArrayToObject(stateByTgID.getData(), UserDto.class);
            ExpenseDto expenseDto = new ExpenseDto();
            expenseDto.setTelegramId(userDto.getTelegramId());
            expenseDto.setActiveProjectId(userDto.getActiveProjectId());
            ExpenseDto enrichedDto = dbService.createNewExpense(expenseDto);
            updateStateInDbAndSetToDto(SET_EXPENSE_NAME, stateByTgID.getTelegramId(), enrichedDto);
            return new MessageButtonHolder(ENTER_EXPENSE_NAME, null);
        }
        throw new UserNotExistsException("tg user ".concat(stateByTgID.getTelegramId().toString()).concat(" not found"));
    }

    private void updateStateInDbAndSetToDto(AddExpenseState state, Long userTgId, ExpenseDto expenseDto) {
        expenseDto.setState(state);
        dbService.setState(userTgId, state.name(), convertObjectToByteArray(userTgId, expenseDto));
    }

    public MessageButtonHolder setExpenseName(StateDto stateByTgID, String expenseDescription) {
        ExpenseDto expenseDto = SerializeUtil.convertByteArrayToObject(stateByTgID.getData(), ExpenseDto.class);
        dbService.updateExpenseDescription(expenseDto.getId(), expenseDescription);
        updateStateInDbAndSetToDto(SET_EXPENSE_AMOUNT, stateByTgID.getTelegramId(), expenseDto);
        return new MessageButtonHolder(ENTER_EXPENSE_AMOUNT, null);
    }

    public MessageButtonHolder setExpenseAmount(StateDto stateByTgID, Long expenseAmount) {
        ExpenseDto expenseDto = SerializeUtil.convertByteArrayToObject(stateByTgID.getData(), ExpenseDto.class);
        dbService.updateExpenseAmount(expenseDto.getId(), expenseAmount);
        updateStateInDbAndSetToDto(SET_EXPENSE_AMOUNT, stateByTgID.getTelegramId(), expenseDto); //TODO
        return new MessageButtonHolder("расход " + expenseAmount + " внесен", null);
    }
}
