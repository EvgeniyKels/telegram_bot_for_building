package kls.tgb.dto;

import kls.tgb.dto.sm.UserAction;

public record TgUserChatDto(
        Long telegramID,
        Long chatID,
        String username,
        UserAction userAction,
        String userInput
) {

}
