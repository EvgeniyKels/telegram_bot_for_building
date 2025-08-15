package kls.tgb.service;

import kls.tgb.dto.StateDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static kls.tgb.util.StringConstants.ALL_EXPENSES_COMMAND_SERVICE;

@Slf4j
@Service
@AllArgsConstructor
@Qualifier(ALL_EXPENSES_COMMAND_SERVICE)
public class AllExpenseStateMachineService {
    public MessageButtonHolder handleInitialState(StateDto stateByTgID) {
        return null;
    }

    public MessageButtonHolder prepareMarkdown(StateDto stateByTgID, String s) {
        return null;
    }
}
