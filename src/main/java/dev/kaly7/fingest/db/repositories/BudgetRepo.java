package dev.kaly7.fingest.db.repositories;

import dev.kaly7.fingest.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepo extends JpaRepository<Budget, Integer> {
}
