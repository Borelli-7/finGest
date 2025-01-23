package dev.kaly7.fingest.controllers;

import dev.kaly7.fingest.entities.Category;
import dev.kaly7.fingest.services.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/resources/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

}
