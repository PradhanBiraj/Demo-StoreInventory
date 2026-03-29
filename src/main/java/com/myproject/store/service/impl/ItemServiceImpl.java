package com.myproject.store.service.impl;

import com.myproject.store.model.Item;
import com.myproject.store.repository.ItemRepository;
import com.myproject.store.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<Item> searchByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Item> findLowStockItems() {
        // threshold will be handled by DashboardService or using a config if needed
        return itemRepository.findByQuantityLessThan(5);
    }

    @Override
    public List<Item> findRecentItems(int limit) {
        List<Item> items = itemRepository.findTop5ByOrderByCreatedAtDesc();
        return items.size() > limit ? items.subList(0, limit) : items;
    }
}
