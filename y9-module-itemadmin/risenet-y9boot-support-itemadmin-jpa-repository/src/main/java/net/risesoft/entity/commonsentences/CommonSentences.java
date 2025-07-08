package net.risesoft.entity.commonsentences;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "FF_COMMONSENTENCES")
@org.hibernate.annotations.Table(comment = "常用语表", appliesTo = "FF_COMMONSENTENCES")
public class CommonSentences implements Serializable {

    private static final long serialVersionUID = -3115139234478159745L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("人员guid")
    @Column(name = "USERID", length = 38, nullable = false)
    private String userId;

    @Comment("常用语内容")
    @Column(name = "CONTENT", length = 4000, nullable = false)
    private String content;

    @Comment("排序号")
    @Column(name = "TABINDEX")
    private Integer tabIndex;

    @Comment("使用次数，点击次数")
    @Column(name = "USENUMGER")
    @ColumnDefault("0")
    private Integer useNumber;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;
}
