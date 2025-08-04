package kls.tgb.service;

import kls.tgb.dto.ExpenseDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.sm.AddExpenseState;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.StartCommandState;
import kls.tgb.exception.StateMachineException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AddExpenseStateMachineImpl implements StateMachine<ExpenseDto, AddExpenseState> {

    private final DbService dbService;
    private final AddExpenseStateMachineService addExpenseStateMachineService;

    @Override
    public MessageButtonHolder handleStartCommandStates(Long userTgId, ExpenseDto dto) {
        AddExpenseState state = dto.getState();
        log.debug("User {}: handling state transition from {}", userTgId, state);
        MessageButtonHolder messageButtonHolder;
        try {
            messageButtonHolder = switch (state) {
                case A -> addExpenseStateMachineService.handleInitialState(userTgId, dto);
                default -> {
                    final var errorMessage = "Unknown state: " + state;
                    log.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
            };
            log.info("User {}: successful transition {} -> {}", userTgId, state, dto.getState());
            return messageButtonHolder;
        } catch (Exception e) {
            log.error("User {}: failed transition from {} with error: {}", userTgId, state, e.getMessage(), e);
            throw new StateMachineException(dto.getChatId(), state.name(), userTgId);
        }

    }

    @Override
    public AddExpenseState getUserState(Long telegramId) {
        StateDto stateByTgID = dbService.getStateByTgID(telegramId);
        return AddExpenseState.valueOf(stateByTgID.getState());
    }

    @Override
    public void removeUserState(Long telegramId) {
        dbService.removeState(telegramId);
    }

}
