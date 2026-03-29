package com.myproject.store.repository;

import com.myproject.store.model.Item;
import com.myproject.store.model.Category;
import com.myproject.store.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContainingIgnoreCase(String name);

    List<Item> findByCategory(Category category);

    List<Item> findBySupplier(Supplier supplier);

    List<Item> findByQuantityLessThan(Integer threshold);

    List<Item> findTop5ByOrderByCreatedAtDesc();
}
