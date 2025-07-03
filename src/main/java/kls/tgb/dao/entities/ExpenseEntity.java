package kls.tgb.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import static kls.tgb.util.StringConstants.*;

@Entity
@Table(name = EXPENSE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 1000)
    private String description;

    // Связи
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PROJECT_ID, nullable = false)
    private ConstructionProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CATEGORY_ID, nullable = false)
    private ExpenseCategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = USER_ID, nullable = false)
    private UserEntity createdBy;

}