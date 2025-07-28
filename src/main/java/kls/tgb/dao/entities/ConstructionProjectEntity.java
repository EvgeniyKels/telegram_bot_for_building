package kls.tgb.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static kls.tgb.util.StringConstants.*;

@Entity
@Table(name = CONSTRUCTION_PROJECT)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String name;

    @Column(name = TOTAL_BUDGET, precision = 12, scale = 2)
    private BigDecimal totalBudget;

    @Column(name = START_DATE)
    private LocalDate startDate;

    @Column(name = END_DATE)
    private LocalDate endDate;

    // Связи
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = USER_ID, nullable = false)
    private UserEntity user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = PROJECT, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseEntity> expenses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = PROJECT, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseCategoryEntity> categories;

    public void addExpense(ExpenseEntity expense) {
        if (isNull(expenses)) {
            expenses = new ArrayList<>();
        }
        expenses.add(expense);
        expense.setProject(this);
    }

    public void addCategory(ExpenseCategoryEntity category) {
        if (isNull(categories)) {
            categories = new ArrayList<>();
        }
        categories.add(category);
        category.setProject(this);
    }
}