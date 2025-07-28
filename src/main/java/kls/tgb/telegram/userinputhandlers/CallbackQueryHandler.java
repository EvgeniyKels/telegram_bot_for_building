package kls.tgb.telegram.userinputhandlers;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackQueryHandler {

    void handleCallbackQuery(CallbackQuery callbackQuery);

}
