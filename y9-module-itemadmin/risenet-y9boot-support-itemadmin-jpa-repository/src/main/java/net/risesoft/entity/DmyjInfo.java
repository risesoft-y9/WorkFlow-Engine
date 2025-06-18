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

@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_DMYJ_INFO")
@Comment("定密依据信息表")
public class DmyjInfo implements Serializable {

    private static final long serialVersionUID = 6576490307775627358L;

    @Id
    @Comment("主键ID")
    @Column(name = "dmyjid", length = 50)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("定密依据密级")
    @Column(name = "dmyjmiji", length = 50)
    private String dmyjmiji;

    @Comment("定密依据名称")
    @Column(name = "dmyjmc", length = 50)
    private String dmyjmc;

    @Comment("定密依据司局")
    @Column(name = "dmyjsj", length = 100)
    private String dmyjsj;

    @Comment("是否删除")
    @Column(name = "isdelete", length = 10)
    private String isdelete = "0";
}
