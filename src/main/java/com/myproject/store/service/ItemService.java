package com.myproject.store.service;

import com.myproject.store.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> findAll();

    Item findById(Long id);

    Item save(Item item);

    void deleteById(Long id);

    List<Item> searchByName(String name);

    List<Item> findLowStockItems();

    List<Item> findRecentItems(int limit);
}
