package kls.tgb.service;

import kls.tgb.dto.TgUserChatDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.StartCommandState;
import kls.tgb.exception.StateMachineException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static kls.tgb.util.StringConstants.*;

@Slf4j
@Service
@Qualifier(START_COMMAND_SM)
public class StartStateMachineImpl implements StateMachine {

    private final DbService dbService;

    @Qualifier(START_COMMAND_SERVICE)
    private final StartStateMachineService startStateMachineService;

    public StartStateMachineImpl(DbService dbService, StartStateMachineService startStateMachineService) {
        this.dbService = dbService;
        this.startStateMachineService = startStateMachineService;
    }

    @Override
    public MessageButtonHolder handleCommandStates(@NonNull TgUserChatDto dto) {
        final var userTgId = dto.telegramID();
        final var stateByTgID = dbService.getStateByTgID(userTgId);
        final var state = StartCommandState.valueOf(stateByTgID.getState());
        log.debug("User {}: handling state transition from {}", userTgId, state);
        MessageButtonHolder messageButtonHolder;
        try {
            messageButtonHolder = switch (state) {
                case STATE_NOT_EXISTS -> startStateMachineService.handleInitialState(stateByTgID, dto.username());
                case WAITING_FOR_NAME -> startStateMachineService.createUserWithCustomName(stateByTgID, dto.username(), dto.userInput());
                case USER_EXISTS, NEW_USER_REGISTERED ->
                        startStateMachineService.showProjectsOrCreateNew(stateByTgID, dto.userAction());
                case PROJECT_NOT_EXISTS -> startStateMachineService.createNewBlankProject(stateByTgID, dto.userAction());
                case BLANK_PROJECT_CREATED -> startStateMachineService.updateProjectName(stateByTgID, dto.userInput());
                case PROJECT_EXISTS, PROJECT_CREATED -> startStateMachineService.handleFinalStartStatus(stateByTgID);
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
