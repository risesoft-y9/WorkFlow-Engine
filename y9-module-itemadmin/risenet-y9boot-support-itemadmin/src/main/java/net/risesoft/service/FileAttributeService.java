package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.FileAttribute;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface FileAttributeService {

    /**
     * 根据pcode获取文件属性
     * 
     * @param pcode
     * @return
     */
    List<FileAttribute> getFileAttribute(String pcode);
}
