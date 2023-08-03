package net.risesoft.service;

import org.springframework.ui.Model;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public interface FilePreview {
    /**
     * Description:
     * 
     * @param url
     * @param model
     * @return
     */
    String filePreviewHandle(String url, Model model);
}
