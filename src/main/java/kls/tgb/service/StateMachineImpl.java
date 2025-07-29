package kls.tgb.service;

import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.State;
import kls.tgb.exception.StateMachineException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StateMachineImpl implements StateMachine {

    private final DbService dbService;
    private final StartStateMachineService startStateMachineService;

    @Override
    public MessageButtonHolder handleStartCommandStates(@NonNull final Long userTgId, @NonNull final UserDto userDto) {
        final var state = userDto.getState();
        log.debug("User {}: handling state transition from {}", userTgId, state);
        MessageButtonHolder messageButtonHolder;
        try {
            messageButtonHolder = switch (state) {
                case STATE_NOT_EXISTS -> startStateMachineService.handleInitialState(userTgId, userDto);
                case WAITING_FOR_NAME -> startStateMachineService.createUserWithCustomName(userTgId, userDto);
                case USER_EXISTS, NEW_USER_REGISTERED ->
                        startStateMachineService.showProjectsOrCreateNew(userTgId, userDto);
                case PROJECT_NOT_EXISTS -> startStateMachineService.createNewBlankProject(userTgId, userDto);
                case BLANK_PROJECT_CREATED -> startStateMachineService.updateProjectName(userTgId, userDto);
                case PROJECT_EXISTS, PROJECT_CREATED -> startStateMachineService.handleFinalStartStatus();
                default -> {
                    final var errorMessage = "Unknown state: " + state;
                    log.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
            };
            log.info("User {}: successful transition {} -> {}", userTgId, state, userDto.getState());
            return messageButtonHolder;
        } catch (Exception e) {
            log.error("User {}: failed transition from {} with error: {}", userTgId, state, e.getMessage(), e);
            throw new StateMachineException(userDto.getChatId(), state, userTgId);
        }

    }

    @Override
    public State getUserState(@NonNull final Long telegramId) {
        return dbService.getStateByTgID(telegramId).getState();
    }

    @Override
    public void removeUserState(@NonNull final Long telegramId) {
        dbService.removeState(telegramId);
    }
}
