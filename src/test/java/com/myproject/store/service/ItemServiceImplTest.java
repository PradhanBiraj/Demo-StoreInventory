package com.myproject.store.service;

import com.myproject.store.model.Item;
import com.myproject.store.repository.ItemRepository;
import com.myproject.store.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
    }

    @Test
    void testFindAll() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item));
        List<Item> items = itemService.findAll();
        assertEquals(1, items.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Item found = itemService.findById(1L);
        assertNotNull(found);
        assertEquals("Test Item", found.getName());
    }

    @Test
    void testFindByIdNotFound() {
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> itemService.findById(2L));
    }

    @Test
    void testSave() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        Item saved = itemService.save(new Item());
        assertEquals(1L, saved.getId());
    }

    @Test
    void testDeleteById() {
        doNothing().when(itemRepository).deleteById(1L);
        itemService.deleteById(1L);
        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchByName() {
        when(itemRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(item));
        List<Item> items = itemService.searchByName("Test");
        assertEquals(1, items.size());
    }

    @Test
    void testFindLowStockItems() {
        when(itemRepository.findByQuantityLessThan(5)).thenReturn(Arrays.asList(item));
        List<Item> items = itemService.findLowStockItems();
        assertEquals(1, items.size());
    }

    @Test
    void testFindRecentItems() {
        Item item2 = new Item();
        item2.setId(2L);
        when(itemRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(item, item2));
        
        // When limit is greater than size
        List<Item> items = itemService.findRecentItems(5);
        assertEquals(2, items.size());

        // When limit is less than size
        List<Item> itemsLimited = itemService.findRecentItems(1);
        assertEquals(1, itemsLimited.size());
    }
}
