package kls.tgb.telegram.userinputhandlers;

import kls.tgb.dto.UserDto;

public record PreHandleDataHolder(
        UserDto userDto,
        Long chatId
) {
}
