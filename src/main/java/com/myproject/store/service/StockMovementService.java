package com.myproject.store.service;

import com.myproject.store.model.Item;
import com.myproject.store.model.StockMovement;

import java.util.List;

public interface StockMovementService {

    StockMovement recordStockIn(Item item, int quantity, String reason, String username);

    StockMovement recordStockOut(Item item, int quantity, String reason, String username);

    List<StockMovement> getMovementsForItem(Item item);
}
