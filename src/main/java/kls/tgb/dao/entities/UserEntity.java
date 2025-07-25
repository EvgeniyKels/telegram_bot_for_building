package kls.tgb.dao.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static kls.tgb.util.StringConstants.*;

@Entity
@Table(name = USERS)
public class UserEntity {

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

    @Column(name = SELF_USERNAME, length = 100)
    private String selfUserName;

    // Связи
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConstructionProjectEntity> projects;

    public UserEntity() {}

    public UserEntity(Long telegramId, String username, String selfUserName, LocalDateTime createdAt) {
        this.telegramId = telegramId;
        this.username = username;
        this.selfUserName = selfUserName;
        this.createdAt = createdAt;
        this.role = Role.USER;
    }

    public Long getId() {
        return id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ConstructionProjectEntity> getProjects() {
        return projects;
    }

    public String getSelfUserName() {
        return selfUserName;
    }

    public void addProject(ConstructionProjectEntity project) {
        if (isNull(projects)) {
            projects = new ArrayList<>();
        }
        projects.add(project);
        project.setUser(this);
    }

}