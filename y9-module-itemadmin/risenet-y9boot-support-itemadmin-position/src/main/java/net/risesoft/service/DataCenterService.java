package net.risesoft.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.datacenter.OfficeInfoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.entity.AssociatedFile;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.TransactionFile;
import net.risesoft.entity.TransactionWord;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.datacenter.AttachmentInfo;
import net.risesoft.model.datacenter.EformInfo;
import net.risesoft.model.datacenter.HistoryInfo;
import net.risesoft.model.datacenter.OfficeInfo;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.jpa.AssociatedFileRepository;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
public class DataCenterService {

    private final JdbcTemplate jdbcTemplate4Tenant;

    private final SpmApproveItemService spmApproveitemService;

    private final TransactionWordService transactionWordService;

    private final TransactionFileService transactionFileService;

    private final ProcessTrackService processTrackService;

    private final Y9FormItemBindService y9FormItemBindService;

    private final AssociatedFileRepository associatedFileRepository;

    private final ProcessParamService processParamService;

    private final Y9FormRepository y9FormRepository;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final Y9FormFieldRepository y9FormFieldRepository;

    private final OfficeInfoApi officeInfoManager;

    private final HistoricProcessApi historicProcessManager;

    private final HistoricVariableApi historicVariableManager;

    public DataCenterService(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        SpmApproveItemService spmApproveitemService, TransactionWordService transactionWordService,
        TransactionFileService transactionFileService, ProcessTrackService processTrackService,
        Y9FormItemBindService y9FormItemBindService, AssociatedFileRepository associatedFileRepository,
        ProcessParamService processParamService, Y9FormRepository y9FormRepository, PositionApi positionApi,
        OrgUnitApi orgUnitApi, Y9FormFieldRepository y9FormFieldRepository, OfficeInfoApi officeInfoManager,
        HistoricProcessApi historicProcessManager, HistoricVariableApi historicVariableManager) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.spmApproveitemService = spmApproveitemService;
        this.transactionWordService = transactionWordService;
        this.transactionFileService = transactionFileService;
        this.processTrackService = processTrackService;
        this.y9FormItemBindService = y9FormItemBindService;
        this.associatedFileRepository = associatedFileRepository;
        this.processParamService = processParamService;
        this.y9FormRepository = y9FormRepository;
        this.positionApi = positionApi;
        this.orgUnitApi = orgUnitApi;
        this.y9FormFieldRepository = y9FormFieldRepository;
        this.officeInfoManager = officeInfoManager;
        this.historicProcessManager = historicProcessManager;
        this.historicVariableManager = historicVariableManager;
    }

    /**
     * 获取表单数据
     *
     * @param processInstanceId
     * @param processDefinitionKey
     * @param processDefinitionId
     */
    public List<EformInfo> getEformInfo(String processInstanceId, String processDefinitionKey,
        String processDefinitionId) {
        List<EformInfo> elist = new ArrayList<>();
        try {
            LOGGER.info(
                "************************************itemAdmin保存表单数据到数据中心***********************************************");
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            String itemId = processParam.getItemId();
            String processSerialNumber = processParam.getProcessSerialNumber();
            List<Y9FormItemBind> formBindData =
                y9FormItemBindService.listByItemIdAndProcDefId(itemId, processDefinitionId);
            for (Y9FormItemBind y9Form : formBindData) {
                EformInfo eformInfo = new EformInfo();
                String fieldNames = "";
                String fieldValues = "";
                DataSource dataSource = Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource());
                DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
                String dialect = dbMetaDataUtil.getDatabaseDialectName(dataSource);
                List<String> list = y9FormRepository.findBindTableName(y9Form.getFormId());
                for (String tableName : list) {
                    StringBuilder sqlStr = new StringBuilder();
                    if ("oracle".equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("dm".equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("kingbase".equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("mysql".equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM " + tableName + " where guid =?");
                    }
                    List<Map<String, Object>> datamap =
                        jdbcTemplate4Tenant.queryForList(sqlStr.toString(), processSerialNumber);
                    if (datamap != null && !datamap.isEmpty()) {
                        List<Y9FormField> elementList =
                            y9FormFieldRepository.findByFormIdAndTableName(y9Form.getFormId(), tableName);
                        for (Y9FormField element : elementList) {
                            String fieldName = element.getFieldName();
                            String fieldCnName = element.getFieldCnName();
                            if (!element.getFieldName().equals("processInstanceId")
                                && !element.getFieldName().equals("guid")) {
                                fieldNames = Y9Util.genCustomStr(fieldNames, fieldCnName, "&");
                                fieldValues = Y9Util.genCustomStr(fieldValues, datamap.get(0).get(fieldName) != null
                                    ? datamap.get(0).get(fieldName).toString() : "", "&");
                            }
                        }
                    }
                }
                eformInfo.setEformName(y9Form.getFormName());
                eformInfo.setFieldNames(fieldNames);
                eformInfo.setFieldValues(fieldValues);
                elist.add(eformInfo);
            }
        } catch (Exception e) {
            LOGGER.warn("保存表单数据到数据中心发生异常", e);
        }
        return elist;
    }

    /**
     * 处理历程信息,将历程存为excel
     *
     * @param processInstanceId
     * @return
     */
    public Map<String, Object> historyExcel(String processSerialNumber, String processInstanceId) {
        Map<String, Object> retMap = new HashMap<>(16);
        String undertaker = "";
        String userIds = "";
        retMap.put("undertaker", undertaker);
        retMap.put("userId", userIds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<HistoryProcessModel> listMap = processTrackService.listByProcessInstanceId(processInstanceId);
            List<HistoryInfo> list = new ArrayList<>();
            for (int i = 0; i < listMap.size(); i++) {
                HistoryProcessModel map = listMap.get(i);
                String assignee = map.getAssignee();
                if (!undertaker.contains(assignee)) {
                    undertaker = Y9Util.genCustomStr(undertaker, assignee);
                }
                String userId = map.getUndertakerId();
                if (StringUtils.isNotBlank(userId)) {
                    if (!userIds.contains(userId)) {
                        userIds = Y9Util.genCustomStr(userIds, userId);
                    }
                }
                String opinion = map.getOpinion();
                String startTime = map.getStartTime();
                String endTime = map.getEndTime();
                String actionName = map.getName();
                HistoryInfo historyInfo = new HistoryInfo();
                historyInfo.setAssignee(assignee);
                historyInfo.setActionName(actionName);
                historyInfo.setEndTime(StringUtils.isNotBlank(endTime) ? sdf.parse(endTime) : new Date());
                historyInfo.setOpinionContent(opinion);
                historyInfo.setStartTime(StringUtils.isNotBlank(startTime) ? sdf.parse(startTime) : new Date());
                list.add(historyInfo);
            }
            retMap.put("userId", userIds);
            retMap.put("undertaker", undertaker);
            retMap.put("list", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    /**
     * 保存办结数据到数据中心
     *
     * @param processInstanceId
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean saveToDateCenter(String processInstanceId) {
        LOGGER.info(
            "************************************itemAdmin保存办结数据到数据中心***********************************************");
        OfficeInfo officeInfo = new OfficeInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Position position = Y9LoginUserHolder.getPosition();
        HistoricProcessInstanceModel processInstance =
            historicProcessManager.getById(Y9LoginUserHolder.getTenantId(), processInstanceId).getData();
        HistoricVariableInstanceModel vmap = historicVariableManager
            .getByProcessInstanceIdAndVariableName(tenantId, processInstanceId, "infoOvert", "").getData();

        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        String itemId = processParam.getItemId();
        String documentTitle = processParam.getTitle();
        String level = processParam.getCustomLevel();
        String number = processParam.getCustomNumber();
        String processSerialNumber = processParam.getProcessSerialNumber();
        String systemCnName = processParam.getSystemCnName();
        String systemName = processParam.getSystemName();
        String infoOvert = ((vmap == null || vmap.getValue() == null) ? "" : vmap.getValue().toString());

        SpmApproveItem spmApproveItem = spmApproveitemService.findById(itemId);
        String startProUserId = processInstance.getStartUserId();
        String startUserId = startProUserId.contains(":") ? startProUserId.split(":")[0] : startProUserId;
        Position startProUser = positionApi.get(tenantId, startUserId).getData();
        OrgUnit dept = orgUnitApi.getBureau(tenantId, startProUser.getParentId()).getData();

        // 获取历程
        Map<String, Object> map = this.historyExcel(processSerialNumber, processInstanceId);
        String userId = (String)map.get("userId");
        List<HistoryInfo> hisList = (List<HistoryInfo>)map.get("list");

        // 获取正文
        TransactionWord word = transactionWordService.getByProcessSerialNumber(processSerialNumber);
        String textUrl = "";
        if (word != null && StringUtils.isNotBlank(word.getFileStoreId())) {
            textUrl = word.getFileStoreId();
        }

        // 获取附件
        List<TransactionFile> fileList = transactionFileService.listByProcessSerialNumber(processSerialNumber);
        List<AttachmentInfo> aList = new ArrayList<>();
        for (TransactionFile file : fileList) {
            AttachmentInfo info = new AttachmentInfo();
            info.setFileContent(null);
            info.setFileId(file.getFileStoreId());
            info.setFileName(file.getName());
            info.setFileType(file.getFileType());
            info.setFileUrl(null);
            aList.add(info);
        }

        AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
        String associatedId = "";
        if (associatedFile != null) {
            associatedId = associatedFile.getAssociatedId();
        }

        officeInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        officeInfo.setTenantId(tenantId);
        officeInfo.setCompleteUserName(position.getName());
        officeInfo.setCreatDeptName(dept.getName());
        officeInfo.setCreatUserName(startProUser.getName());
        officeInfo.setEndTime(processInstance.getEndTime());
        officeInfo.setStartTime(processInstance.getStartTime());
        officeInfo.setAppCnName(spmApproveItem != null ? spmApproveItem.getName() : "");
        officeInfo.setSystemCnName(systemCnName);
        officeInfo.setSystemName(systemName);
        officeInfo.setDocNature("");
        officeInfo.setFilingDept(dept.getName());
        officeInfo.setFilingMan(position.getName());
        officeInfo.setProcessInstanceId(processInstanceId);
        officeInfo.setProcessSerialNumber(processSerialNumber);
        officeInfo.setSecurityLevel("一般");
        officeInfo.setSerialNumber(number);
        officeInfo.setText("");
        officeInfo.setTextUrl(textUrl);
        officeInfo.setTitle(documentTitle);
        officeInfo.setUndertaker(userId);
        officeInfo.setUrgency(level);
        officeInfo.setAssociatedId(associatedId);
        officeInfo.setDisabled(infoOvert.equals("1") ? "1" : "0");

        officeInfo.setHistorys(hisList);
        officeInfo.setAttachments(aList);

        String processDefinitionId = processInstance.getProcessDefinitionId();
        List<EformInfo> elist =
            this.getEformInfo(processInstanceId, processDefinitionId.split(":")[0], processDefinitionId);
        officeInfo.setEforms(elist);

        boolean b = officeInfoManager.saveOfficeInfo(tenantId, officeInfo);
        LOGGER.info("*****officeInfo数保存到数据中心 {} *****", b);
        LOGGER.info("-----办结数据：{}", officeInfo);
        return b;
    }

    @SuppressWarnings("unchecked")
    public boolean saveToDateCenter1(String processInstanceId, String processDefinitionId) {
        LOGGER.info(
            "************************************itemAdmin保存办结数据到数据中心***********************************************");
        OfficeInfo officeInfo = new OfficeInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String sql = "SELECT v.TEXT_ from act_hi_varinst_2023 v where v.NAME_ = 'infoOvert' and v.PROC_INST_ID_ = '"
                + processInstanceId + "'";
            List<Map<String, Object>> list0 = jdbcTemplate4Tenant.queryForList(sql);
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            String itemId = processParam.getItemId();
            String documentTitle = processParam.getTitle();
            String level = processParam.getCustomLevel();
            String number = processParam.getCustomNumber();
            String processSerialNumber = processParam.getProcessSerialNumber();
            String systemCnName = processParam.getSystemCnName();
            String systemName = processParam.getSystemName();
            String infoOvert = list0.size() == 0 ? "" : (String)(list0.get(0).get("TEXT_"));

            SpmApproveItem spmApproveItem = spmApproveitemService.findById(itemId);
            String startProUserId = processParam.getStartor();
            String startUserId = startProUserId.contains(":") ? startProUserId.split(":")[0] : startProUserId;
            Position startProUser = positionApi.get(tenantId, startUserId).getData();
            OrgUnit dept = orgUnitApi.getBureau(tenantId, startProUser.getParentId()).getData();

            // 获取历程
            Map<String, Object> map = this.historyExcel(processSerialNumber, processInstanceId);
            String userId = (String)map.get("userId");
            List<HistoryInfo> hisList = (List<HistoryInfo>)map.get("list");

            // 获取正文
            TransactionWord word = transactionWordService.getByProcessSerialNumber(processSerialNumber);
            String textUrl = "";
            if (word != null && StringUtils.isNotBlank(word.getFileStoreId())) {
                textUrl = word.getFileStoreId();
            }

            // 获取附件
            List<TransactionFile> fileList = transactionFileService.listByProcessSerialNumber(processSerialNumber);
            List<AttachmentInfo> aList = new ArrayList<AttachmentInfo>();
            for (TransactionFile file : fileList) {
                AttachmentInfo info = new AttachmentInfo();
                info.setFileContent(null);
                info.setFileId(file.getFileStoreId());
                info.setFileName(file.getName());
                info.setFileType(file.getFileType());
                info.setFileUrl(null);
                aList.add(info);
            }

            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            String associatedId = "";
            if (associatedFile != null) {
                associatedId = associatedFile.getAssociatedId();
            }

            sql =
                "SELECT SUBSTRING(P.START_TIME_,1,19) as START_TIME_,SUBSTRING(P.END_TIME_,1,19) as END_TIME_ FROM ACT_HI_PROCINST_2023 P WHERE P .PROC_INST_ID_ ='"
                    + processInstanceId + "'";
            List<Map<String, Object>> list1 = jdbcTemplate4Tenant.queryForList(sql);

            officeInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            officeInfo.setTenantId(tenantId);
            officeInfo.setCompleteUserName(processParam.getCompleter());
            officeInfo.setCreatDeptName(dept.getName());
            officeInfo.setCreatUserName(startProUser.getName());
            officeInfo.setEndTime(sdf.parse(list1.get(0).get("END_TIME_").toString()));
            officeInfo.setStartTime(sdf.parse(list1.get(0).get("START_TIME_").toString()));
            officeInfo.setAppCnName(spmApproveItem != null ? spmApproveItem.getName() : "");
            officeInfo.setSystemCnName(systemCnName);
            officeInfo.setSystemName(systemName);
            officeInfo.setDocNature("");
            officeInfo.setFilingDept(dept.getName());
            officeInfo.setFilingMan(processParam.getCompleter());
            officeInfo.setProcessInstanceId(processInstanceId);
            officeInfo.setProcessSerialNumber(processSerialNumber);
            officeInfo.setSecurityLevel("一般");
            officeInfo.setSerialNumber(number);
            officeInfo.setText("");
            officeInfo.setTextUrl(textUrl);
            officeInfo.setTitle(documentTitle);
            officeInfo.setUndertaker(userId);
            officeInfo.setUrgency(level);
            officeInfo.setAssociatedId(associatedId);
            officeInfo.setDisabled(infoOvert.equals("1") ? "1" : "0");

            officeInfo.setHistorys(hisList);
            officeInfo.setAttachments(aList);

            List<EformInfo> elist =
                this.getEformInfo(processInstanceId, processDefinitionId.split(":")[0], processDefinitionId);
            officeInfo.setEforms(elist);

            boolean b = officeInfoManager.saveOfficeInfo(tenantId, officeInfo);
            LOGGER.info("*****officeInfo数保存到数据中心 {} *****", b);
            LOGGER.info("-----办结数据：{}", officeInfo);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
