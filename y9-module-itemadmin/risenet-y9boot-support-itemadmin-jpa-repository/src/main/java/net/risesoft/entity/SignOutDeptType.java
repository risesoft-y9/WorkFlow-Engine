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
@Table(name = "FF_SIGN_OUT_DEPT_TYPE")
@Comment("委外会签单位类型表")
public class SignOutDeptType implements Serializable {

    @Id
    @Comment("单位类型Id")
    @Column(name = "DEPTTYPEID", length = 10, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String deptTypeId;

    @Comment("单位类型")
    @Column(name = "DEPTTYPE", length = 50)
    private String deptType;

    @Comment("是否禁止")
    @Column(name = "ISFORBIDDEN")
    private Integer isForbidden = 0;

    @Comment("序号")
    @Column(name = "TABINDEX")
    private Integer tabIndex;

}