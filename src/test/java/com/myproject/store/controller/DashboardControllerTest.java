package com.myproject.store.controller;

import com.myproject.store.service.DashboardService;
import com.myproject.store.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private DashboardController controller;

    @Test
    void testDashboard() {
        when(dashboardService.getTotalItems()).thenReturn(10L);
        when(dashboardService.getTotalCategories()).thenReturn(5L);
        when(dashboardService.getTotalSuppliers()).thenReturn(2L);
        when(dashboardService.getLowStockItemCount()).thenReturn(1L);
        when(dashboardService.getRecentItems(5)).thenReturn(Collections.emptyList());
        when(dashboardService.getLowStockItems()).thenReturn(Collections.emptyList());

        assertNotNull(controller.dashboard(new ConcurrentModel()));
    }
}
