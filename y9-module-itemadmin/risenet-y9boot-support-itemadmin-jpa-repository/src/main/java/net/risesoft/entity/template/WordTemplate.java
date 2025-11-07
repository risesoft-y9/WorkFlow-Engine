package net.risesoft.entity.template;

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

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_WORDTEMPLATE")
@Comment("正文模板信息表")
public class WordTemplate extends ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = -7420288864269881175L;

    @Id
    @Comment("主键")
    @Column(name = "ID")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("委办局Id")
    @Column(name = "BUREAUID", length = 50)
    private String bureauId;

    @Comment("文档名称")
    @Column(name = "FILENAME", length = 50)
    private String fileName;

    @Comment("文档路径")
    @Column(name = "FILEPATH", length = 2000)
    private String filePath;

    @Comment("文件大小")
    @Column(name = "FILESIZE", length = 20)
    private String fileSize;

    @Comment("上传人Id")
    @Column(name = "PERSONID", length = 50)
    private String personId;

    @Comment("上传人")
    @Column(name = "PERSONNAME", length = 100)
    private String personName;

    @Comment("文件描述")
    @Column(name = "DESCRIBES", length = 1000)
    private String describes;

}
