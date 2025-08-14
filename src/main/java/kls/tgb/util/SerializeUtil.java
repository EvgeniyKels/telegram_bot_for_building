package kls.tgb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerializeUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T convertByteArrayToObject(byte[] data, Class<T> clazz) {
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO
        }
    }

    public static <K> byte[] convertObjectToByteArray(K dto) {
        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //TODO
        }
        return bytes;
    }

}
