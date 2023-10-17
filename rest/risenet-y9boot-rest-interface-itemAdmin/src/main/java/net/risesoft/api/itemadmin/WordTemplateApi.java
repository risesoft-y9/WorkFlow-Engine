package net.risesoft.api.itemadmin;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface WordTemplateApi {
    /**
     *
     * Description: 根据唯一标示获取模板辣眼睛
     * 
     * @param id id
     * @return String
     */
    String getFilePathById(String id);
}
