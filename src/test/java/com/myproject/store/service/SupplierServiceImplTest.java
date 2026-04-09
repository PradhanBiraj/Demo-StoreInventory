package com.myproject.store.service;

import com.myproject.store.model.Supplier;
import com.myproject.store.repository.SupplierRepository;
import com.myproject.store.service.impl.SupplierServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    @Test
    void testCrud() {
        when(supplierRepository.findAll()).thenReturn(Collections.emptyList());
        assertNotNull(supplierService.findAll());

        when(supplierRepository.findById(1L)).thenReturn(Optional.of(new Supplier()));
        assertNotNull(supplierService.findById(1L));

        when(supplierRepository.save(any(Supplier.class))).thenReturn(new Supplier());
        assertNotNull(supplierService.save(new Supplier()));

        doNothing().when(supplierRepository).deleteById(1L);
        supplierService.deleteById(1L);
        verify(supplierRepository, times(1)).deleteById(1L);
    }
}
