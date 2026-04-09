package com.myproject.store.controller;

import com.myproject.store.model.Item;
import com.myproject.store.model.StockMovement;
import com.myproject.store.service.ItemService;
import com.myproject.store.service.StockMovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockMovementControllerTest {

    @Mock
    private StockMovementService stockMovementService;
    
    @Mock
    private ItemService itemService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StockMovementController controller;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
    }

    @Test
    void testShowStockInForm() {
        Item item = new Item();
        when(itemService.findById(1L)).thenReturn(item);

        String view = controller.showStockInForm(1L, model);
        assertEquals("stock/stock_in_form", view);
    }

    @Test
    void testHandleStockIn() {
        Item item = new Item();
        when(itemService.findById(1L)).thenReturn(item);
        when(authentication.getName()).thenReturn("user");

        String view = controller.handleStockIn(1L, 10, "Ref", authentication);
        assertEquals("redirect:/items", view);
        verify(stockMovementService, times(1)).recordStockIn(item, 10, "Ref", "user");
    }

    @Test
    void testShowStockOutForm() {
        Item item = new Item();
        when(itemService.findById(1L)).thenReturn(item);

        String view = controller.showStockOutForm(1L, model);
        assertEquals("stock/stock_out_form", view);
    }

    @Test
    void testHandleStockOutSuccess() {
        Item item = new Item();
        when(itemService.findById(1L)).thenReturn(item);
        when(authentication.getName()).thenReturn("user");

        // mock success
        when(stockMovementService.recordStockOut(item, 5, "Ref", "user")).thenReturn(new StockMovement());

        String view = controller.handleStockOut(1L, 5, "Ref", authentication, model);
        assertEquals("redirect:/items", view);
    }

    @Test
    void testHandleStockOutFailure() {
        Item item = new Item();
        when(itemService.findById(1L)).thenReturn(item);
        when(authentication.getName()).thenReturn("user");

        when(stockMovementService.recordStockOut(item, 5, "Ref", "user"))
            .thenThrow(new IllegalArgumentException("Not enough stock"));

        String view = controller.handleStockOut(1L, 5, "Ref", authentication, model);
        assertEquals("stock/stock_out_form", view);
    }

    @Test
    void testViewHistory() {
        Item item = new Item();
        when(itemService.findById(1L)).thenReturn(item);
        when(stockMovementService.getMovementsForItem(item)).thenReturn(Collections.emptyList());

        String view = controller.viewHistory(1L, model);
        assertEquals("stock/history", view);
    }
}
