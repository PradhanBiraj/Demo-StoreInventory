package com.myproject.store.service.impl;

import com.myproject.store.model.Item;
import com.myproject.store.repository.CategoryRepository;
import com.myproject.store.repository.ItemRepository;
import com.myproject.store.repository.SupplierRepository;
import com.myproject.store.service.DashboardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Value("${inventory.low-stock-threshold:5}")
    private int lowStockThreshold;

    public DashboardServiceImpl(ItemRepository itemRepository,
                                CategoryRepository categoryRepository,
                                SupplierRepository supplierRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public long getTotalItems() {
        return itemRepository.count();
    }

    @Override
    public long getTotalCategories() {
        return categoryRepository.count();
    }

    @Override
    public long getTotalSuppliers() {
        return supplierRepository.count();
    }

    @Override
    public long getLowStockItemCount() {
        return itemRepository.findByQuantityLessThan(lowStockThreshold).size();
    }

    @Override
    public List<Item> getRecentItems(int limit) {
        List<Item> items = itemRepository.findTop5ByOrderByCreatedAtDesc();
        return items.size() > limit ? items.subList(0, limit) : items;
    }

    @Override
    public List<Item> getLowStockItems() {
        return itemRepository.findByQuantityLessThan(lowStockThreshold);
    }
}
