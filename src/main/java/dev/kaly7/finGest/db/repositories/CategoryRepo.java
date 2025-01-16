package dev.kaly7.finGest.db.repositories;

import dev.kaly7.finGest.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Category.CategoryPK> {
}
