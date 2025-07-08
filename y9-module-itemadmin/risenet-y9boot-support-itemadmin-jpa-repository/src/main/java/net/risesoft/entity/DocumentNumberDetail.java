package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "FF_DOCUMENTNUMBERDETAIL")
@Comment("文号信息表")
public class DocumentNumberDetail implements Serializable {

    private static final long serialVersionUID = -223673649868267898L;

    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 文号年份
     */
    @Comment("文号年份")
    @Column(name = "CALENDARYEAR", length = 10, nullable = false)
    private Integer calendarYear;

    /**
     * 文号的序号长度
     */
    @Comment("文号的序号长度")
    @Column(name = "NUMLENGTH", length = 10)
    private Integer numLength;

    /**
     * 文号的序列号初始化值
     */
    @Comment("文号的序列号初始化值")
    @Column(name = "SEQUENCEINITVALUE", length = 50)
    private Integer sequenceInitValue;

    @Comment("租户id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;
}
