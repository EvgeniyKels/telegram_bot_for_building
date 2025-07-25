package kls.tgb.service;

import kls.tgb.dao.entities.StateEntity;
import kls.tgb.dao.entities.UserEntity;
import kls.tgb.dao.repo.StateRepo;
import kls.tgb.dao.repo.UserRepo;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.RegistrationState;
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
    private final UserMapper userMapper;

    private final StateRepo stateRepo;

    @Override
    @Transactional
    public UserDto registerOrUpdateUser(final UserDto userDto) {
        return userRepo.findByTelegramId(userDto.getTelegramId()).map(x -> {
            log.debug("User already exists with telegram id {}", userDto.getTelegramId());
            setState(userDto.getTelegramId(), RegistrationState.USER_EXISTS);
            return userMapper.fromUserEntityToUserDto(x);
        }).orElseGet(() -> {
            log.debug("User does not exist with telegram id {}", userDto.getTelegramId());
            final var savedEntity = saveUser(userDto);
            setState(userDto.getTelegramId(), RegistrationState.NEW_USER_REGISTERED);
            return userMapper.fromUserEntityToUserDto(savedEntity);
        });

    }

    @Override
    @Transactional(readOnly = true) //TODO а нада ?
    public RegistrationState getStateByTgID(@NonNull Long telegramId) {
        final var stateEntityOptional = stateRepo.findByTelegramId(telegramId);
        return stateEntityOptional.isPresent() ?
                stateEntityOptional.get().getRegistrationState() :
                RegistrationState.STATE_NOT_EXISTS;
    }

    @Override
    @Transactional
    public void setState(Long telegramId, RegistrationState state) {
        Optional<StateEntity> stateRepoByTelegramId = stateRepo.findByTelegramId(telegramId);

        if (stateRepoByTelegramId.isPresent()) {
            StateEntity stateEntity = stateRepoByTelegramId.get();
            stateEntity.setRegistrationState(state);
            stateRepo.save(stateEntity);
        } else {
            stateRepo.save(new StateEntity(telegramId, state));
        }

    }

    private UserEntity saveUser(final UserDto userDto) {
        final var userEntity = new UserEntity(
                userDto.getTelegramId(), userDto.getUsername(), LocalDateTime.now());
        return userRepo.save(userEntity);
    }
}
