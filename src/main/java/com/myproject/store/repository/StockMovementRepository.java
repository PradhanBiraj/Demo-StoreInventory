package com.myproject.store.repository;

import com.myproject.store.model.StockMovement;
import com.myproject.store.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByItemOrderByCreatedAtDesc(Item item);
}
