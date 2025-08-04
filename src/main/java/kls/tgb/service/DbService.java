package kls.tgb.service;

import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.StartCommandState;
import lombok.NonNull;

import java.util.List;

public interface DbService {
    UserDto getOrCreateUser(final Long telegramId, UserDto selfUserName);

    StateDto getStateByTgID(@NonNull Long id);

    String setState(Long telegramId, String state, byte[] data);

    List<ConstructionProjectDto> getAllUserProjects(Long userTgId);

    void removeState(Long userTgId);

    boolean isUserExists(Long userTgId);

    Long createNewProject(UserDto userDto);

    void updateProjectName(Long userTgId, Long projectId, UserDto userDto);
}
