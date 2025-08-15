package kls.tgb.service;

import kls.tgb.dto.StateDto;
import kls.tgb.dto.sm.AddExpenseState;
import kls.tgb.dto.sm.StartCommandState;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static kls.tgb.util.StringConstants.*;

@Component
public class SMFactory {

    private final StateMachine startCommandSm;

    private final StateMachine addExpenseCommandSm;

    private final DbService dbService;

    public SMFactory(
            @Qualifier(START_COMMAND_SM) StateMachine startCommandSm,
            @Qualifier(EXPENSE_COMMAND_SM) StateMachine addExpenseCommandSm, DbService dbService) {
        this.startCommandSm = startCommandSm;
        this.addExpenseCommandSm = addExpenseCommandSm;
        this.dbService = dbService;
    }

    public StateMachine getStateMachineByState(Long tgId) {
        StateDto stateByTgID = dbService.getStateByTgID(tgId);
        String state = stateByTgID.getState();
        if (Arrays.stream(StartCommandState.values()).map(Enum::name).anyMatch(x -> x.equals(state))) {
            return startCommandSm;
        } else if (Arrays.stream(AddExpenseState.values()).map(Enum::name).anyMatch(x -> x.equals(state))) {
            return addExpenseCommandSm;
        } else {
            throw new IllegalArgumentException("Unknown state " + state);
        }
    }
}
