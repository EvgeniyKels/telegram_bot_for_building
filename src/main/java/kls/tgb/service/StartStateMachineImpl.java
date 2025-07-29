package kls.tgb.service;

import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.StartCommandState;
import kls.tgb.exception.StateMachineException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StartStateMachineImpl implements StateMachine<UserDto> {

    private final DbService dbService;
    private final StartStateMachineService startStateMachineService;

    @Override
    public MessageButtonHolder handleStartCommandStates(@NonNull final Long userTgId, @NonNull final UserDto dto) {
        final var state = dto.getState();
        log.debug("User {}: handling state transition from {}", userTgId, state);
        MessageButtonHolder messageButtonHolder;
        try {
            messageButtonHolder = switch (state) {
                case STATE_NOT_EXISTS -> startStateMachineService.handleInitialState(userTgId, dto);
                case WAITING_FOR_NAME -> startStateMachineService.createUserWithCustomName(userTgId, dto);
                case USER_EXISTS, NEW_USER_REGISTERED ->
                        startStateMachineService.showProjectsOrCreateNew(userTgId, dto);
                case PROJECT_NOT_EXISTS -> startStateMachineService.createNewBlankProject(userTgId, dto);
                case BLANK_PROJECT_CREATED -> startStateMachineService.updateProjectName(userTgId, dto);
                case PROJECT_EXISTS, PROJECT_CREATED -> startStateMachineService.handleFinalStartStatus();
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
    public StartCommandState getUserState(@NonNull final Long telegramId) {
        return dbService.getStateByTgID(telegramId).getState();
    }

    @Override
    public void removeUserState(@NonNull final Long telegramId) {
        dbService.removeState(telegramId);
    }
}
