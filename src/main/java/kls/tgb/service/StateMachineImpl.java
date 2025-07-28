package kls.tgb.service;

import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.State;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
                userDto.setState(dbService.setState(userTgId, WAITING_FOR_NAME));
                return new MessageButtonHolder("приветик, как тебя звать ?", null);
            }
            case WAITING_FOR_NAME -> {
                final var userDtoAfterSaveUpdate = dbService.getOrCreateUser(userTgId, userDto);
                userDto.setState(dbService.setState(userTgId, NEW_USER_REGISTERED));
                return new MessageButtonHolder(
                        "приятно познакомиться, ".concat(userDtoAfterSaveUpdate.getSelfUserName()).concat(" Поздравляю с регистрацией. Перейдем к проектам ? "),
                        Map.of("Да", "Yes", "Нет", "No"));
            }
            case NEW_USER_REGISTERED -> {
                // получаем данные о проектах или предлагаем создать новый
                List<ConstructionProjectDto> allUserProjects = dbService.getAllUserProjects(userTgId);
                if (allUserProjects.isEmpty()) {
                    userDto.setState(dbService.setState(userTgId, PROJECT_NOT_EXISTS));
                    return new MessageButtonHolder(
                            "У вас нет проектов. Хотите создать новый ?",
                            Map.of("Да", "Yes", "Нет", "No"));
                } else {
                    userDto.setState(dbService.setState(userTgId, PROJECT_EXISTS));
                    Map<String, String> map = allUserProjects.stream().map(Objects::toString).collect(Collectors.toMap(s -> s, s -> s));
                    map.put("новый", "new");
                    return new MessageButtonHolder(
                            "Вот список ваших проектов, выберети нужный иди создайте новый ",
                            Collections.unmodifiableMap(map)
                    );
                }
            }
            case PROJECT_NOT_EXISTS -> {
                System.out.println(userDto);
            }
            case PROJECT_EXISTS -> {
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
