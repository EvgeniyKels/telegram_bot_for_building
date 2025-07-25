package kls.tgb.service;

import kls.tgb.dao.entities.StateEntity;
import kls.tgb.dao.entities.UserEntity;
import kls.tgb.dao.repo.StateRepo;
import kls.tgb.dao.repo.UserRepo;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.State;
import kls.tgb.mapper.StateMapper;
import kls.tgb.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class DbServiceImpl implements DbService {

    private final UserRepo userRepo;
    private final StateRepo stateRepo;
    private final StateMapper stateMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto registerOrUpdateUser(final Long telegramId, UserDto userDto) {
        return userRepo.findByTelegramId(telegramId).map(x -> {
            log.debug("User already exists with telegram id {}", telegramId);
            setState(telegramId, State.USER_EXISTS);
            return userMapper.fromUserEntityToUserDto(x);
        }).orElseGet(() -> {
            log.debug("User does not exist with telegram id {}", telegramId);
            final var savedEntity = saveUser(userDto);
            setState(telegramId, State.NEW_USER_REGISTERED);
            return userMapper.fromUserEntityToUserDto(savedEntity);
        });

    }

    @Override
    @Transactional
    public void setState(Long telegramId, State state) {
        Optional<StateEntity> stateRepoByTelegramId = stateRepo.findByTelegramId(telegramId);

        if (stateRepoByTelegramId.isPresent()) {
            StateEntity stateEntity = stateRepoByTelegramId.get();
            stateEntity.setState(state);
            stateRepo.save(stateEntity);
        } else {
            stateRepo.save(new StateEntity(telegramId, state));
        }

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
