package kls.tgb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kls.tgb.dto.StateDto;
import kls.tgb.dto.TgUserChatDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.StartCommandState;
import kls.tgb.dto.sm.UserAction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static kls.tgb.util.StringConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class StartStateMachineImplTest {
    @Autowired
    @Qualifier(START_COMMAND_SM)
    private StateMachine stateMachine;

    @MockBean
    private DbService dbService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long TEST_USER_ID = 123L;
    private final String TEST_USERNAME = "test_user";
    private final Long TEST_CHAT_ID = 321L;

    @Test
    void testFullStateFlow() throws Exception {
        // 1. STATE_NOT_EXISTS → WAITING_FOR_NAME (пользователь новый)
        UserDto userDto = new UserDto();
        userDto.setId(TEST_USER_ID);
        userDto.setUsername(TEST_USERNAME);
        mockStateInDb(TEST_USER_ID, StartCommandState.STATE_NOT_EXISTS, userDto);

        TgUserChatDto dto = new TgUserChatDto(TEST_USER_ID, TEST_CHAT_ID, null, null, null);
        MessageButtonHolder response = stateMachine.handleCommandStates(dto);

        assertThat(response.message()).isEqualTo(WHAT_IS_YOUR_NAME);
        verify(dbService).setState(eq(TEST_USER_ID), eq(StartCommandState.WAITING_FOR_NAME.name()), any());

        // 2. WAITING_FOR_NAME → NEW_USER_REGISTERED (вводим имя)
        UserDto userWithName = new UserDto();
        userWithName.setId(TEST_USER_ID);
        userWithName.setUsername(TEST_USERNAME);
        mockStateInDb(TEST_USER_ID, StartCommandState.WAITING_FOR_NAME, userWithName);

        dto = new TgUserChatDto(TEST_USER_ID, TEST_CHAT_ID, "Иван", null, null);
        response = stateMachine.handleCommandStates(dto);

        assertThat(response.message()).contains(GREETINGS);
        verify(dbService).setState(eq(TEST_USER_ID), eq(StartCommandState.NEW_USER_REGISTERED.name()), any());

        // 3. NEW_USER_REGISTERED → PROJECT_NOT_EXISTS (выбираем "Посмотреть проекты")
        dto = new TgUserChatDto(TEST_USER_ID, TEST_CHAT_ID, null, UserAction.LETS_SEE_PROJECTS, null);
        when(dbService.getAllUserProjects(TEST_USER_ID)).thenReturn(List.of());
        response = stateMachine.handleCommandStates(dto);

        assertThat(response.message()).isEqualTo(YOU_HAVE_NOT_PROJECTS);
        verify(dbService).setState(eq(TEST_USER_ID), eq(StartCommandState.PROJECT_NOT_EXISTS.name()), any());

        // 4. PROJECT_NOT_EXISTS → BLANK_PROJECT_CREATED (создаём проект)
        dto = new TgUserChatDto(TEST_USER_ID, TEST_CHAT_ID, null, UserAction.LETS_OPEN_PROJECT, null);
        when(dbService.createNewProject(any())).thenReturn(1L);
        response = stateMachine.handleCommandStates(dto);

        assertThat(response.message()).isEqualTo(ENTER_PROJECT_NAME);
        verify(dbService).setState(eq(TEST_USER_ID), eq(StartCommandState.BLANK_PROJECT_CREATED.name()), any());

        // 5. BLANK_PROJECT_CREATED → PROJECT_CREATED (вводим имя проекта)
        dto = new TgUserChatDto(TEST_USER_ID, TEST_CHAT_ID, "Дом", null, null);
        response = stateMachine.handleCommandStates(dto);

        assertThat(response.message()).contains("Дом");
        verify(dbService).setState(eq(TEST_USER_ID), eq(StartCommandState.PROJECT_CREATED.name()), any());

        // 6. PROJECT_CREATED → START_COMMAND_FINISHED (завершение)
        response = stateMachine.handleCommandStates(dto);
        assertThat(response.message()).contains("/add_expense");
        verify(dbService).setState(eq(TEST_USER_ID), eq(StartCommandState.START_COMMAND_FINISHED.name()), any());
    }

    private void mockStateInDb(Long userId, StartCommandState state, UserDto userDto) throws Exception {
        StateDto stateDto = new StateDto();
        stateDto.setTelegramId(userId);
        stateDto.setState(state.name());
        stateDto.setData(objectMapper.writeValueAsBytes(userDto));
        when(dbService.getStateByTgID(userId)).thenReturn(stateDto);
        when(dbService.isUserExists(userId)).thenReturn(state != StartCommandState.STATE_NOT_EXISTS);
    }
}