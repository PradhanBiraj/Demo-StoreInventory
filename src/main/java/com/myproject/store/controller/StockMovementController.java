package com.myproject.store.controller;

import com.myproject.store.model.Item;
import com.myproject.store.model.StockMovement;
import com.myproject.store.service.ItemService;
import com.myproject.store.service.StockMovementService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockMovementController {

    private final StockMovementService stockMovementService;
    private final ItemService itemService;

    public StockMovementController(StockMovementService stockMovementService,
            ItemService itemService) {
        this.stockMovementService = stockMovementService;
        this.itemService = itemService;
    }

    @GetMapping("/in/{itemId}")
    public String showStockInForm(@PathVariable Long itemId, Model model) {
        Item item = itemService.findById(itemId);
        model.addAttribute("item", item);
        model.addAttribute("type", "IN");
        return "stock/stock_in_form";
    }

    @PostMapping("/in/{itemId}")
    public String handleStockIn(@PathVariable Long itemId,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "reason", required = false) String reason,
            Authentication authentication) {
        Item item = itemService.findById(itemId);
        String username = authentication != null ? authentication.getName() : null;
        stockMovementService.recordStockIn(item, quantity, reason, username);
        return "redirect:/items";
    }

    @GetMapping("/out/{itemId}")
    public String showStockOutForm(@PathVariable Long itemId, Model model) {
        Item item = itemService.findById(itemId);
        model.addAttribute("item", item);
        model.addAttribute("type", "OUT");
        return "stock/stock_out_form";
    }

    @PostMapping("/out/{itemId}")
    public String handleStockOut(@PathVariable Long itemId,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "reason", required = false) String reason,
            Authentication authentication,
            Model model) {
        Item item = itemService.findById(itemId);
        try {
            String username = authentication != null ? authentication.getName() : null;
            stockMovementService.recordStockOut(item, quantity, reason, username);
            return "redirect:/items";
        } catch (IllegalArgumentException e) {
            model.addAttribute("item", item);
            model.addAttribute("type", "OUT");
            model.addAttribute("error", e.getMessage());
            return "stock/stock_out_form";
        }
    }

    @GetMapping("/history/{itemId}")
    public String viewHistory(@PathVariable Long itemId, Model model) {
        Item item = itemService.findById(itemId);
        List<StockMovement> movements = stockMovementService.getMovementsForItem(item);
        model.addAttribute("item", item);
        model.addAttribute("movements", movements);
        return "stock/history";
    }
}
