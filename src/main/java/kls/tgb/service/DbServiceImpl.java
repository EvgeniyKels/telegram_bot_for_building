package kls.tgb.service;

import kls.tgb.dao.entities.UserEntity;
import kls.tgb.dao.repo.UserRepo;
import kls.tgb.dto.UserDto;
import kls.tgb.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class DbServiceImpl implements DbService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto registerOrUpdateUser(final UserDto userDto) {
        return userRepo.findByTelegramId(userDto.getTelegramId()).map(x -> {
            log.debug("User already exists with telegram id {}", userDto.getTelegramId());
            return userMapper.fromUserEntityToUserDto(x);
        }).orElseGet(() -> {
            log.debug("User does not exist with telegram id {}", userDto.getTelegramId());
            final var savedEntity = saveUser(userDto);
            return userMapper.fromUserEntityToUserDto(savedEntity);
        });

    }

    private UserEntity saveUser(final UserDto userDto) {
        final var userEntity = new UserEntity(
                userDto.getTelegramId(), userDto.getUsername(), LocalDateTime.now());
        return userRepo.save(userEntity);
    }
}
