package com.gsu.knowledgebase.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@RequestMapping("/")
@Controller("KnowledgeBaseHomeController")
public class HomeController {

    @RequestMapping(value={"/knowledge-base","/knowledge-base/"})
    public String home() {
        return "static/knowledge-base/index.html";
    }

    @RequestMapping("/memory")
    public String memory() {
        return "static/knowledge-base/memory.html";
    }

    @RequestMapping("/user-confirmed")
    public String userConfirmed() {
        return "static/knowledge-base/user-confirmed.html";
    }

    @RequestMapping("/confirmation-code-not-found")
    public String confirmationCodeNotFound() {
        return "static/knowledge-base/confirmation-code-not-found.html";
    }
}