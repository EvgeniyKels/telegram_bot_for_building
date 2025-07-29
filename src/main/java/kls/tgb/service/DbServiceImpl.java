package kls.tgb.service;

import kls.tgb.dao.entities.ConstructionProjectEntity;
import kls.tgb.dao.entities.StateEntity;
import kls.tgb.dao.entities.UserEntity;
import kls.tgb.dao.repo.ConstructionProjectRepo;
import kls.tgb.dao.repo.StateRepo;
import kls.tgb.dao.repo.UserRepo;
import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.State;
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

    @Override
    @Transactional
    public UserDto getOrCreateUser(final Long telegramId, UserDto userDto) {
        return userRepo.findByTelegramId(telegramId).map(x -> {
            log.debug("User already exists with telegram id {}", telegramId);
            UserDto userDtoFromDb = userMapper.fromUserEntityToUserDto(x);
            userDtoFromDb.setIsNewUser(Boolean.FALSE);
            return userDtoFromDb;
        }).orElseGet(() -> {
            log.debug("User does not exist with telegram id {}", telegramId);
            UserDto userDtoFromDb = userMapper.fromUserEntityToUserDto(saveUser(userDto));
            userDtoFromDb.setIsNewUser(Boolean.TRUE);
            return userDtoFromDb;
        });

    }

    @Override
    @Transactional
    public State setState(Long telegramId, State state) {
        return setState(telegramId, state, new byte[0]);
    }

    @Override
    @Transactional
    public State setState(Long telegramId, State state, byte[] data) {
        Optional<StateEntity> stateRepoByTelegramId = stateRepo.findByTelegramId(telegramId);

        StateEntity stateEntity;

        if (stateRepoByTelegramId.isPresent()) {
            stateEntity = stateRepoByTelegramId.get();
            stateEntity.setState(state);
        } else {
            stateEntity = new StateEntity(telegramId, state);
        }

        if (data.length != 0) {
            stateEntity.setData(data);
        }

        return stateRepo.save(stateEntity).getState();
    }

    @Override
    @Transactional
    public List<ConstructionProjectDto> getAllUserProjects(Long userTgId) {
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
    public void removeState(Long userTgId) {
        stateRepo.deleteByTelegramId(userTgId);
    }

    @Override
    public boolean isUserExists(Long userTgId) {
        return userRepo.existsByTelegramId(userTgId);
    }

    @Override
    @Transactional
    public Long createNewProject(UserDto userDto) {
        UserEntity userEntity = userRepo.findByTelegramId(userDto.getTelegramId()).orElseThrow();
        ConstructionProjectEntity newProject = new ConstructionProjectEntity();
        newProject.setName("Project without name");
        newProject.setTotalBudget(BigDecimal.ZERO);
        newProject.setStartDate(LocalDate.now());
        userEntity.addProject(newProject);
        ConstructionProjectEntity projectEntity = constructionProjectRepo.save(newProject);
        userRepo.save(userEntity);
        return projectEntity.getId();
    }

    @Override
    @Transactional
    public void updateProjectName(Long userTgId, Long projectId, UserDto userDto) {
        ConstructionProjectEntity constructionProjectEntity = constructionProjectRepo.findById(projectId).orElseThrow();
        constructionProjectEntity.setName(userDto.getNewProjectName());
        constructionProjectRepo.save(constructionProjectEntity);
    }

    @Override
    @Transactional(readOnly = true) //TODO а нада ?
    public StateDto getStateByTgID(@NonNull Long telegramId) {
        final var stateEntityOptional = stateRepo.findByTelegramId(telegramId);
        return stateEntityOptional.map(stateMapper::fromStateEntityToStateDto).orElseGet(() -> {
            StateDto stateDto = new StateDto();
            stateDto.setTelegramId(telegramId);
            stateDto.setState(State.STATE_NOT_EXISTS);
            return stateDto;
        });
    }


    private UserEntity saveUser(final UserDto userDto) {
        final var userEntity = new UserEntity(
                userDto.getTelegramId(), userDto.getUsername(), userDto.getSelfUserName(), LocalDateTime.now());

        return userRepo.save(userEntity);
    }
}
