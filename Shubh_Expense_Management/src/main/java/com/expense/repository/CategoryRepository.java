package com.expense.repository;

import com.expense.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Category entity.
 * Provides database access methods for expense category management.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find a category by its name.
     * Used for category validation and lookup.
     *
     * @param name the category name to search for
     * @return Optional containing the category if found, empty otherwise
     */
    Optional<Category> findByName(String name);
}
