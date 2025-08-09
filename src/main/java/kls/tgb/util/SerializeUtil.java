package kls.tgb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kls.tgb.dto.ExpenseDto;
import kls.tgb.dto.UserDto;
import kls.tgb.exception.StateMachineException;

import java.io.IOException;

public class SerializeUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T convertByteArrayToObject(byte[] data, Class<T> clazz) {
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO
        }
    }

    public static <K> byte[] convertObjectToByteArray(Long userTgId, K userDto) {
        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(userDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //TODO
        }
        return bytes;
    }

}
