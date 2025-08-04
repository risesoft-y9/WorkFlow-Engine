package net.risesoft.entity.form;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Entity
@Table(name = "FF_ITEM_Y9PREFORMBIND")
@Comment("Y9前置表单与事项流程任务绑定表")
public class Y9PreFormItemBind implements Serializable {

    private static final long serialVersionUID = 6523220184234477595L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("表单ID")
    @Column(name = "FORMID", length = 38, nullable = false)
    private String formId;

    @Comment("表单名称")
    @Column(name = "FORMNAME", length = 55, nullable = false)
    private String formName;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 55, nullable = false)
    private String itemId;

}
