package com.myproject.store.service;

import com.myproject.store.model.Item;

import java.util.List;

public interface DashboardService {

    long getTotalItems();

    long getTotalCategories();

    long getTotalSuppliers();

    long getLowStockItemCount();

    List<Item> getRecentItems(int limit);

    List<Item> getLowStockItems();
}
