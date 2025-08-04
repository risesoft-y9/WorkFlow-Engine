package net.risesoft.service.opinion;

import java.util.List;

import net.risesoft.entity.opinion.OpinionSign;
import net.risesoft.model.itemadmin.OpinionSignListModel;

/**
 * @author qinman
 */
public interface OpinionSignService {

    OpinionSign saveOrUpdate(OpinionSign opinionSign);

    List<OpinionSignListModel> list(String processSerialNumber, String signDeptDetailId, String itemBox, String taskId,
        String opinionFrameMark);

    List<OpinionSign> findBySignDeptDetailIdAndOpinionFrameMark(String signDeptDetailId, String opinionFrameMark);

    List<OpinionSign> findBySignDeptDetailId(String signDeptDetailId);

    OpinionSign findById(String id);

    void deleteById(String id);
}
