package kls.tgb.dto;

import kls.tgb.dao.entities.Role;
import kls.tgb.dto.sm.Actions;
import kls.tgb.dto.sm.State;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private Long telegramId;
    private String username;
    private Role role;
    private LocalDateTime createdAt;
    private List<ConstructionProjectDto> projects;
    private State state;
    private String selfUserName;
    private String newProjectName;
    private Actions userAction;
    private Boolean isNewUser;
    private Long chatId;
}
