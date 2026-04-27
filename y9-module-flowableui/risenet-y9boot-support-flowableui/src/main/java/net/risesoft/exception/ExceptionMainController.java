package net.risesoft.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionMainController {

    public ExceptionMainController() {
        LOGGER.debug("ExceptionMainController init ……");
    }

    @ExceptionHandler(value = AccessManagerException.class)
    public ModelAndView hasNoPermission(Model model, HttpServletRequest request, AccessManagerException e) {
        LOGGER.error("检测到权限异常,异常为：{}", e.getMessage());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/error/403");
        return modelAndView;
    }
}
