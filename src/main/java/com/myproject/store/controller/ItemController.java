package com.myproject.store.controller;

import com.myproject.store.model.Category;
import com.myproject.store.model.Item;
import com.myproject.store.model.Supplier;
import com.myproject.store.service.CategoryService;
import com.myproject.store.service.ItemService;
import com.myproject.store.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    public ItemController(ItemService itemService,
                          CategoryService categoryService,
                          SupplierService supplierService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
    }

    @GetMapping
    public String listItems(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "categoryId", required = false) Long categoryId,
                            @RequestParam(value = "lowStock", required = false) Boolean lowStock,
                            Model model) {

        List<Item> items;

        if (search != null && !search.isBlank()) {
            items = itemService.searchByName(search);
        } else if (Boolean.TRUE.equals(lowStock)) {
            items = itemService.findLowStockItems();
        } else {
            items = itemService.findAll();
        }

        model.addAttribute("items", items);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("search", search);
        model.addAttribute("lowStock", lowStock);

        return "items/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("formTitle", "Create Item");
        return "items/form";
    }

    @PostMapping
    public String createItem(@ModelAttribute("item") Item item,
                             @RequestParam("categoryId") Long categoryId,
                             @RequestParam("supplierId") Long supplierId) {
        Category category = categoryService.findById(categoryId);
        Supplier supplier = supplierService.findById(supplierId);
        item.setCategory(category);
        item.setSupplier(supplier);
        itemService.save(item);
        return "redirect:/items";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("formTitle", "Edit Item");
        return "items/form";
    }

    @PostMapping("/update/{id}")
    public String updateItem(@PathVariable Long id,
                             @ModelAttribute("item") Item item,
                             @RequestParam("categoryId") Long categoryId,
                             @RequestParam("supplierId") Long supplierId) {
        item.setId(id);
        Category category = categoryService.findById(categoryId);
        Supplier supplier = supplierService.findById(supplierId);
        item.setCategory(category);
        item.setSupplier(supplier);
        itemService.save(item);
        return "redirect:/items";
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        itemService.deleteById(id);
        return "redirect:/items";
    }
}
