package kls.tgb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.State;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static kls.tgb.dto.sm.Actions.*;
import static kls.tgb.dto.sm.State.*;

@Service
@AllArgsConstructor
public class StateMachineImpl /* implements StateMachine */ {

    private final DbService dbService;

    //TODO приветственное сообщение получать из конфига
    public MessageButtonHolder handleState(final Long userTgId, final UserDto userDto) {
        final var state = userDto.getState();
        switch (state) {
            case STATE_NOT_EXISTS -> {
                if (dbService.isUserExists(userTgId)) {
                    userDto.setState(dbService.setState(userTgId, USER_EXISTS));
                    UserDto userFromDb = dbService.getOrCreateUser(userTgId, userDto);
                    return new MessageButtonHolder(
                            "привет, ".concat(userFromDb.getSelfUserName()).concat(" Перейдем к проектам ? "),
                            Map.of("Да", LETS_SEE_PROJECTS, "Нет", DONT_SEE_PROJECT));
                } else {
                    userDto.setState(dbService.setState(userTgId, WAITING_FOR_NAME));
                    return new MessageButtonHolder("приветик, как тебя звать ?", null);
                }
            }
            case WAITING_FOR_NAME -> {
                final var userDtoAfterSaveUpdate = dbService.getOrCreateUser(userTgId, userDto);
                if (Boolean.TRUE.equals(userDtoAfterSaveUpdate.getIsNewUser())) {
                    userDto.setState(dbService.setState(userTgId, NEW_USER_REGISTERED));
                    return new MessageButtonHolder(
                            "приятно познакомиться, ".concat(userDtoAfterSaveUpdate.getSelfUserName()).concat(" Поздравляю с регистрацией. Перейдем к проектам ? "),
                            Map.of("Да", LETS_SEE_PROJECTS, "Нет", DONT_SEE_PROJECT));
                } else {
                    throw new IllegalStateException();
                }

            }
            case USER_EXISTS, NEW_USER_REGISTERED -> {
                if (LETS_SEE_PROJECTS == userDto.getUserAction()) {
                    // получаем данные о проектах или предлагаем создать новый
                    List<ConstructionProjectDto> allUserProjects = dbService.getAllUserProjects(userTgId);
                    if (allUserProjects.isEmpty()) {
                        userDto.setState(dbService.setState(userTgId, PROJECT_NOT_EXISTS));
                        return new MessageButtonHolder(
                                "У вас нет проектов. Хотите создать новый ?",
                                Map.of("Да", LETS_OPEN_PROJECT, "Нет", DONT_OPEN_PROJECT));
                    } else {
                        userDto.setState(dbService.setState(userTgId, PROJECT_EXISTS));
                        return new MessageButtonHolder(
                                "Вот ваши проекты. Выберите любой или начните новый.",
                                allUserProjects.stream().map(ConstructionProjectDto::name).collect(Collectors.toMap(s -> s, s -> OPEN_EXIST_PROJECT)));
                    }
                } else if (DONT_SEE_PROJECT == userDto.getUserAction()) {
                    dbService.removeState(userTgId);
                    return new MessageButtonHolder("рад был познакомиться, пока!", null);
                }
            }
            case PROJECT_NOT_EXISTS -> {
                if (LETS_OPEN_PROJECT == userDto.getUserAction()) {
                    Long newProjectId = dbService.createNewProject(userDto);
                    ObjectMapper objectMapper = new ObjectMapper();
                    byte[] bytes = null;
                    try {
                        bytes = objectMapper.writeValueAsBytes(newProjectId);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e); //TODO
                    }
                    userDto.setState(dbService.setState(userTgId, BLANK_PROJECT_CREATED, bytes));
                    return new MessageButtonHolder("введите название проекта", null);
                }
                throw new IllegalStateException();
            }
            case BLANK_PROJECT_CREATED -> {
                StateDto stateByTgID = dbService.getStateByTgID(userTgId);
                dbService.updateProjectName(userTgId, Long.valueOf(new String(stateByTgID.getData())), userDto);
                userDto.setState(dbService.setState(userTgId, PROJECT_CREATED));
                List<ConstructionProjectDto> allUserProjects = dbService.getAllUserProjects(userTgId);
                return new MessageButtonHolder(
                        "Вот ваши проекты. Выберите любой или начните новый.",
                        allUserProjects.stream().map(ConstructionProjectDto::name).collect(Collectors.toMap(s -> s, s -> OPEN_EXIST_PROJECT)));
            }
            case PROJECT_EXISTS, PROJECT_CREATED -> {
                System.out.println(userDto);
            }
//            case CHOOSE_PROJECT -> null;
//            case CREATING_PROJECT -> null;
//            case PROJECT_CREATED -> null;
        }
        return null;
    }

    public State getUserState(Long telegramId) {
        return dbService.getStateByTgID(telegramId).getState();
    }
}
