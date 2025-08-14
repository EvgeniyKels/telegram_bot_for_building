package kls.tgb.dto;

import lombok.Data;

@Data
public class StateDto {

    private Long telegramId;
    private String state;
    private byte[] data;

}
