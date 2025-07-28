package kls.tgb.service;

import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.State;
import lombok.NonNull;

import java.util.List;

public interface DbService {
    UserDto getOrCreateUser(final Long telegramId, UserDto selfUserName);

    StateDto getStateByTgID(@NonNull Long id);

    State setState(Long telegramId, State state);

    List<ConstructionProjectDto> getAllUserProjects(Long userTgId);
}
