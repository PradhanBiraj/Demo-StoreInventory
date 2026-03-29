package com.myproject.store.service;

import com.myproject.store.model.Supplier;

import java.util.List;

public interface SupplierService {

    List<Supplier> findAll();

    Supplier findById(Long id);

    Supplier save(Supplier supplier);

    void deleteById(Long id);
}
