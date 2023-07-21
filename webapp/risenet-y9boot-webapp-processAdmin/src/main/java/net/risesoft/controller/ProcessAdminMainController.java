package net.risesoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class ProcessAdminMainController {

    /**
     * 进入后台管理主页面
     * 
     * @return
     */
    @RequestMapping(value = "/main/index")
    public String managerIndex(Model model) {
        return "/main/index";
    }

}