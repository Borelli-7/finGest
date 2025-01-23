package dev.kaly7.fingest.services;

import dev.kaly7.fingest.db.repositories.CategoryRepo;
import dev.kaly7.fingest.entities.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepo.findAll();
    }
}
