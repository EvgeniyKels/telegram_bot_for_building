package kls.tgb.service;

import kls.tgb.dto.TgUserChatDto;
import kls.tgb.dto.sm.AddExpenseState;
import kls.tgb.dto.sm.AllExpensesState;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.exception.StateMachineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static kls.tgb.dto.sm.AllExpensesState.CHOOSE_PROJECT;
import static kls.tgb.util.StringConstants.*;

@Slf4j
@Service
@Qualifier(ALL_EXPENSES_COMMAND_SM)
public class GetAllExpensesStateMachineImpl implements StateMachine {

    private final DbService dbService;

    @Qualifier(ALL_EXPENSES_COMMAND_SERVICE)
    private final AllExpenseStateMachineService allExpensesStateMachineService;

    public GetAllExpensesStateMachineImpl(DbService dbService, AllExpenseStateMachineService allExpensesStateMachineService) {
        this.dbService = dbService;
        this.allExpensesStateMachineService = allExpensesStateMachineService;
    }

    @Override
    public MessageButtonHolder handleCommandStates(TgUserChatDto dto) {
        final var userTgId = dto.telegramID();
        final var stateByTgID = dbService.getStateByTgID(userTgId);

        if(Arrays.stream(AllExpensesState.values()).map(AllExpensesState::name).noneMatch(x -> x.equals(stateByTgID.getState()))) {
            stateByTgID.setState(CHOOSE_PROJECT.name());
        }
        AllExpensesState state = AllExpensesState.valueOf(stateByTgID.getState());
        log.debug("User {}: handling state transition from {}", userTgId, state);
        MessageButtonHolder messageButtonHolder;
        try {
            messageButtonHolder = switch (state) {
                case CHOOSE_PROJECT -> allExpensesStateMachineService.handleInitialState(stateByTgID);
                case MARKDOWN_READY -> allExpensesStateMachineService.prepareMarkdown(stateByTgID, dto.userInput());
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
