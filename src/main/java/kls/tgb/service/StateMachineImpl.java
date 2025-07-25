package kls.tgb.service;

import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.State;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static kls.tgb.dto.sm.State.*;

@Service
@AllArgsConstructor
public class StateMachineImpl /* implements StateMachine */ {

    private final DbService dbService;

    //TODO приветственное сообщение получать из БД
    public String handleState(final Long userTgId, final UserDto userDto) {
        final var state = userDto.getState();
        switch (state) {
            case STATE_NOT_EXISTS -> {
                userDto.setState(dbService.setState(userTgId, WAITING_FOR_NAME));
                return "приветик, как тебя звать ?";
            }
            case WAITING_FOR_NAME -> {
                final var userDtoAfterSaveUpdate = dbService.getOrCreateUser(userTgId, userDto);
                userDto.setState(dbService.setState(userTgId, NEW_USER_REGISTERED));
                return "приятно познакомиться, ".concat(userDtoAfterSaveUpdate.getSelfUserName());
            }
            case NEW_USER_REGISTERED -> {
                return "поздравляю с регистрацией";
            }
//            case PROJECT_EXISTS -> null;
//            case PROJECT_NOT_EXISTS -> null;
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
