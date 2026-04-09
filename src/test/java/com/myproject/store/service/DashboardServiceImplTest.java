package com.myproject.store.service;

import com.myproject.store.repository.CategoryRepository;
import com.myproject.store.repository.ItemRepository;
import com.myproject.store.repository.SupplierRepository;
import com.myproject.store.service.impl.DashboardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void testDashboardMethods() {
        ReflectionTestUtils.setField(dashboardService, "lowStockThreshold", 5);

        when(itemRepository.count()).thenReturn(10L);
        when(categoryRepository.count()).thenReturn(5L);
        when(supplierRepository.count()).thenReturn(2L);
        when(itemRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(Collections.emptyList());
        when(itemRepository.findByQuantityLessThan(5)).thenReturn(Collections.singletonList(new com.myproject.store.model.Item()));

        assertEquals(10L, dashboardService.getTotalItems());
        assertEquals(5L, dashboardService.getTotalCategories());
        assertEquals(2L, dashboardService.getTotalSuppliers());
        assertEquals(1L, dashboardService.getLowStockItemCount());
        assertNotNull(dashboardService.getRecentItems(5));
        assertNotNull(dashboardService.getLowStockItems());
    }
}
