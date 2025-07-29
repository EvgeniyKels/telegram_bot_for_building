package kls.tgb.telegram.userinputhandlers;

import kls.tgb.dto.sm.Actions;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@AllArgsConstructor
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {

    private final UserInputHandlerUtils userInputHandlerUtils;
    private final StateMachine stateMachine;
    private final MessageSender messageSender;

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {

        PreHandleDataHolder preHandleDataHolder = userInputHandlerUtils.prepareHandlerData(callbackQuery);
        final var registrationState = stateMachine.getUserState(preHandleDataHolder.userDto().getTelegramId());
        preHandleDataHolder.userDto().setState(registrationState);
        preHandleDataHolder.userDto().setUserAction(Actions.valueOf(callbackQuery.getData()));

        MessageButtonHolder messageButtonHolder = stateMachine.handleStartCommandStates(preHandleDataHolder.userDto().getTelegramId(), preHandleDataHolder.userDto());

        messageSender.sendMessage(String.valueOf(preHandleDataHolder.chatId()), messageButtonHolder);

    }
}
