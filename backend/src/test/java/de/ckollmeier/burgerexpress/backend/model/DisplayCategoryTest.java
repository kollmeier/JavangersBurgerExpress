package de.ckollmeier.burgerexpress.backend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DisplayCategory")
class DisplayCategoryTest {

    @Test
    @DisplayName("Builder should create DisplayCategory with all properties set")
    void should_createDisplayCategoryWithAllProperties_when_usingBuilder() {
        // Given
        String id = "test-id";
        String name = "Test Category";
        String description = "Test Description";
        List<DisplayItem> displayItems = new ArrayList<>();
        String imageUrl = "test-image-url";
        boolean published = true;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        int position = 5;

        // When
        DisplayCategory category = DisplayCategory.builder()
                .id(id)
                .name(name)
                .description(description)
                .displayItems(displayItems)
                .imageUrl(imageUrl)
                .published(published)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .position(position)
                .build();

        // Then
        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertEquals(displayItems, category.getDisplayItems());
        assertEquals(imageUrl, category.getImageUrl());
        assertEquals(published, category.isPublished());
        assertEquals(createdAt, category.getCreatedAt());
        assertEquals(updatedAt, category.getUpdatedAt());
        assertEquals(position, category.getPosition());
    }

    @Test
    @DisplayName("Builder should set default values when not specified")
    void should_setDefaultValues_when_propertiesNotSpecified() {
        // When
        DisplayCategory category = DisplayCategory.builder()
                .name("Test Category")
                .description("Test Description")
                .build();

        // Then
        assertNotNull(category.getDisplayItems());
        assertTrue(category.getDisplayItems().isEmpty());
        assertFalse(category.isPublished());
        assertNotNull(category.getCreatedAt());
        assertEquals(0, category.getPosition());
    }

    @Test
    @DisplayName("compareWith should return positive when this position is greater")
    void should_returnPositive_when_thisPositionIsGreater() {
        // Given
        DisplayCategory category1 = DisplayCategory.builder()
                .position(2)
                .build();

        DisplayCategory category2 = DisplayCategory.builder()
                .position(1)
                .build();

        // When
        int result = category1.compareWith(category2);

        // Then
        assertTrue(result > 0);
    }

    @Test
    @DisplayName("compareWith should return negative when this position is less")
    void should_returnNegative_when_thisPositionIsLess() {
        // Given
        DisplayCategory category1 = DisplayCategory.builder()
                .position(1)
                .build();

        DisplayCategory category2 = DisplayCategory.builder()
                .position(2)
                .build();

        // When
        int result = category1.compareWith(category2);

        // Then
        assertTrue(result < 0);
    }

    @Test
    @DisplayName("compareWith should return zero when positions are equal")
    void should_returnZero_when_positionsAreEqual() {
        // Given
        DisplayCategory category1 = DisplayCategory.builder()
                .position(1)
                .build();

        DisplayCategory category2 = DisplayCategory.builder()
                .position(1)
                .build();

        // When
        int result = category1.compareWith(category2);

        // Then
        assertEquals(0, result);
    }

    @Test
    @DisplayName("with methods should create new instance with updated property")
    void should_createNewInstanceWithUpdatedProperty_when_usingWithMethods() {
        // Given
        DisplayCategory original = DisplayCategory.builder()
                .name("Original Name")
                .description("Original Description")
                .position(1)
                .build();

        // When
        DisplayCategory updated = original.withName("Updated Name")
                .withDescription("Updated Description")
                .withPosition(2);

        // Then
        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(2, updated.getPosition());
        
        // Original should be unchanged
        assertEquals("Original Name", original.getName());
        assertEquals("Original Description", original.getDescription());
        assertEquals(1, original.getPosition());
    }
}