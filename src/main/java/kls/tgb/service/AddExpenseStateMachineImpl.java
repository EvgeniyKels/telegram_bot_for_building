package kls.tgb.service;

import kls.tgb.dto.TgUserChatDto;
import kls.tgb.dto.sm.AddExpenseState;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.exception.StateMachineException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static kls.tgb.dto.sm.AddExpenseState.CREATE_EXPENSE;
import static kls.tgb.util.StringConstants.EXPENSE_COMMAND_SERVICE;
import static kls.tgb.util.StringConstants.EXPENSE_COMMAND_SM;

@Slf4j
@Service
@Qualifier(EXPENSE_COMMAND_SM)
public class AddExpenseStateMachineImpl implements StateMachine {

    private final DbService dbService;
    @Qualifier(EXPENSE_COMMAND_SERVICE)
    private final AddExpenseStateMachineService addExpenseStateMachineService;

    public AddExpenseStateMachineImpl(DbService dbService, AddExpenseStateMachineService addExpenseStateMachineService) {
        this.dbService = dbService;
        this.addExpenseStateMachineService = addExpenseStateMachineService;
    }

    @Override
    public MessageButtonHolder handleCommandStates(TgUserChatDto dto) {
        final var userTgId = dto.telegramID();
        final var stateByTgID = dbService.getStateByTgID(userTgId);

        if(Arrays.stream(AddExpenseState.values()).map(AddExpenseState::name).noneMatch(x -> x.equals(stateByTgID.getState()))) {
            stateByTgID.setState(CREATE_EXPENSE.name());
        }
        AddExpenseState state = AddExpenseState.valueOf(stateByTgID.getState());
        log.debug("User {}: handling state transition from {}", userTgId, state);
        MessageButtonHolder messageButtonHolder;
        try {
            messageButtonHolder = switch (state) {
                case CREATE_EXPENSE -> addExpenseStateMachineService.handleInitialState(stateByTgID);
                case SET_EXPENSE_NAME -> addExpenseStateMachineService.setExpenseName(stateByTgID, dto.userInput());
                case SET_EXPENSE_AMOUNT -> addExpenseStateMachineService.setExpenseAmount(stateByTgID, Long.parseLong(dto.userInput()));
                default -> {
                    final var errorMessage = "Unknown state: " + state;
                    log.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
            };
            return messageButtonHolder;
        } catch (Exception e) {
            log.error("User {}: failed transition from {} with error: {}", userTgId, state, e.getMessage(), e);
            throw new StateMachineException(dto.chatID(), state.name(), userTgId);
        }

    }

}
