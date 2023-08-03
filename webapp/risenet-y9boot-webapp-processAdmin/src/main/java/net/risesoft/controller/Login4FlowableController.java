package net.risesoft.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Controller
@RequestMapping("/login4Flowable")
public class Login4FlowableController {

    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Map<String, Object> logout(HttpServletRequest request, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        if (session != null) {
            session.invalidate();
        }
        map.put("y9LogoutUrl", Y9Context.getLogoutUrl());
        map.put("success", true);
        return map;
    }
}
