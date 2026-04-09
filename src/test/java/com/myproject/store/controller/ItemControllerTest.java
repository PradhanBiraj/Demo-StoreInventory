package com.myproject.store.controller;

import com.myproject.store.model.Category;
import com.myproject.store.model.Item;
import com.myproject.store.model.Supplier;
import com.myproject.store.service.CategoryService;
import com.myproject.store.service.ItemService;
import com.myproject.store.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private ItemController itemController;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
    }

    @Test
    void testListItems() {
        when(itemService.findAll()).thenReturn(Collections.emptyList());
        when(categoryService.findAll()).thenReturn(Collections.emptyList());

        String viewName = itemController.listItems(null, null, null, model);
        assertEquals("items/list", viewName);

        when(itemService.searchByName("test")).thenReturn(Collections.emptyList());
        viewName = itemController.listItems("test", null, null, model);
        assertEquals("items/list", viewName);

        when(itemService.findLowStockItems()).thenReturn(Collections.emptyList());
        viewName = itemController.listItems("", null, true, model);
        assertEquals("items/list", viewName);
    }

    @Test
    void testShowCreateForm() {
        String viewName = itemController.showCreateForm(model);
        assertEquals("items/form", viewName);
    }

    @Test
    void testCreateItem() {
        Category cat = new Category();
        Supplier sup = new Supplier();
        when(categoryService.findById(1L)).thenReturn(cat);
        when(supplierService.findById(1L)).thenReturn(sup);

        String viewName = itemController.createItem(new Item(), 1L, 1L);
        assertEquals("redirect:/items", viewName);
        verify(itemService, times(1)).save(any(Item.class));
    }

    @Test
    void testShowEditForm() {
        when(itemService.findById(1L)).thenReturn(new Item());
        String viewName = itemController.showEditForm(1L, model);
        assertEquals("items/form", viewName);
    }

    @Test
    void testUpdateItem() {
        Category cat = new Category();
        Supplier sup = new Supplier();
        when(categoryService.findById(1L)).thenReturn(cat);
        when(supplierService.findById(1L)).thenReturn(sup);

        String viewName = itemController.updateItem(1L, new Item(), 1L, 1L);
        assertEquals("redirect:/items", viewName);
        verify(itemService, times(1)).save(any(Item.class));
    }

    @Test
    void testDeleteItem() {
        String viewName = itemController.deleteItem(1L);
        assertEquals("redirect:/items", viewName);
        verify(itemService, times(1)).deleteById(1L);
    }
}
