package kls.tgb.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static kls.tgb.util.StringConstants.*;

@Entity
@Table(name = CONSTRUCTION_PROJECT)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionProject {

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
    private User user;

    @OneToMany(mappedBy = PROJECT, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = PROJECT, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseCategory> categories = new ArrayList<>();

    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setProject(this);
    }

    public void addCategory(ExpenseCategory category) {
        categories.add(category);
        category.setProject(this);
    }
}