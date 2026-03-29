package com.myproject.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {

    @GetMapping("/error/401")
    public String unauthorized() {
        return "error/401";
    }
}
