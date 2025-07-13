package kls.tgb.dto;

import kls.tgb.dao.entities.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    private final Long telegramId;
    private final String username;
    private final Role role;
    private final LocalDateTime createdAt;
    private final List<ConstructionProjectDto> projects;
}
