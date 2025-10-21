package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;

import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.SpeakInfo;
import net.risesoft.entity.entrust.EntrustHistory;
import net.risesoft.model.itemadmin.EntrustHistoryModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.itemadmin.SpeakInfoModel;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class ItemAdminModelConvertUtil {

    public static List<EntrustHistoryModel> entrustHistoryList2ModelList(List<EntrustHistory> ehList) {
        List<EntrustHistoryModel> ehModelList = new ArrayList<>();
        for (EntrustHistory entrustHistory : ehList) {
            EntrustHistoryModel entrustHistory2Model = new EntrustHistoryModel();
            Y9BeanUtil.copyProperties(entrustHistory, entrustHistory2Model);
            ehModelList.add(entrustHistory2Model);
        }
        return ehModelList;
    }

    public static List<ProcessTrackModel> processTrackList2ModelList(List<ProcessTrack> ptList) {
        List<ProcessTrackModel> ptModelList = new ArrayList<>();
        for (ProcessTrack processTrack : ptList) {
            ProcessTrackModel processTrack2Model = new ProcessTrackModel();
            Y9BeanUtil.copyProperties(processTrack, processTrack2Model);
            ptModelList.add(processTrack2Model);
        }
        return ptModelList;
    }

    public static List<SpeakInfoModel> speakInfoList2ModelList(List<SpeakInfo> siList) {
        List<SpeakInfoModel> siModelList = new ArrayList<>();
        for (SpeakInfo speakInfo : siList) {
            SpeakInfoModel speakInfo2Model = new SpeakInfoModel();
            Y9BeanUtil.copyProperties(speakInfo, speakInfo2Model);
            siModelList.add(speakInfo2Model);
        }
        return siModelList;
    }

    public static List<ActRuDetailModel> convertActRuDetailsToModels(List<ActRuDetail> actRuDetails) {
        List<ActRuDetailModel> modelList = new ArrayList<>();
        for (ActRuDetail actRuDetail : actRuDetails) {
            ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, actRuDetailModel);
            modelList.add(actRuDetailModel);
        }
        return modelList;
    }
}
