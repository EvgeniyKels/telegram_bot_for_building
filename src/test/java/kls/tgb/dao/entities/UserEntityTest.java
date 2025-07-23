package kls.tgb.dao.entities;

import kls.tgb.dao.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepo repo;

    @Test
    void retrieveUser() {
        final var telegramId = 222L;
        final var userName = "user_name";
        final var role = Role.USER;

        final var projectName = "project_name";
        final var totalBudget = BigDecimal.TEN;
        final var startDate = LocalDate.now().minusDays(10);
        final var endDate = LocalDate.now().minusDays(1);

        UserEntity userEntity = new UserEntity(telegramId, userName, LocalDateTime.now());

        ConstructionProjectEntity constructionProjectEntity =
                ConstructionProjectEntity.builder().
                        name(projectName).
                        totalBudget(totalBudget).
                        startDate(startDate).
                        endDate(endDate).
                            build();

        userEntity.addProject(constructionProjectEntity);

        final var assignedUserId = entityManager.persistAndFlush(userEntity).getId();

        Optional<UserEntity> userEntityOptional = repo.findById(assignedUserId);

        assertTrue(userEntityOptional.isPresent());

        UserEntity userEntityFromDb = userEntityOptional.get();
        assertEquals(assignedUserId, userEntityFromDb.getId());
        assertEquals(telegramId, userEntityFromDb.getTelegramId());
        assertEquals(userName, userEntityFromDb.getUsername());
        assertEquals(role, userEntityFromDb.getRole());

        List<ConstructionProjectEntity> projects = userEntityFromDb.getProjects();
        assertEquals(1, projects.size());

        ConstructionProjectEntity constructionProject = projects.get(0);
        assertNotNull(constructionProjectEntity.getId());
        assertEquals(projectName, constructionProject.getName());
        assertEquals(0, totalBudget.compareTo(constructionProject.getTotalBudget()));
        assertEquals(startDate, constructionProject.getStartDate());
        assertEquals(endDate, constructionProject.getEndDate());

        assertEquals(assignedUserId, constructionProject.getUser().getId());

    }

}