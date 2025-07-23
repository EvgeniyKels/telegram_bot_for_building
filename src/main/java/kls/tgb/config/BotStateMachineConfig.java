package kls.tgb.config;

import kls.tgb.dto.RegistrationEvent;
import kls.tgb.dto.RegistrationState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class BotStateMachineConfig extends EnumStateMachineConfigurerAdapter<RegistrationState, RegistrationEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<RegistrationState, RegistrationEvent> states) throws Exception {
        states.
                withStates().
                initial(RegistrationState.START).
                states(
                        EnumSet.allOf(RegistrationState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<RegistrationState, RegistrationEvent> transitions) throws Exception {
        transitions.
                withExternal().
                    source(RegistrationState.START).
                    target(RegistrationState.USER_EXISTS).
                    event(RegistrationEvent.USER_FOUND).
                and().
                withExternal().
                    source(RegistrationState.START).
                    target(RegistrationState.NEW_USER_REGISTERED).
                    event(RegistrationEvent.USER_NOT_FOUND).
                and().
                withExternal().
                    source(RegistrationState.NEW_USER_REGISTERED).
                    target(RegistrationState.WAITING_FOR_PROJECT_NAME).
                    event(RegistrationEvent.CREATE_PROJECT).
                and().
                withExternal().
                    source(RegistrationState.WAITING_FOR_PROJECT_NAME).
                    target(RegistrationState.PROJECT_CREATED).
                    event(RegistrationEvent.PROJECT_NAME_RECEIVED);
    }

}
