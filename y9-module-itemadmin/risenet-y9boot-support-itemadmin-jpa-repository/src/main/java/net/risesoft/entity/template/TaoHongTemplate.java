package net.risesoft.entity.template;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Comment("套红模板信息表")
@Table(name = "FF_TAOHONGTEMPLATE")
public class TaoHongTemplate implements Serializable {

    private static final long serialVersionUID = -9021379988316606779L;

    @Id
    @Comment("主键")
    @Column(name = "TEMPLATE_GUID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String templateGuid;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @Comment("委办局GUID")
    @Column(name = "BUREAU_GUID", length = 38)
    private String bureauGuid;

    @Comment("委办局名称")
    @Column(name = "BUREAU_NAME", length = 38)
    private String bureauName;

    @Comment("上传人")
    @Column(name = "USERID", length = 38)
    private String userId;

    @Comment("上传时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date uploadTime;

    @Comment("文件内容")
    @Lob
    @Column(name = "TEMPLATE_CONTENT")
    private byte[] templateContent;

    @Comment("文件名")
    @Column(name = "TEMPLATE_FILENAME", length = 100)
    private String templateFileName;

    @Comment("模板类型")
    @Column(name = "TEMPLATE_TYPE", length = 100)
    private String templateType;

    @Comment("排序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    // 上传人
    @Transient
    private String userName;

}
