package net.risesoft.model.itemadmin.core;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.OpinionFrameModel;
import net.risesoft.model.itemadmin.TransactionWordModel;

/**
 * 流程详情数据
 *
 * @author qinman
 * @date 2024/11/01
 */
@Data
public class DocumentDetailMobileModel extends DocumentDetailBaseModel implements Serializable {

    private static final long serialVersionUID = -1102213532776738439L;
    /**
     * 意见框及意见详情
     */
    private List<OpinionFrameModel> opinionFrameList;

    /**
     * 附件信息
     */
    private List<AttachmentModel> attachmentList;

    /**
     * 正文信息
     */
    private TransactionWordModel transactionWord;
}
