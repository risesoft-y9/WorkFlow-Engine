package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;

import net.risesoft.entity.CalendarConfig;
import net.risesoft.entity.Entrust;
import net.risesoft.entity.EntrustHistory;
import net.risesoft.entity.Opinion;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.SpeakInfo;
import net.risesoft.entity.TransactionFile;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.model.itemadmin.EntrustHistoryModel;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.itemadmin.SpeakInfoModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public class ItemAdminModelConvertUtil {

    public static AttachmentModel attachment2Model(TransactionFile transactionFile) {
        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setDescribes(transactionFile.getDescribes());
        attachmentModel.setFileStoreId(transactionFile.getFileStoreId());
        attachmentModel.setFileSize(transactionFile.getFileSize());
        attachmentModel.setFileSource(transactionFile.getFileSource());
        attachmentModel.setFileType(transactionFile.getFileType());
        attachmentModel.setId(transactionFile.getId());
        attachmentModel.setName(transactionFile.getName());
        attachmentModel.setPersonId(transactionFile.getPersonId());
        attachmentModel.setPersonName(transactionFile.getPersonName());
        attachmentModel.setDeptId(transactionFile.getDeptId());
        attachmentModel.setDeptName(transactionFile.getDeptName());
        attachmentModel.setProcessInstanceId(transactionFile.getProcessInstanceId());
        attachmentModel.setProcessSerialNumber(transactionFile.getProcessSerialNumber());
        attachmentModel.setSerialNumber(transactionFile.getSerialNumber());
        attachmentModel.setTaskId(transactionFile.getTaskId());
        attachmentModel.setUploadTime(transactionFile.getUploadTime());
        return attachmentModel;
    }

    public static List<AttachmentModel> attachmentList2ModelList(List<TransactionFile> transactionList) {
        List<AttachmentModel> transactionModelList = new ArrayList<AttachmentModel>();
        for (TransactionFile transactionFile : transactionList) {
            transactionModelList.add(attachment2Model(transactionFile));
        }
        return transactionModelList;
    }

    public static TransactionFile attachmentModel2TransactionFile(AttachmentModel attachmentModel) {
        TransactionFile transactionFile = new TransactionFile();
        transactionFile.setDescribes(attachmentModel.getDescribes());
        transactionFile.setFileStoreId(attachmentModel.getFileStoreId());
        transactionFile.setFileSize(attachmentModel.getFileSize());
        transactionFile.setFileSource(attachmentModel.getFileSource());
        transactionFile.setFileType(attachmentModel.getFileType());
        transactionFile.setId(attachmentModel.getId());
        transactionFile.setName(attachmentModel.getName());
        transactionFile.setPersonId(attachmentModel.getPersonId());
        transactionFile.setPersonName(attachmentModel.getPersonName());
        transactionFile.setDeptId(attachmentModel.getDeptId());
        transactionFile.setDeptName(attachmentModel.getDeptName());
        transactionFile.setProcessInstanceId(attachmentModel.getProcessInstanceId());
        transactionFile.setProcessSerialNumber(attachmentModel.getProcessSerialNumber());
        transactionFile.setSerialNumber(attachmentModel.getSerialNumber());
        transactionFile.setTaskId(attachmentModel.getTaskId());
        return transactionFile;
    }

    public static CalendarConfigModel calendarConfig2CalendarConfigModel(CalendarConfig calendarConfig) {
        CalendarConfigModel calendarConfigModel = new CalendarConfigModel();
        if (calendarConfig != null) {
            calendarConfigModel.setId(calendarConfig.getId());
            calendarConfigModel.setEveryYearHoliday(calendarConfig.getEveryYearHoliday());
            calendarConfigModel.setWeekend2WorkingDay(calendarConfig.getWeekend2WorkingDay());
            calendarConfigModel.setWorkingDay2Holiday(calendarConfig.getWorkingDay2Holiday());
            calendarConfigModel.setYear(calendarConfig.getYear());
        }
        return calendarConfigModel;
    }

    public static EntrustModel entrust2Model(Entrust entrust) {
        EntrustModel em = new EntrustModel();
        em.setId(entrust.getId());
        em.setAssigneeId(entrust.getAssigneeId());
        em.setAssigneeName(entrust.getAssigneeName());
        em.setCreatTime(entrust.getCreatTime());
        em.setUsed(entrust.getUsed());
        em.setEndTime(entrust.getEndTime());
        em.setItemId(entrust.getItemId());
        em.setItemName(entrust.getItemName());
        em.setOwnerId(entrust.getOwnerId());
        em.setOwnerName(entrust.getOwnerName());
        em.setStartTime(entrust.getStartTime());
        em.setUpdateTime(entrust.getUpdateTime());

        return em;
    }

    public static EntrustHistoryModel entrustHistory2Model(EntrustHistory entrustHistory) {
        EntrustHistoryModel eh = new EntrustHistoryModel();
        eh.setId(entrustHistory.getId());
        eh.setAssigneeId(entrustHistory.getAssigneeId());
        eh.setAssigneeName(entrustHistory.getAssigneeName());
        eh.setCreatTime(entrustHistory.getCreatTime());
        eh.setEndTime(entrustHistory.getEndTime());
        eh.setItemId(entrustHistory.getItemId());
        eh.setItemName(entrustHistory.getItemName());
        eh.setOwnerId(entrustHistory.getOwnerId());
        eh.setOwnerName(entrustHistory.getOwnerName());
        eh.setStartTime(entrustHistory.getStartTime());
        eh.setUpdateTime(entrustHistory.getUpdateTime());

        return eh;
    }

    public static List<EntrustHistoryModel> entrustHistoryList2ModelList(List<EntrustHistory> ehList) {
        List<EntrustHistoryModel> ehModelList = new ArrayList<EntrustHistoryModel>();
        for (EntrustHistory entrustHistory : ehList) {
            ehModelList.add(entrustHistory2Model(entrustHistory));
        }
        return ehModelList;
    }

    public static List<EntrustModel> entrustList2ModelList(List<Entrust> entrustList) {
        List<EntrustModel> entrustModelList = new ArrayList<EntrustModel>();
        for (Entrust entrust : entrustList) {
            entrustModelList.add(entrust2Model(entrust));
        }
        return entrustModelList;
    }

    public static Entrust entrustModel2Entrust(EntrustModel em) {
        Entrust entrust = new Entrust();
        entrust.setId(em.getId());
        entrust.setAssigneeId(em.getAssigneeId());
        entrust.setAssigneeName(em.getAssigneeName());
        entrust.setCreatTime(em.getCreatTime());
        entrust.setUsed(em.getUsed());
        entrust.setEndTime(em.getEndTime());
        entrust.setItemId(em.getItemId());
        entrust.setItemName(em.getItemName());
        entrust.setOwnerId(em.getOwnerId());
        entrust.setOwnerName(em.getOwnerName());
        entrust.setStartTime(em.getStartTime());
        entrust.setUpdateTime(em.getUpdateTime());

        return entrust;
    }

    public static OpinionModel opinion2Model(Opinion opinion) {
        OpinionModel om = new OpinionModel();
        if (opinion != null) {
            om.setId(opinion.getId());
            om.setContent(opinion.getContent());
            om.setCreateDate(opinion.getCreateDate());
            om.setModifyDate(opinion.getModifyDate());
            om.setOpinionFrameMark(opinion.getOpinionFrameMark());
            om.setProcessInstanceId(opinion.getProcessInstanceId());
            om.setProcessSerialNumber(opinion.getProcessSerialNumber());
            om.setTaskId(opinion.getTaskId());
            om.setTenantId(opinion.getTenantId());
            om.setUserId(opinion.getUserId());
            om.setUserName(opinion.getUserName());
            om.setDeptId(opinion.getDeptId());
            om.setDeptName(opinion.getDeptName());
        }
        return om;
    }

    public static List<OpinionModel> opinionList2ModelList(List<Opinion> opinionList) {
        List<OpinionModel> opinionModelList = new ArrayList<OpinionModel>();
        for (Opinion opinion : opinionList) {
            opinionModelList.add(opinion2Model(opinion));
        }
        return opinionModelList;
    }

    public static Opinion opinionModel2Opinion(OpinionModel om) {
        Opinion opinion = new Opinion();
        opinion.setId(om.getId());
        opinion.setContent(om.getContent());
        opinion.setCreateDate(om.getCreateDate());
        opinion.setModifyDate(om.getModifyDate());
        opinion.setOpinionFrameMark(om.getOpinionFrameMark());
        opinion.setProcessInstanceId(om.getProcessInstanceId());
        opinion.setProcessSerialNumber(om.getProcessSerialNumber());
        opinion.setTaskId(om.getTaskId());
        opinion.setTenantId(om.getTenantId());
        opinion.setUserId(om.getUserId());
        opinion.setUserName(om.getUserName());
        opinion.setDeptId(om.getDeptId());
        opinion.setDeptName(om.getDeptName());
        return opinion;
    }

    public static Opinion opinionModel2Opinion4Position(OpinionModel om) {
        Opinion opinion = new Opinion();
        opinion.setId(om.getId());
        opinion.setContent(om.getContent());
        opinion.setCreateDate(om.getCreateDate());
        opinion.setModifyDate(om.getModifyDate());
        opinion.setOpinionFrameMark(om.getOpinionFrameMark());
        opinion.setProcessInstanceId(om.getProcessInstanceId());
        opinion.setProcessSerialNumber(om.getProcessSerialNumber());
        opinion.setTaskId(om.getTaskId());
        opinion.setTenantId(om.getTenantId());
        opinion.setUserId(om.getUserId());
        opinion.setUserName(om.getUserName());
        return opinion;
    }

    public static ProcessTrackModel processTrack2Model(ProcessTrack processTrack) {
        ProcessTrackModel ptModel = new ProcessTrackModel();
        ptModel.setId(processTrack.getId());
        ptModel.setDescribed(processTrack.getDescribed());
        ptModel.setDocVersion(processTrack.getDocVersion());
        ptModel.setEndTime(processTrack.getEndTime());
        ptModel.setHandlingTime(processTrack.getHandlingTime());
        ptModel.setIsChaoSong(processTrack.getIsChaoSong());
        ptModel.setOpinion(processTrack.getOpinion());
        ptModel.setProcessInstanceId(processTrack.getProcessInstanceId());
        ptModel.setReceiverName(processTrack.getReceiverName());
        ptModel.setSenderName(processTrack.getSenderName());
        ptModel.setStartTime(processTrack.getStartTime());
        ptModel.setTaskDefName(processTrack.getTaskDefName());
        ptModel.setTaskId(processTrack.getTaskId());
        return ptModel;
    }

    public static List<ProcessTrackModel> processTrackList2ModelList(List<ProcessTrack> ptList) {
        List<ProcessTrackModel> ptModelList = new ArrayList<ProcessTrackModel>();
        for (ProcessTrack processTrack : ptList) {
            ptModelList.add(processTrack2Model(processTrack));
        }
        return ptModelList;
    }

    public static ProcessTrack processTrackModel2ProcessTrack(ProcessTrackModel ptModel) {
        ProcessTrack pt = new ProcessTrack();
        pt.setId(ptModel.getId());
        pt.setDescribed(ptModel.getDescribed());
        pt.setEndTime(ptModel.getEndTime());
        pt.setProcessInstanceId(ptModel.getProcessInstanceId());
        pt.setReceiverName(ptModel.getReceiverName());
        pt.setSenderName(ptModel.getSenderName());
        pt.setStartTime(ptModel.getStartTime());
        pt.setTaskDefName(ptModel.getTaskDefName());
        pt.setTaskId(ptModel.getTaskId());
        return pt;
    }

    public static SpeakInfoModel speakInfo2Model(SpeakInfo speakInfo) {
        SpeakInfoModel siModel = new SpeakInfoModel();
        siModel.setId(speakInfo.getId());
        siModel.setContent(speakInfo.getContent());
        siModel.setProcessInstanceId(speakInfo.getProcessInstanceId());
        siModel.setEdited(speakInfo.isEdited());
        siModel.setUserId(speakInfo.getUserId());
        siModel.setUserName(speakInfo.getUserName());
        siModel.setCreateTime(speakInfo.getCreateTime());
        siModel.setUpdateTime(speakInfo.getUpdateTime());

        return siModel;
    }

    public static List<SpeakInfoModel> speakInfoList2ModelList(List<SpeakInfo> siList) {
        List<SpeakInfoModel> siModelList = new ArrayList<SpeakInfoModel>();
        for (SpeakInfo speakInfo : siList) {
            siModelList.add(speakInfo2Model(speakInfo));
        }
        return siModelList;
    }

    public static SpeakInfo speakInfoModel2SpeakInfo(SpeakInfoModel siModel) {
        SpeakInfo si = new SpeakInfo();
        si.setId(siModel.getId());
        si.setContent(siModel.getContent());
        si.setProcessInstanceId(siModel.getProcessInstanceId());
        si.setUserId(siModel.getUserId());
        si.setUserName(siModel.getUserName());
        si.setCreateTime(siModel.getCreateTime());
        si.setUpdateTime(siModel.getUpdateTime());
        return si;
    }
}
