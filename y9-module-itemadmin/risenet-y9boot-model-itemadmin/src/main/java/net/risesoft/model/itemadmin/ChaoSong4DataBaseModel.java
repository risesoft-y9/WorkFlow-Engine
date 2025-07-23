package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

import net.risesoft.enums.ChaoSongStatusEnum;

/**
 * 抄送信息模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ChaoSong4DataBaseModel extends ChaoSongBaseModel implements Serializable {

    private static final long serialVersionUID = -7436190610436168357L;

    /**
     * 传阅的状态
     */
    private ChaoSongStatusEnum status = ChaoSongStatusEnum.NEW;
}
