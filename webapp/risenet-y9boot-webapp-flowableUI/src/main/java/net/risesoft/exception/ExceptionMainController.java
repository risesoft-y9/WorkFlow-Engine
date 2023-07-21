package net.risesoft.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@ControllerAdvice
public class ExceptionMainController {

    private static final Logger log = LoggerFactory.getLogger(ExceptionMainController.class);

    public ExceptionMainController() {
        log.debug("ExceptionMainController init ……");
    }

    @ExceptionHandler(value = AccessManagerException.class)
    public ModelAndView hasNoPermission(Model model, HttpServletRequest request, AccessManagerException e) {
        log.error("检测到权限异常,异常为：{}", e.getMessage());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/error/403");
        return modelAndView;
    }
}
