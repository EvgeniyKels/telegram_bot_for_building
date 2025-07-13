package kls.tgb.dao;

import kls.tgb.dao.entities.UserEntity;
import kls.tgb.dao.repo.UserRepo;
import kls.tgb.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DbServiceImpl implements DbService {

    private final UserRepo userRepo;

    @Override
    public UserDto registerOrUpdateUser(UserDto userDto) {
        return null;
    }
}
