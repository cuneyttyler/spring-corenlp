package com.gsu.entitylinker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller("EntityLinkerHomeController")
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "static/entity-linker/index.html";
    }

    @RequestMapping("/ed")
    public String emotionDetection() {
        return "static/emotion-detection/index.html";
    }
}
