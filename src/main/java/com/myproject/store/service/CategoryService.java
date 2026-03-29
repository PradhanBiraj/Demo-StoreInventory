package com.myproject.store.service;

import com.myproject.store.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    Category findById(Long id);

    Category save(Category category);

    void deleteById(Long id);

    boolean existsByName(String name);
}
