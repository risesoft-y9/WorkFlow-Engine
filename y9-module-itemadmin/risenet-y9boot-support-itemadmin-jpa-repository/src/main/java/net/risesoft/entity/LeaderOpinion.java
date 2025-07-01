package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @date 2024/11/11
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_LEADER_OPINION")
@org.hibernate.annotations.Table(comment = "领导批示", appliesTo = "FF_LEADER_OPINION")
public class LeaderOpinion implements Serializable {

    private static final long serialVersionUID = -5689476613176053832L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程实例编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("批示领导岗位名称")
    @Column(name = "POSITIONNAME", length = 50)
    private String positionName;

    @Comment("批示内容")
    @Column(name = "OPINIONCONTENT", length = 500)
    private String opinionContent;

    @Comment("批示日期")
    @Column(name = "OPINIONDATE", length = 50)
    private String opinionDate;

    @Comment("文件仓库Id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    @Comment("文件名称")
    @Column(name = "FILENAME", length = 200)
    private String fileName;

    @Comment("上传人")
    @Column(name = "PERSONNAME", length = 50)
    private String personName;

    @Comment("上传人员Id")
    @Column(name = "PERSONID", length = 50)
    private String personId;

    @Comment("生成时间")
    @Column(name = "CREATEDATE", length = 50)
    private String createDate;

    @Comment("修改时间")
    @Column(name = "UPDATEDATE", length = 50)
    private String updateDate;
}
