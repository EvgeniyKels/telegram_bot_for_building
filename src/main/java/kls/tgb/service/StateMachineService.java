package kls.tgb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.Actions;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.State;
import kls.tgb.exception.StateMachineException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kls.tgb.dto.sm.Actions.*;
import static kls.tgb.dto.sm.Actions.DONT_SEE_PROJECT;
import static kls.tgb.dto.sm.Actions.LETS_OPEN_PROJECT;
import static kls.tgb.dto.sm.Actions.OPEN_EXIST_PROJECT;
import static kls.tgb.dto.sm.State.*;
import static kls.tgb.util.StringConstants.*;
import static kls.tgb.util.StringConstants.LETS_SEE_PROJECTS;

@Slf4j
@Component
@AllArgsConstructor
class StartStateMachineService {

    private final DbService dbService;

    MessageButtonHolder showProjectsOrCreateNew(@NonNull Long userTgId, @NonNull UserDto userDto) {
        State oldState = userDto.getState();
        if (Actions.LETS_SEE_PROJECTS == userDto.getUserAction()) {
            // получаем данные о проектах или предлагаем создать новый
            List<ConstructionProjectDto> allUserProjects = dbService.getAllUserProjects(userTgId);
            if (allUserProjects.isEmpty()) {
                userDto.setState(dbService.setState(userTgId, PROJECT_NOT_EXISTS));
                logUserState(userTgId, userDto, oldState);
                return new MessageButtonHolder(
                        YOU_HAVE_NOT_PROJECTS,
                        Map.of(YES, LETS_OPEN_PROJECT, NO, DONT_OPEN_PROJECT));
            } else {
                userDto.setState(dbService.setState(userTgId, PROJECT_EXISTS));
                logUserState(userTgId, userDto, oldState);
                return getMessageButtonHolderWithAllProjects(allUserProjects);
            }
        } else if (DONT_SEE_PROJECT == userDto.getUserAction()) {
            dbService.removeState(userTgId);
            return new MessageButtonHolder(BYE_MESSAGE, null);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private MessageButtonHolder getMessageButtonHolderWithAllProjects(List<ConstructionProjectDto> allUserProjects) {
        return new MessageButtonHolder(
                ITS_YOU_PROJECTS,
                allUserProjects.stream().map(ConstructionProjectDto::name).collect(Collectors.toMap(s -> s, s -> OPEN_EXIST_PROJECT)));
    }

    MessageButtonHolder updateProjectName(@NonNull Long userTgId, @NonNull UserDto userDto) {
        State oldState = userDto.getState();
        StateDto stateByTgID = dbService.getStateByTgID(userTgId);
        if (null == stateByTgID.getData() || stateByTgID.getData().length == 0) {
            throw new IllegalArgumentException();
        }
        dbService.updateProjectName(userTgId, Long.valueOf(new String(stateByTgID.getData())), userDto);
        userDto.setState(dbService.setState(userTgId, PROJECT_CREATED));
        logUserState(userTgId, userDto, oldState);
        List<ConstructionProjectDto> allUserProjects = dbService.getAllUserProjects(userTgId);
        return getMessageButtonHolderWithAllProjects(allUserProjects);
    }

    MessageButtonHolder createNewBlankProject(@NonNull Long userTgId, @NonNull UserDto userDto) {
        State oldState = userDto.getState();
        if (LETS_OPEN_PROJECT == userDto.getUserAction()) {
            Long newProjectId = dbService.createNewProject(userDto);
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] bytes;
            try {
                bytes = objectMapper.writeValueAsBytes(newProjectId);
            } catch (JsonProcessingException e) {
                throw new StateMachineException(userDto.getChatId(), BLANK_PROJECT_CREATED, userTgId);
            }
            userDto.setState(dbService.setState(userTgId, BLANK_PROJECT_CREATED, bytes));
            logUserState(userTgId, userDto, oldState);
            return new MessageButtonHolder(ENTER_PROJECT_NAME, null);
        } else {
            dbService.removeState(userTgId);
            return new MessageButtonHolder(BYE_MESSAGE, null);
        }
    }

    private static void logUserState(Long userTgId, UserDto userDto, State oldState) {
        log.info("User {} moved to state {} from {}", userTgId, userDto.getState(), oldState);
    }

    MessageButtonHolder createUserWithCustomName(@NonNull Long userTgId, @NonNull UserDto userDto) {
        State oldState = userDto.getState();
        final var userDtoAfterSaveUpdate = dbService.getOrCreateUser(userTgId, userDto);
        if (Boolean.TRUE.equals(userDtoAfterSaveUpdate.getIsNewUser())) {
            userDto.setState(dbService.setState(userTgId, NEW_USER_REGISTERED));
            logUserState(userTgId, userDto, oldState);
            return new MessageButtonHolder(
                    GREETINGS.concat(userDtoAfterSaveUpdate.getSelfUserName()).concat(LETS_SEE_PROJECTS),
                    Map.of(YES, Actions.LETS_SEE_PROJECTS, NO, DONT_SEE_PROJECT));
        } else {
            throw new IllegalStateException(CHANGE_NAME_RESTRICTION);
        }
    }

    MessageButtonHolder handleInitialState(@NonNull Long userTgId, @NonNull UserDto userDto) {
        State oldState = userDto.getState();
        if (dbService.isUserExists(userTgId)) {
            userDto.setState(dbService.setState(userTgId, USER_EXISTS));
            logUserState(userTgId, userDto, oldState);
            UserDto userFromDb = dbService.getOrCreateUser(userTgId, userDto);
            return new MessageButtonHolder(
                    GREETINGS.concat(userFromDb.getSelfUserName()).concat(LETS_SEE_PROJECTS),
                    Map.of(YES, Actions.LETS_SEE_PROJECTS, NO, DONT_SEE_PROJECT));
        } else {
            userDto.setState(dbService.setState(userTgId, WAITING_FOR_NAME));
            logUserState(userTgId, userDto, oldState);
            return new MessageButtonHolder(WHAT_IS_YOUR_NAME, null);
        }
    }

    MessageButtonHolder handleFinalStartStatus() {
        return new MessageButtonHolder(INPUT_EXCHANGES, null);
    }
}
