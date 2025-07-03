package kls.tgb.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kls.tgb.util.StringConstants.*;

@Entity
@Table(name = USERS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = TELEGRAM_ID, unique = true, nullable = false)
    private Long telegramId;

    @Column(name = USERNAME, length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = ROLE, nullable = false, length = 20)
    private Role role;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Связи
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConstructionProject> projects = new ArrayList<>();

}