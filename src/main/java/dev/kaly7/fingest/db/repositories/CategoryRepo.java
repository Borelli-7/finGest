package dev.kaly7.fingest.db.repositories;

import dev.kaly7.fingest.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Category.CategoryPK> {
}
