//package kls.tgb.mapper;
//
//import kls.tgb.dao.entities.UserEntity;
//import kls.tgb.dto.UserDto;
//import org.instancio.Instancio;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.telegram.telegrambots.meta.api.objects.User;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class UserMapperTest {
//TODO
//    private final UserMapper userMapper;
//
//    @Autowired
//    public UserMapperTest(UserMapper userMapper) {
//        this.userMapper = userMapper;
//    }
//
//    @Test
//    void mapFromTgUserToUserDto() {
//        User telegramUser = Instancio.create(User.class);
//        UserDto userDto = userMapper.fromTgUserToUserDto(telegramUser);
//
//        assertEquals(telegramUser.getId(), userDto.getTelegramId());
//        assertEquals(telegramUser.getUserName(), userDto.getUsername());
//    }
//
//    @Test
//    void mapFromUserEntityToUserDto() {
//        UserEntity userEntity = Instancio.create(UserEntity.class);
//        UserDto userDto = userMapper.fromUserEntityToUserDto(userEntity);
//
//        assertEquals(userEntity.getId(), userDto.getId());
//        assertEquals(userEntity.getTelegramId(), userDto.getTelegramId());
//        assertEquals(userEntity.getUsername(), userDto.getUsername());
//        assertEquals(userEntity.getRole(), userDto.getRole());
//        assertEquals(userEntity.getCreatedAt(), userDto.getCreatedAt());
//        assertEquals(userEntity.getProjects().size(), userDto.getProjects().size());
//    }
//
//}