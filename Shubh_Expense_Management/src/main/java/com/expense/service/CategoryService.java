package com.expense.service;

import com.expense.dto.CategoryResponse;
import com.expense.model.Category;
import com.expense.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing expense categories.
 * Handles category retrieval and validation operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Retrieve all available expense categories.
     * 
     * @return List of CategoryResponse DTOs containing all categories
     */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Validate that a category exists by its ID.
     * 
     * @param categoryId the ID of the category to validate
     * @return true if the category exists, false otherwise
     */
    public boolean categoryExists(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
    
    /**
     * Validate that a category exists by its name.
     * 
     * @param categoryName the name of the category to validate
     * @return true if the category exists, false otherwise
     */
    public boolean categoryExistsByName(String categoryName) {
        return categoryRepository.findByName(categoryName).isPresent();
    }
    
    /**
     * Map Category entity to CategoryResponse DTO.
     * 
     * @param category the Category entity to map
     * @return CategoryResponse DTO
     */
    private CategoryResponse mapToCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
