package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_SECRET_LEVEL_RECORD")
@org.hibernate.annotations.Table(comment = "密级记录表", appliesTo = "FF_SECRET_LEVEL_RECORD")
public class SecretLevelRecord implements Serializable {

    private static final long serialVersionUID = -2859284532731967688L;

    @Id
    @Comment("主键ID")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 38, nullable = false)
    private String processSerialNumber;

    @Comment("密级")
    @Column(name = "SECRET_LEVEL", length = 50)
    private String secretLevel;

    @Comment("定密依据")
    @Column(name = "SECRET_BASIS", length = 50)
    private String secretBasis;

    @Comment("具体事项")
    @Column(name = "SECRET_ITEM", length = 200)
    private String secretItem;

    @Comment("说明")
    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    @Comment("创建人Id")
    @Column(name = "CREATE_USERID", length = 50)
    private String createUserId;

    @Comment("创建时间")
    @Column(name = "CREATE_TIME", length = 50)
    private String createTime;
}
