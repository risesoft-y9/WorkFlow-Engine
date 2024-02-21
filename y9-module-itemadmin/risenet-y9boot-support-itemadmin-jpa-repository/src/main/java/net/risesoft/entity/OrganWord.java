package net.risesoft.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@org.hibernate.annotations.Table(comment = "编号标识表", appliesTo = "FF_ORGANWORD")
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

    @Transient
    private List<OrganWordProperty> organWordProperties;

    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    @Comment("生成时间")
    @Column(name = "CREATETIME")
    private String createTime;

}
