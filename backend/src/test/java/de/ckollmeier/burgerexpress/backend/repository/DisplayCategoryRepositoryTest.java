package de.ckollmeier.burgerexpress.backend.repository;

import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("DisplayCategoryRepository")
class DisplayCategoryRepositoryTest {

    @Autowired
    private DisplayCategoryRepository displayCategoryRepository;

    @BeforeEach
    void setUp() {
        displayCategoryRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        displayCategoryRepository.deleteAll();
    }

    @Test
    @DisplayName("findAllByOrderByPositionAscCreatedAtDesc should return categories ordered by position and then by createdAt")
    void should_returnCategoriesOrderedByPositionAndCreatedAt_when_findAllByOrderByPositionAscCreatedAtDesc() {
        // Given
        Instant now = Instant.now();
        
        // Create categories with different positions and creation times
        DisplayCategory category1 = DisplayCategory.builder()
                .name("Category 1")
                .description("Description 1")
                .position(2)
                .createdAt(now.minus(1, ChronoUnit.HOURS))
                .build();
        
        DisplayCategory category2 = DisplayCategory.builder()
                .name("Category 2")
                .description("Description 2")
                .position(1)
                .createdAt(now.minus(2, ChronoUnit.HOURS))
                .build();
        
        DisplayCategory category3 = DisplayCategory.builder()
                .name("Category 3")
                .description("Description 3")
                .position(1)
                .createdAt(now.minus(1, ChronoUnit.MINUTES))
                .build();
        
        // Save categories in random order
        displayCategoryRepository.save(category1);
        displayCategoryRepository.save(category2);
        displayCategoryRepository.save(category3);
        
        // When
        List<DisplayCategory> result = displayCategoryRepository.findAllByOrderByPositionAscCreatedAtDesc();
        
        // Then
        assertEquals(3, result.size());
        
        // First should be position 1, newest creation time
        assertEquals("Category 3", result.get(0).getName());
        
        // Second should be position 1, older creation time
        assertEquals("Category 2", result.get(1).getName());
        
        // Third should be position 2
        assertEquals("Category 1", result.get(2).getName());
    }

    @Test
    @DisplayName("save should store a category and assign an ID")
    void should_storeCategory_when_save() {
        // Given
        DisplayCategory category = DisplayCategory.builder()
                .name("Test Category")
                .description("Test Description")
                .position(1)
                .build();
        
        // When
        DisplayCategory savedCategory = displayCategoryRepository.save(category);
        
        // Then
        assertNotNull(savedCategory.getId());
        assertEquals("Test Category", savedCategory.getName());
        assertEquals("Test Description", savedCategory.getDescription());
        assertEquals(1, savedCategory.getPosition());
        
        // Verify it can be retrieved
        DisplayCategory retrievedCategory = displayCategoryRepository.findById(savedCategory.getId()).orElse(null);
        assertNotNull(retrievedCategory);
        assertEquals(savedCategory.getId(), retrievedCategory.getId());
        assertEquals(savedCategory.getName(), retrievedCategory.getName());
    }

    @Test
    @DisplayName("findById should return the category with the given ID")
    void should_returnCategory_when_findById() {
        // Given
        DisplayCategory category = DisplayCategory.builder()
                .name("Test Category")
                .description("Test Description")
                .position(1)
                .build();
        
        DisplayCategory savedCategory = displayCategoryRepository.save(category);
        
        // When
        DisplayCategory foundCategory = displayCategoryRepository.findById(savedCategory.getId()).orElse(null);
        
        // Then
        assertNotNull(foundCategory);
        assertEquals(savedCategory.getId(), foundCategory.getId());
        assertEquals(savedCategory.getName(), foundCategory.getName());
        assertEquals(savedCategory.getDescription(), foundCategory.getDescription());
    }

    @Test
    @DisplayName("deleteById should remove the category with the given ID")
    void should_removeCategory_when_deleteById() {
        // Given
        DisplayCategory category = DisplayCategory.builder()
                .name("Test Category")
                .description("Test Description")
                .position(1)
                .build();
        
        DisplayCategory savedCategory = displayCategoryRepository.save(category);
        
        // When
        displayCategoryRepository.deleteById(savedCategory.getId());
        
        // Then
        assertFalse(displayCategoryRepository.findById(savedCategory.getId()).isPresent());
    }
}