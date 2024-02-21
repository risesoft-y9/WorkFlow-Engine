package net.risesoft.service;

import net.risesoft.entity.DocumentWps;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DocumentWpsService {

    /**
     * 根据id获取正文信息
     *
     * @param id
     * @return
     */
    DocumentWps findById(String id);

    /**
     * 根据流程编号获取正文
     *
     * @param processSerialNumber
     * @return
     */
    DocumentWps findByProcessSerialNumber(String processSerialNumber);

    /**
     * 保存正文信息
     *
     * @param documentWps
     */
    void saveDocumentWps(DocumentWps documentWps);

    /**
     * 保存正文内容状态
     *
     * @param processSerialNumber
     * @param hasContent
     */
    void saveWpsContent(String processSerialNumber, String hasContent);

    /**
     * 保存正文流程实例id
     *
     * @param processSerialNumber
     * @param processInstanceId
     */
    void updateProcessInstanceId(String processSerialNumber, String processInstanceId);

}
