package kls.tgb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.Actions;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.StartCommandState;
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
import static kls.tgb.dto.sm.StartCommandState.*;
import static kls.tgb.util.StringConstants.*;
import static kls.tgb.util.StringConstants.LETS_SEE_PROJECTS;

@Slf4j
@Component
@AllArgsConstructor
class StartStateMachineService {

    private final DbService dbService;

    MessageButtonHolder showProjectsOrCreateNew(@NonNull Long userTgId, @NonNull UserDto userDto) {
        StartCommandState oldState = userDto.getState();
        if (Actions.LETS_SEE_PROJECTS == userDto.getUserAction()) {
            // получаем данные о проектах или предлагаем создать новый
            List<ConstructionProjectDto> allUserProjects = dbService.getAllUserProjects(userTgId);
            if (allUserProjects.isEmpty()) {
                updateStateInDbAndSetToDto(PROJECT_NOT_EXISTS, userTgId, userDto);
                logUserState(userTgId, userDto, oldState);
                return new MessageButtonHolder(
                        YOU_HAVE_NOT_PROJECTS,
                        Map.of(YES, LETS_OPEN_PROJECT, NO, DONT_OPEN_PROJECT));
            } else {
                updateStateInDbAndSetToDto(PROJECT_EXISTS, userTgId, userDto);
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
        StartCommandState oldState = userDto.getState();
        StateDto state = dbService.getStateByTgID(userTgId);
        dbService.updateProjectName(userTgId, Long.valueOf(new String(state.getData())), userDto);
        updateStateInDbAndSetToDto(PROJECT_CREATED, userTgId, userDto);
        logUserState(userTgId, userDto, oldState);
        List<ConstructionProjectDto> allUserProjects = dbService.getAllUserProjects(userTgId);
        return getMessageButtonHolderWithAllProjects(allUserProjects);
    }

    private void updateStateInDbAndSetToDto(StartCommandState state, Long userTgId, UserDto userDto) {
        dbService.setState(userTgId, state.name(), new byte[0]);
        userDto.setState(state);
    }


    private void updateStateInDbAndSetToDto(StartCommandState state, Long userTgId, UserDto userDto, byte[] bytes) {
        dbService.setState(userTgId, state.name(), bytes);
        userDto.setState(state);
    }

    MessageButtonHolder createNewBlankProject(@NonNull Long userTgId, @NonNull UserDto userDto) {
        StartCommandState oldState = userDto.getState();
        if (LETS_OPEN_PROJECT == userDto.getUserAction()) {
            Long newProjectId = dbService.createNewProject(userDto);
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] bytes;
            try {
                bytes = objectMapper.writeValueAsBytes(newProjectId);
            } catch (JsonProcessingException e) {
                throw new StateMachineException(userDto.getChatId(), BLANK_PROJECT_CREATED.name(), userTgId);
            }
            updateStateInDbAndSetToDto(BLANK_PROJECT_CREATED, userTgId, userDto, bytes);
            logUserState(userTgId, userDto, oldState);
            return new MessageButtonHolder(ENTER_PROJECT_NAME, null);
        } else {
            dbService.removeState(userTgId);
            return new MessageButtonHolder(BYE_MESSAGE, null);
        }
    }

    private static void logUserState(Long userTgId, UserDto userDto, StartCommandState oldState) {
        log.info("User {} moved to state {} from {}", userTgId, userDto.getState(), oldState);
    }

    MessageButtonHolder createUserWithCustomName(@NonNull Long userTgId, @NonNull UserDto userDto) {
        StartCommandState oldState = userDto.getState();
        final var userDtoAfterSaveUpdate = dbService.getOrCreateUser(userTgId, userDto);
        if (Boolean.TRUE.equals(userDtoAfterSaveUpdate.getIsNewUser())) {
            updateStateInDbAndSetToDto(NEW_USER_REGISTERED, userTgId, userDto);
            logUserState(userTgId, userDto, oldState);
            return new MessageButtonHolder(
                    GREETINGS.concat(userDtoAfterSaveUpdate.getSelfUserName()).concat(LETS_SEE_PROJECTS),
                    Map.of(YES, Actions.LETS_SEE_PROJECTS, NO, DONT_SEE_PROJECT));
        } else {
            throw new IllegalStateException(CHANGE_NAME_RESTRICTION);
        }
    }

    MessageButtonHolder handleInitialState(@NonNull Long userTgId, @NonNull UserDto userDto) {
        StartCommandState oldState = userDto.getState();
        if (dbService.isUserExists(userTgId)) {
            updateStateInDbAndSetToDto(USER_EXISTS, userTgId, userDto);
            logUserState(userTgId, userDto, oldState);
            UserDto userFromDb = dbService.getOrCreateUser(userTgId, userDto);
            return new MessageButtonHolder(
                    GREETINGS.concat(userFromDb.getSelfUserName()).concat(LETS_SEE_PROJECTS),
                    Map.of(YES, Actions.LETS_SEE_PROJECTS, NO, DONT_SEE_PROJECT));
        } else {
            updateStateInDbAndSetToDto(WAITING_FOR_NAME, userTgId, userDto);
            logUserState(userTgId, userDto, oldState);
            return new MessageButtonHolder(WHAT_IS_YOUR_NAME, null);
        }
    }

    MessageButtonHolder handleFinalStartStatus() {
        return new MessageButtonHolder(INPUT_EXCHANGES, null);
    }
}
