package com.myproject.store.controller;

import com.myproject.store.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalItems", dashboardService.getTotalItems());
        model.addAttribute("totalCategories", dashboardService.getTotalCategories());
        model.addAttribute("totalSuppliers", dashboardService.getTotalSuppliers());
        model.addAttribute("lowStockCount", dashboardService.getLowStockItemCount());
        model.addAttribute("lowStockItems", dashboardService.getLowStockItems());
        model.addAttribute("recentItems", dashboardService.getRecentItems(5));

        return "dashboard";
    }
}
