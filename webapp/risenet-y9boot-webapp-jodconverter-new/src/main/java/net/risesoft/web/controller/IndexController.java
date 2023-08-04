package net.risesoft.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面跳转
 *
 * @author lizihwen
 * @date 2023-08-01
 */
@Controller
public class IndexController {

    @GetMapping("/index")
    public String go2Index() {
        return "/main/index";
    }

    @GetMapping("/record")
    public String go2Record() {
        return "/main/record";
    }

    @GetMapping("/sponsor")
    public String go2Sponsor() {
        return "/main/sponsor";
    }

    @GetMapping("/integrated")
    public String go2Integrated() {
        return "/main/integrated";
    }

    @GetMapping("/")
    public String root() {
        return "/main/index";
    }


}
