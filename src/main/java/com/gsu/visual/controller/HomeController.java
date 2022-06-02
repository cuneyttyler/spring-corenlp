package com.gsu.visual.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by cnytync on 11/05/2017.
 */
@Controller("VisualHomeController")
@RequestMapping("/visual")
public class HomeController {

    @RequestMapping(value = "/emergentTown")
    public String emergentTown() {
        return "static/visual/emergent_town.html";
    }
}
