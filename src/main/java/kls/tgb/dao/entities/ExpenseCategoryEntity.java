package kls.tgb.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

import static kls.tgb.util.StringConstants.*;

@Entity
@Table(name = EXPENSE_CATEGORY)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // Связи
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PROJECT_ID, nullable = false)
    private ConstructionProjectEntity project;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = CATEGORY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseEntity> expenses = new ArrayList<>();

}