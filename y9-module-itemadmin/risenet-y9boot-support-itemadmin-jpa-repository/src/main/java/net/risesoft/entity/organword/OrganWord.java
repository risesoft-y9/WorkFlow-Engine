package net.risesoft.entity.organword;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "FF_ORGANWORD")
@Comment("编号标识表")
public class OrganWord implements Serializable {

    private static final long serialVersionUID = 991194093927556827L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("编号标识标志")
    @Column(name = "CUSTOM", length = 50, unique = true)
    private String custom;

    @Comment("编号标识名字")
    @Column(name = "NAME", length = 50)
    private String name;

    @Comment("编号类型")
    @Column(name = "NUMBERTYPE", length = 50)
    private String numberType;

    @Comment("编号位数")
    @Column(name = "NUMBERLENGTH")
    private Integer numberLength;

    @Transient
    private List<OrganWordProperty> organWordProperties;

    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    @Comment("生成时间")
    @Column(name = "CREATETIME")
    private String createTime;

}
