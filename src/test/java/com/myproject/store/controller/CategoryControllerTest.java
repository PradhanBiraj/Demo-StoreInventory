package com.myproject.store.controller;

import com.myproject.store.model.Category;
import com.myproject.store.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
    }

    @Test
    void testListCategories() {
        when(categoryService.findAll()).thenReturn(Collections.emptyList());
        assertNotNull(categoryController.listCategories(model));
    }

    @Test
    void testShowCreateForm() {
        assertNotNull(categoryController.showCreateForm(model));
    }

    @Test
    void testCreateCategory() {
        assertNotNull(categoryController.createCategory(new Category()));
        verify(categoryService, times(1)).save(any(Category.class));
    }

    @Test
    void testShowEditForm() {
        when(categoryService.findById(1L)).thenReturn(new Category());
        assertNotNull(categoryController.showEditForm(1L, model));
    }

    @Test
    void testUpdateCategory() {
        assertNotNull(categoryController.updateCategory(1L, new Category()));
        verify(categoryService, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        assertNotNull(categoryController.deleteCategory(1L));
        verify(categoryService, times(1)).deleteById(1L);
    }
}
