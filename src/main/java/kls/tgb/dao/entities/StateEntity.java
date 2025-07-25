package kls.tgb.dao.entities;

import jakarta.persistence.*;
import kls.tgb.dto.sm.RegistrationState;

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
    private RegistrationState registrationState;

    @Lob
    @Column(length = 1000000)
    private byte[] data;

    public StateEntity() {}

    public StateEntity(Long telegramId, RegistrationState registrationState) {
        this.telegramId = telegramId;
        this.registrationState = registrationState;
    }

    public Long getId() {
        return id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public RegistrationState getRegistrationState() {
        return registrationState;
    }

    public byte[] getData() {
        return data;
    }

    public void setRegistrationState(RegistrationState registrationState) {
        this.registrationState = registrationState;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
