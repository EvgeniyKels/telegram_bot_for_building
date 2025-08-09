package kls.tgb.dto.sm;

import lombok.Getter;

@Getter
public enum UserAction {

    LETS_SEE_PROJECTS("lets_see_projects"),
    DONT_SEE_PROJECT("dont_see_projects"),

    LETS_OPEN_PROJECT("lets_open_projects"),
    DONT_OPEN_PROJECT("dont_open_projects"),

    OPEN_EXIST_PROJECT("open_exist_project");

    UserAction(String userAction) {}
}
