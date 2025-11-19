package com.expense.config;

import com.expense.model.Category;
import com.expense.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Data initializer to ensure predefined categories exist in the database.
 * This runs on application startup and creates categories if they don't exist.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final CategoryRepository categoryRepository;
    
    @Override
    public void run(String... args) {
        initializeCategories();
    }
    
    /**
     * Initialize predefined expense categories if they don't exist.
     * Categories: Travel, Meals, Office Supplies, Equipment, Other
     */
    private void initializeCategories() {
        List<CategoryData> predefinedCategories = Arrays.asList(
                new CategoryData("Travel", "Transportation, accommodation, and travel-related expenses"),
                new CategoryData("Meals", "Food and beverage expenses including client meals"),
                new CategoryData("Office Supplies", "Stationery, office equipment, and supplies"),
                new CategoryData("Equipment", "Computer hardware, software, and technical equipment"),
                new CategoryData("Other", "Miscellaneous expenses not covered by other categories")
        );
        
        for (CategoryData categoryData : predefinedCategories) {
            if (categoryRepository.findByName(categoryData.name).isEmpty()) {
                Category category = new Category();
                category.setName(categoryData.name);
                category.setDescription(categoryData.description);
                category.setCreatedAt(LocalDateTime.now());
                categoryRepository.save(category);
                log.info("Created category: {}", categoryData.name);
            }
        }
        
        log.info("Category initialization complete. Total categories: {}", categoryRepository.count());
    }
    
    /**
     * Helper record to hold category data.
     */
    private record CategoryData(String name, String description) {}
}
