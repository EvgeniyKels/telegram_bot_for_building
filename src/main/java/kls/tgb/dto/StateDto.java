package kls.tgb.dto;

import kls.tgb.dto.sm.StartCommandState;
import lombok.Data;

@Data
public class StateDto {

    private Long telegramId;
    private String state;
    private byte[] data;

}
