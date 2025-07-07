package kls.tgb.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Dispatcher {

    void dispatch(Update update);

}
