package kls.tgb.service;

import kls.tgb.dao.entities.ConstructionProjectEntity;
import kls.tgb.dao.entities.ExpenseEntity;
import kls.tgb.dao.entities.StateEntity;
import kls.tgb.dao.entities.UserEntity;
import kls.tgb.dao.repo.ConstructionProjectRepo;
import kls.tgb.dao.repo.ExpenseRepo;
import kls.tgb.dao.repo.StateRepo;
import kls.tgb.dao.repo.UserRepo;
import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.ExpenseDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.StartCommandState;
import kls.tgb.mapper.ConstructionProjectMapper;
import kls.tgb.mapper.StateMapper;
import kls.tgb.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static kls.tgb.util.SerializeUtil.convertObjectToByteArray;

@Slf4j
@Service
@AllArgsConstructor
public class DbServiceImpl implements DbService {

    private final UserRepo userRepo;
    private final StateRepo stateRepo;
    private final StateMapper stateMapper;
    private final UserMapper userMapper;
    private final ConstructionProjectMapper projectMapper;
    private final ConstructionProjectRepo constructionProjectRepo;
    private final ExpenseRepo expenseRepo;

    @Override
    @Transactional
    public UserDto getOrCreateUser(@NonNull final Long telegramId, @NonNull final String tgUserName, final String selfUserName) { //TODO азнести на два метода
        return userRepo.findByTelegramId(telegramId).map(x -> {
            log.debug("User already exists with telegram id {}", telegramId);
            UserDto userDtoFromDb = userMapper.fromUserEntityToUserDto(x);
            userDtoFromDb.setIsNewUser(Boolean.FALSE);
            return userDtoFromDb;
        }).orElseGet(() -> {
            log.debug("User does not exist with telegram id {}", telegramId);
            UserDto userDtoFromDb = userMapper.fromUserEntityToUserDto(saveUser(telegramId, tgUserName, selfUserName));
            userDtoFromDb.setIsNewUser(Boolean.TRUE);
            return userDtoFromDb;
        });

    }

    @Override
    @Transactional
    public String setState(@NonNull final Long telegramId, @NonNull final String state, byte[] data) {
        Optional<StateEntity> stateRepoByTelegramId = stateRepo.findByTelegramId(telegramId);

        StateEntity stateEntity;

        if (stateRepoByTelegramId.isPresent()) {
            stateEntity = stateRepoByTelegramId.get();
            stateEntity.setState(state);
        } else {
            stateEntity = new StateEntity(telegramId, state);
        }

        stateEntity.setData(data);

        return stateRepo.save(stateEntity).getState();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConstructionProjectDto> getAllUserProjects(@NonNull final Long userTgId) {
        Optional<UserEntity> userEntityOptional = userRepo.findByTelegramId(userTgId);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            return constructionProjectRepo.findConstructionProjectEntitiesByUser(userEntity).
                    stream().
                    map(projectMapper::fromConstructionProjectEntityToConstructionProjectDto).
                    toList();
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional
    public void removeState(@NonNull final Long userTgId) {
        stateRepo.deleteByTelegramId(userTgId);
    }

    @Override
    public boolean isUserExists(@NonNull final Long userTgId) {
        return userRepo.existsByTelegramId(userTgId);
    }

    @Override
    @Transactional
    public Long createNewProject(@NonNull final UserDto userDto) {
        UserEntity userEntity = userRepo.findByTelegramId(userDto.getTelegramId()).orElseThrow();
        ConstructionProjectEntity newProject = new ConstructionProjectEntity();
        newProject.setName("Project without name");
        newProject.setTotalBudget(BigDecimal.ZERO);
        newProject.setStartDate(LocalDate.now());
        userEntity.addProject(newProject);
        ConstructionProjectEntity projectEntity = constructionProjectRepo.save(newProject);
        return projectEntity.getId();
    }

    @Override
    @Transactional
    public void updateProjectName(@NonNull final Long projectId, @NonNull final UserDto userDto) {
        ConstructionProjectEntity constructionProjectEntity = constructionProjectRepo.findById(projectId).orElseThrow();
        constructionProjectEntity.setName(userDto.getActiveProjectName());
        ConstructionProjectEntity projectEntity = constructionProjectRepo.save(constructionProjectEntity);
        userDto.setActiveProjectId(projectEntity.getId());
    }

    @Transactional
    public void updateExpenseDescription(@NonNull final Long expenseId, @NonNull final String description) {
        ExpenseEntity expenseEntity = expenseRepo.findById(expenseId).orElseThrow();
        expenseEntity.setDescription(description);
    }

    @Override
    public void updateExpenseAmount(Long expenseId, Long amount) {
        ExpenseEntity expenseEntity = expenseRepo.findById(expenseId).orElseThrow();
        expenseEntity.setAmount(BigDecimal.valueOf(amount));
    }

    @Override
    @Transactional
    public ExpenseDto createNewExpense(ExpenseDto dto) {
        ExpenseEntity expenseEntity = new ExpenseEntity();
        UserEntity userEntity = userRepo.findByTelegramId(dto.getTelegramId()).orElseThrow();
        ConstructionProjectEntity constructionProjectEntity = constructionProjectRepo.findById(dto.getActiveProjectId()).orElseThrow();
        expenseEntity.setUser(userEntity);
        expenseEntity.setProject(constructionProjectEntity);
        ExpenseEntity savedExpenseEntity = expenseRepo.save(expenseEntity);
        userRepo.save(userEntity);
        dto.setId(savedExpenseEntity.getId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public StateDto getStateByTgID(@NonNull Long telegramId) {
        final var stateEntityOptional = stateRepo.findByTelegramId(telegramId);
        return stateEntityOptional.map(stateMapper::fromStateEntityToStateDto).orElseGet(() -> {
            StateDto stateDto = new StateDto();
            stateDto.setTelegramId(telegramId);
            stateDto.setState(StartCommandState.STATE_NOT_EXISTS.name());
            UserDto userDto = new UserDto();
            userDto.setTelegramId(telegramId);
            stateDto.setData(convertObjectToByteArray(telegramId, userDto));
            return stateDto;
        });
    }


    private UserEntity saveUser(final Long tgId, final String userName, final String selfUserName) {
        final var userEntity = new UserEntity(
                tgId, userName, selfUserName, LocalDateTime.now());

        return userRepo.save(userEntity);
    }
}
