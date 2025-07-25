package kls.tgb.dto;

import kls.tgb.dto.sm.State;
import lombok.Data;

@Data
public class StateDto {

    private Long telegramId;
    private State state;
    private byte[] data;

}
