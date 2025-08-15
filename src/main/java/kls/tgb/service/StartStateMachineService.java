package kls.tgb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.UserAction;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.StartCommandState;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kls.tgb.dto.sm.UserAction.*;
import static kls.tgb.dto.sm.UserAction.DONT_SEE_PROJECT;
import static kls.tgb.dto.sm.UserAction.LETS_OPEN_PROJECT;
import static kls.tgb.dto.sm.UserAction.OPEN_EXIST_PROJECT;
import static kls.tgb.dto.sm.StartCommandState.*;
import static kls.tgb.util.SerializeUtil.convertByteArrayToObject;
import static kls.tgb.util.SerializeUtil.convertObjectToByteArray;
import static kls.tgb.util.StringConstants.*;
import static kls.tgb.util.StringConstants.LETS_SEE_PROJECTS;

@Slf4j
@Component
@AllArgsConstructor
@Qualifier(START_COMMAND_SERVICE)
class StartStateMachineService {

    private final DbService dbService;

    MessageButtonHolder showProjectsOrCreateNew(StateDto stateByTgID, UserAction action) {
        StartCommandState oldState = StartCommandState.valueOf(stateByTgID.getState());
        UserDto userDto = convertByteArrayToObject(stateByTgID.getData(), UserDto.class);
        final var userTgId = stateByTgID.getTelegramId();
        if (UserAction.LETS_SEE_PROJECTS == action) {
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

    MessageButtonHolder updateProjectName(StateDto stateByTgID, String projectName) {
        StartCommandState oldState = StartCommandState.valueOf(stateByTgID.getState());
        UserDto userDto = convertByteArrayToObject(stateByTgID.getData(), UserDto.class);
        userDto.setActiveProjectName(projectName);
        dbService.updateProjectName(userDto.getActiveProjectId(), userDto);
        userDto.setActiveProjectId(userDto.getActiveProjectId());
        updateStateInDbAndSetToDto(PROJECT_CREATED, userDto.getTelegramId(), userDto);
        logUserState(userDto.getTelegramId(), userDto, oldState);
        List<ConstructionProjectDto> allUserProjects = dbService.getAllUserProjects(userDto.getTelegramId());
        return getMessageButtonHolderWithAllProjects(allUserProjects);
    }


    private void updateStateInDbAndSetToDto(StartCommandState state, Long userTgId, UserDto userDto) {
        userDto.setState(state);
        dbService.setState(userTgId, state.name(), convertObjectToByteArray(userDto));
    }

    MessageButtonHolder createNewBlankProject(StateDto stateByTgID, UserAction action) {
        StartCommandState oldState = StartCommandState.valueOf(stateByTgID.getState());
        UserDto userDto = convertByteArrayToObject(stateByTgID.getData(), UserDto.class);
        userDto.setUserAction(action);
        if (LETS_OPEN_PROJECT == userDto.getUserAction()) {
            Long newProjectId = dbService.createNewProject(userDto);
            userDto.setActiveProjectId(newProjectId);
            updateStateInDbAndSetToDto(BLANK_PROJECT_CREATED, stateByTgID.getTelegramId(), userDto);
            logUserState(stateByTgID.getTelegramId(), userDto, oldState);
            return new MessageButtonHolder(ENTER_PROJECT_NAME, null);
        } else {
            dbService.removeState(stateByTgID.getTelegramId());
            return new MessageButtonHolder(BYE_MESSAGE, null);
        }
    }

    private static void logUserState(Long userTgId, UserDto userDto, StartCommandState oldState) {
        log.info("User {} moved to state {} from {}", userTgId, userDto.getState(), oldState);
    }

    MessageButtonHolder createUserWithCustomName(@NonNull StateDto stateDto, @NonNull String userName, @NonNull String userInput) {
        StartCommandState oldState = StartCommandState.valueOf(stateDto.getState());
        final var userDtoAfterSaveUpdate = dbService.getOrCreateUser(stateDto.getTelegramId(), userName, userInput);
        UserDto userDto = convertByteArrayToObject(stateDto.getData(), UserDto.class);
        userDto.setUsername(userName);
        userDto.setSelfUserName(userInput);
        if (Boolean.TRUE.equals(userDtoAfterSaveUpdate.getIsNewUser())) {
            updateStateInDbAndSetToDto(NEW_USER_REGISTERED, stateDto.getTelegramId(), userDto);
            logUserState(stateDto.getTelegramId(), userDto, oldState);
            return new MessageButtonHolder(
                    GREETINGS.concat(userDtoAfterSaveUpdate.getSelfUserName()).concat(LETS_SEE_PROJECTS),
                    Map.of(YES, UserAction.LETS_SEE_PROJECTS, NO, DONT_SEE_PROJECT));
        } else {
            throw new IllegalStateException(CHANGE_NAME_RESTRICTION);
        }
    }

    MessageButtonHolder handleInitialState(@NonNull StateDto stateDto, @NonNull String userName) {
        byte[] data = stateDto.getData();
        final var userDto = convertByteArrayToObject(data, UserDto.class);
        final var oldState = userDto.getState();
        final var userTgId = userDto.getTelegramId();
        if (dbService.isUserExists(userTgId)) {
            updateStateInDbAndSetToDto(USER_EXISTS, userTgId, userDto);
            logUserState(userTgId, userDto, oldState);
            UserDto userFromDb = dbService.getOrCreateUser(userTgId, userName, null);
            return new MessageButtonHolder(
                    GREETINGS.concat(userFromDb.getSelfUserName()).concat(LETS_SEE_PROJECTS),
                    Map.of(YES, UserAction.LETS_SEE_PROJECTS, NO, DONT_SEE_PROJECT));
        } else {
            updateStateInDbAndSetToDto(WAITING_FOR_NAME, userTgId, userDto);
            logUserState(userTgId, userDto, oldState);
            return new MessageButtonHolder(WHAT_IS_YOUR_NAME, null);
        }
    }

    MessageButtonHolder handleFinalStartStatus(@NonNull StateDto stateByTgID) {
        StartCommandState oldState = StartCommandState.valueOf(stateByTgID.getState());
        UserDto userDto = convertByteArrayToObject(stateByTgID.getData(), UserDto.class);
        updateStateInDbAndSetToDto(START_COMMAND_FINISHED, userDto.getTelegramId(), userDto);
        logUserState(userDto.getTelegramId(), userDto, oldState);
        return new MessageButtonHolder(
                userDto.getUsername() + ", ты выбрал проект " +
                        userDto.getActiveProjectName() +
                        ". Для ввода расходов по проекту используй команду /" + ADD_EXPENSE, null);
    }
}
