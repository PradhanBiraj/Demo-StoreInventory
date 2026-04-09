package com.myproject.store.service;

import com.myproject.store.model.Item;
import com.myproject.store.model.StockMovement;
import com.myproject.store.model.User;
import com.myproject.store.repository.ItemRepository;
import com.myproject.store.repository.StockMovementRepository;
import com.myproject.store.repository.UserRepository;
import com.myproject.store.service.impl.StockMovementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockMovementServiceImplTest {

    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    @Test
    void testRecordStockIn() {
        Item item = new Item();
        item.setQuantity(5);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(new StockMovement());
        
        assertNotNull(stockMovementService.recordStockIn(item, 5, "Ref", "user"));
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testRecordStockOutSuccess() {
        Item item = new Item();
        item.setQuantity(15);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(new StockMovement());
        
        assertNotNull(stockMovementService.recordStockOut(item, 5, "Ref", "user"));
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testRecordStockOutFailure() {
        Item item = new Item();
        item.setQuantity(2);
        
        assertThrows(IllegalArgumentException.class, () -> 
            stockMovementService.recordStockOut(item, 5, "Ref", "user"));
    }

    @Test
    void testGetMovements() {
        when(stockMovementRepository.findByItemOrderByCreatedAtDesc(any(Item.class)))
            .thenReturn(Collections.emptyList());
        assertNotNull(stockMovementService.getMovementsForItem(new Item()));
    }
}
