package kls.tgb.dao.entities;

import jakarta.persistence.*;
import kls.tgb.dto.sm.StartCommandState;

import static kls.tgb.util.StringConstants.STATE;
import static kls.tgb.util.StringConstants.TELEGRAM_ID;

@Entity
@Table(name = STATE)
public class StateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = TELEGRAM_ID, unique = true, nullable = false)
    private Long telegramId;

    @Column(name = STATE, nullable = false)
    @Enumerated(EnumType.STRING)
    private StartCommandState state;

    @Lob
    @Column(length = 1000000)
    private byte[] data;

    public StateEntity() {}

    public StateEntity(Long telegramId, StartCommandState state) {
        this.telegramId = telegramId;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public StartCommandState getState() {
        return state;
    }

    public byte[] getData() {
        return data;
    }

    public void setState(StartCommandState state) {
        this.state = state;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
