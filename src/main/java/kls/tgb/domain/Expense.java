package kls.tgb.domain;

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
public class Expense {

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
    private ConstructionProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CATEGORY_ID, nullable = false)
    private ExpenseCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = USER_ID, nullable = false)
    private User createdBy;

}