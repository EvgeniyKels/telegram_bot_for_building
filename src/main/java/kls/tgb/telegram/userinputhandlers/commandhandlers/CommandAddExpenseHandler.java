package kls.tgb.telegram.userinputhandlers.commandhandlers;

import kls.tgb.dto.ExpenseDto;
import kls.tgb.dto.sm.AddExpenseState;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandAddExpenseHandler implements CommandHandler{

    @Value("${telegram.handler.description.add_expense}")
    private String addExpenseHandlerDescription;

    private final MessageSender messageSender;
    private final StateMachine<ExpenseDto, AddExpenseState> stateMachine;
    private final UserMapper userMapper;

    public CommandAddExpenseHandler(MessageSender messageSender, StateMachine<ExpenseDto, AddExpenseState> stateMachine, UserMapper userMapper) {
        this.messageSender = messageSender;
        this.stateMachine = stateMachine;
        this.userMapper = userMapper;
    }

    @Override
    public void handle(Message message) {
        stateMachine.handleStartCommandStates(null, null);
    }

    @Override
    public String getHandlerName() {
        return SLASH.concat(ADD_EXPENSE);
    }

    @Override
    public String getHandlerDescription() {
        return addExpenseHandlerDescription;
    }
}
