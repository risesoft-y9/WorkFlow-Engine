package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 套红模板信息
 *
 * @author mengjuhua
 * @date 2024/06/24
 */
@Data
public class TaoHongTemplateModel implements Serializable {
    private static final long serialVersionUID = 4710567853996348931L;

    /** 主键 */
    private String templateGuid;

    /** 委办局GUID */
    private String bureauGuid;

    /** 委办局名称 */
    private String bureauName;

    /** 文件内容 */
    private byte[] templateContent;

    /** 文件名 */
    private String templateFileName;

    /** 模板类型 */
    private String templateType;

    private String hasDocumentTemplate;

}
