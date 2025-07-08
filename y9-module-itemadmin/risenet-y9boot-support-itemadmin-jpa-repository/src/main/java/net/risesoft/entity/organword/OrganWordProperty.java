package net.risesoft.entity.organword;

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
@Comment("编号标识对应的机关代字表")
@Table(name = "FF_ORGANWORD_PROPTY")
public class OrganWordProperty implements Serializable {

    private static final long serialVersionUID = 8837558736331688025L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("编号标识Id")
    @Column(name = "ORGANWORDID", length = 50)
    private String organWordId;

    @Comment("机关代字名字")
    @Column(name = "NAME", length = 50)
    private String name;

    @Comment("初始值")
    @Column(name = "INITNUMBER", length = 10, nullable = false)
    private Integer initNumber;

    @Comment("序号")
    @Column(name = "TABINDEX", length = 10, nullable = false)
    private Integer tabIndex;

}
