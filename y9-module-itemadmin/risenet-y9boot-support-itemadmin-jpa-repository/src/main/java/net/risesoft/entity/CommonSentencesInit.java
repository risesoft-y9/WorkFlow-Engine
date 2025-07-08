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
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_COMMONSENTENCESINIT")
@org.hibernate.annotations.Table(comment = "个人常用语初始化表", appliesTo = "FF_COMMONSENTENCESINIT")
public class CommonSentencesInit implements Serializable {

    private static final long serialVersionUID = -1547137719113783741L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("人员id")
    @Column(name = "USERID", length = 38, nullable = false)
    private String userId;
}
