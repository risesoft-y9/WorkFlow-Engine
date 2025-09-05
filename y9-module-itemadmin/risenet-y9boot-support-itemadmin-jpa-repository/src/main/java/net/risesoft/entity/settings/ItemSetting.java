package net.risesoft.entity.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 事项管理系统配置
 *
 * @author qinman
 * @date 2025/08/28
 */
@Entity
@Table(name = "FF_ITEM_SETTING")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "事项管理系统配置", appliesTo = "FF_ITEM_SETTING")
@NoArgsConstructor
@Data
public class ItemSetting extends BaseEntity {

    private static final long serialVersionUID = -5605126243237525767L;

    @Id
    @Column(name = "SETTING_KEY")
    @Comment("设置key")
    private String key;

    @Column(name = "SETTING_VALUE")
    @Comment("设置value")
    private String value;

}
