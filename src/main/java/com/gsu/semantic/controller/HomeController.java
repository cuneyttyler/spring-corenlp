package com.gsu.semantic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("SemanticHomeController")
public class HomeController {

    @RequestMapping("/semantic")
    public String home() {
        return "static/semantic/index.html";
    }

    @RequestMapping("/historicalIstanbul")
    public String historicalIstanbul() {
        return "static/semantic/historical_istanbul.html";
    }

}