package net.risesoft.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
public class EntrustItemModel implements Serializable {

    private static final long serialVersionUID = 3473775314284668064L;

    /**
     * 事项Id
     */
    private String itemId;

    /**
     * 事项名称
     */
    private String itemName;

    /**
     * 委托人Id
     */
    private String ownerId;

    /**
     * 是否委托了
     */
    private Boolean isEntrust;
}
