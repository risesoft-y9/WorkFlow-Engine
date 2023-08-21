package net.risesoft.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String go2Index() {
        return "/main/index";
    }

    @GetMapping("/")
    public String root() {
        return "/main/index";
    }

}
