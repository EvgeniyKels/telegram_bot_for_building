package kls.tgb.dao.entities;

import jakarta.persistence.*;
import kls.tgb.dto.sm.StartCommandState;
import lombok.Getter;
import lombok.Setter;

import static kls.tgb.util.StringConstants.STATE;
import static kls.tgb.util.StringConstants.TELEGRAM_ID;

@Entity
@Table(name = STATE)
@Getter
@Setter
public class StateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = TELEGRAM_ID, unique = true, nullable = false)
    private Long telegramId;

    @Column(name = STATE, nullable = false)
    private String state;

    @Lob
    @Column(length = 1000000)
    private byte[] data;

    public StateEntity() {}

    public StateEntity(Long telegramId, String state) {
        this.telegramId = telegramId;
        this.state = state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
