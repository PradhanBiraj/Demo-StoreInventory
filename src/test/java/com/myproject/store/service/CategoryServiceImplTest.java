package com.myproject.store.service;

import com.myproject.store.model.Category;
import com.myproject.store.repository.CategoryRepository;
import com.myproject.store.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category cat;

    @BeforeEach
    void setUp() {
        cat = new Category();
        cat.setId(1L);
        cat.setName("Test Cat");
    }

    @Test
    void testFindAll() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat));
        assertEquals(1, categoryService.findAll().size());
    }

    @Test
    void testFindById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        assertEquals(cat, categoryService.findById(1L));
    }

    @Test
    void testSave() {
        when(categoryRepository.save(any(Category.class))).thenReturn(cat);
        assertEquals(cat, categoryService.save(new Category()));
    }

    @Test
    void testDelete() {
        doNothing().when(categoryRepository).deleteById(1L);
        categoryService.deleteById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExistsByName() {
        when(categoryRepository.existsByName("Test Cat")).thenReturn(true);
        assertTrue(categoryService.existsByName("Test Cat"));
    }
}
