package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "FF_BOOKMARKBIND")
@org.hibernate.annotations.Table(comment = "正文模板书签绑定数据库表字段信息", appliesTo = "FF_BOOKMARKBIND")
public class BookMarkBind implements Serializable {
    private static final long serialVersionUID = 3758496712029777467L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("正文模板Id")
    @Column(name = "WORDTEMPLATEID", length = 50, nullable = false)
    private String wordTemplateId;

    @org.hibernate.annotations.Comment("书签名称")
    @Column(name = "BOOKMARKNAME", length = 50, nullable = false)
    private String bookMarkName;

    @org.hibernate.annotations.Comment("书签类型")
    @Column(name = "BOOKMARKTYPE", length = 2, nullable = false)
    private int bookMarkType;

    @org.hibernate.annotations.Comment("绑定的数据库表")
    @Column(name = "TABLENAME", length = 50, nullable = false)
    private String tableName;

    @org.hibernate.annotations.Comment("绑定的数据库表字段")
    @Column(name = "COLUMNAME", length = 50, nullable = false)
    private String columnName;

    @org.hibernate.annotations.Comment("绑定的人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    @org.hibernate.annotations.Comment("绑定的人员Id")
    @Column(name = "USERID", length = 50, nullable = false)
    private String userId;

    @org.hibernate.annotations.Comment("绑定时间")
    @Column(name = "CREATETIME", length = 100)
    private String createTime;

    @org.hibernate.annotations.Comment("更新绑定时间")
    @Column(name = "UPDATETIME", length = 100)
    private String updateTime;
}
