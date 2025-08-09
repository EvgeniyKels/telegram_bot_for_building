package kls.tgb.service;

import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.ExpenseDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import lombok.NonNull;

import java.util.List;

public interface DbService {
    UserDto getOrCreateUser(final Long telegramId, @NonNull String userName, String selfUserName);

    StateDto getStateByTgID(@NonNull Long id);

    String setState(Long telegramId, String state, byte[] data);

    List<ConstructionProjectDto> getAllUserProjects(Long userTgId);

    void removeState(Long userTgId);

    boolean isUserExists(Long userTgId);

    Long createNewProject(UserDto userDto);

    void updateProjectName(@NonNull final Long projectId, @NonNull final UserDto userDto);

    ExpenseDto createNewExpense(ExpenseDto dto);

    void updateExpenseDescription(@NonNull final Long expenseId, @NonNull final String description);

    void updateExpenseAmount(Long expenseId, Long amount);
}
