package com.ethan.es2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {


    @GetMapping(value = "/home")
    public String home(){

        return "home";
    }
}
