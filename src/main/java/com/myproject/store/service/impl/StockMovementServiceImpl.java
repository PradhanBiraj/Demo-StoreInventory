package com.myproject.store.service.impl;

import com.myproject.store.model.Item;
import com.myproject.store.model.StockMovement;
import com.myproject.store.model.User;
import com.myproject.store.repository.ItemRepository;
import com.myproject.store.repository.StockMovementRepository;
import com.myproject.store.repository.UserRepository;
import com.myproject.store.service.StockMovementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
            ItemRepository itemRepository,
            UserRepository userRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public StockMovement recordStockIn(Item item, int quantity, String reason, String username) {
        User user = userRepository.findByUsername(username).orElse(null);

        item.setQuantity(item.getQuantity() + quantity);
        itemRepository.save(item);

        StockMovement movement = new StockMovement(item, StockMovement.MovementType.IN, quantity, reason, user);
        return stockMovementRepository.save(movement);
    }

    @Override
    @Transactional
    public StockMovement recordStockOut(Item item, int quantity, String reason, String username) {
        if (quantity > item.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + item.getQuantity());
        }
        User user = userRepository.findByUsername(username).orElse(null);

        item.setQuantity(item.getQuantity() - quantity);
        itemRepository.save(item);

        StockMovement movement = new StockMovement(item, StockMovement.MovementType.OUT, quantity, reason, user);
        return stockMovementRepository.save(movement);
    }

    @Override
    public List<StockMovement> getMovementsForItem(Item item) {
        return stockMovementRepository.findByItemOrderByCreatedAtDesc(item);
    }
}
