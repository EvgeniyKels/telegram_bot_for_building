package kls.tgb.telegram.userinputhandlers.commandhandlers;

import kls.tgb.dto.ExpenseDto;
import kls.tgb.telegram.MessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandAllExpensesHandler implements CommandHandler {

    @Value("${telegram.handler.description.all_expenses}")
    private String allExpHandlerDescription;

    private final MessageSender messageSender;

    public CommandAllExpensesHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void handle(Message message) {

        messageSender.sendMessage(String.valueOf(tgUserChatDto.chatID()), messageButtonHolder);

    }

    @Override
    public String getHandlerName() {
        return SLASH.concat(ALL_EXPENSES);
    }

    @Override
    public String getHandlerDescription() {
        return allExpHandlerDescription;
    }

    private void prepareExpensesTable(List<ExpenseDto> expenses) {
        String table =
                "```\n" +
                        "| Категория   | Сумма    | Дата     |\n" +
                        "|-------------|----------|----------|\n" +
                        "```";


        StringBuilder tableSb = new StringBuilder();
        tableSb.append(table);

        for (ExpenseDto expense : expenses) {
            tableSb.append(String.format("| %-15s | %-8.2f | %-10s |\n",
                    expense.getCategory(),
                    expense.getAmount(),
                    expense.getDate()));
        }
    }
}
