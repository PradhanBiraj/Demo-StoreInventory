package com.myproject.store.controller;

import com.myproject.store.model.Supplier;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierControllerTest {

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierController controller;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
    }

    @Test
    void testListSuppliers() {
        when(supplierService.findAll()).thenReturn(Collections.emptyList());
        assertNotNull(controller.listSuppliers(model));
    }

    @Test
    void testShowCreateForm() {
        assertNotNull(controller.showCreateForm(model));
    }

    @Test
    void testCreateSupplier() {
        assertNotNull(controller.createSupplier(new Supplier()));
        verify(supplierService, times(1)).save(any(Supplier.class));
    }

    @Test
    void testShowEditForm() {
        when(supplierService.findById(1L)).thenReturn(new Supplier());
        assertNotNull(controller.showEditForm(1L, model));
    }

    @Test
    void testUpdateSupplier() {
        assertNotNull(controller.updateSupplier(1L, new Supplier()));
        verify(supplierService, times(1)).save(any(Supplier.class));
    }

    @Test
    void testDeleteSupplier() {
        assertNotNull(controller.deleteSupplier(1L));
        verify(supplierService, times(1)).deleteById(1L);
    }
}
