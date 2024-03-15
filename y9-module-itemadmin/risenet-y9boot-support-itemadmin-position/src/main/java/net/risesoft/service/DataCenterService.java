package net.risesoft.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.datacenter.OfficeInfoApi;
import net.risesoft.api.platform.org.DepartmentApi;
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
@Service
@Slf4j
public class DataCenterService {

    @Autowired
    private SpmApproveItemService spmApproveitemService;

    @Autowired
    private TransactionWordService transactionWordService;

    @Autowired
    private TransactionFileService transactionFileService;

    @Autowired
    private ProcessTrackService processTrackService;

    @Autowired
    private Y9FormItemBindService y9FormItemBindService;

    @Autowired
    private AssociatedFileRepository associatedFileRepository;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    @Qualifier("jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate4Tenant;

    @Autowired
    private Y9FormRepository y9FormRepository;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private DepartmentApi departmentApi;

    @Autowired
    private OrgUnitApi orgUnitApi;

    @Autowired
    private Y9FormFieldRepository y9FormFieldRepository;

    @Autowired
    private OfficeInfoApi officeInfoManager;

    @Autowired
    private HistoricProcessApi historicProcessManager;

    @Autowired
    private HistoricVariableApi historicVariableManager;

    /**
     * 获取表单数据
     *
     * @param processInstanceId
     * @param processDefinitionKey
     * @param processDefinitionId
     */
    public List<EformInfo> getEformInfo(String processInstanceId, String processDefinitionKey,
        String processDefinitionId) {
        Connection connection = null;
        List<EformInfo> elist = new ArrayList<EformInfo>();
        try {
            LOGGER.info(
                "************************************itemAdmin保存表单数据到数据中心***********************************************");
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            String itemId = processParam.getItemId();
            String processSerialNumber = processParam.getProcessSerialNumber();
            List<Y9FormItemBind> formBindData =
                y9FormItemBindService.findByItemIdAndProcDefId(itemId, processDefinitionId);
            for (Y9FormItemBind y9Form : formBindData) {
                EformInfo eformInfo = new EformInfo();
                String fieldNames = "";
                String fieldValues = "";
                connection = jdbcTemplate4Tenant.getDataSource().getConnection();
                DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
                String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
                List<String> list = y9FormRepository.findBindTableName(y9Form.getFormId());
                for (String tableName : list) {
                    StringBuffer sqlStr = new StringBuffer();
                    if ("oracle".equals(dialect)) {
                        sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("dm".equals(dialect)) {
                        sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("kingbase".equals(dialect)) {
                        sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("mysql".equals(dialect)) {
                        sqlStr = new StringBuffer("SELECT * FROM " + tableName + " where guid =?");
                    }
                    List<Map<String, Object>> datamap =
                        jdbcTemplate4Tenant.queryForList(sqlStr.toString(), processSerialNumber);
                    if (datamap.size() > 0) {
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
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.warn("数据库连接关闭异常", e);
                }
            }
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
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        String undertaker = "";
        String userIds = "";
        retMap.put("undertaker", undertaker);
        retMap.put("userId", userIds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<Map<String, Object>> listMap = processTrackService.getListMap(processInstanceId);
            List<HistoryInfo> list = new ArrayList<HistoryInfo>();
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Object> map = listMap.get(i);
                String assignee = map.get("assignee") != null ? (String)map.get("assignee") : "";
                if (!undertaker.contains(assignee)) {
                    undertaker = Y9Util.genCustomStr(undertaker, assignee);
                }
                String userId = map.get("undertakerId") != null ? (String)map.get("undertakerId") : "";
                if (StringUtils.isNotBlank(userId)) {
                    if (!userIds.contains(userId)) {
                        userIds = Y9Util.genCustomStr(userIds, userId);
                    }
                }
                String opinion = map.get("opinion") != null ? (String)map.get("opinion") : "";
                String startTime = map.get("startTime") != null ? (String)map.get("startTime") : "";
                String endTime = map.get("endTime") != null ? (String)map.get("endTime") : "";
                String actionName = map.get("name") != null ? (String)map.get("name") : "";
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
        try {
            HistoricProcessInstanceModel processInstance =
                historicProcessManager.getById(Y9LoginUserHolder.getTenantId(), processInstanceId);
            HistoricVariableInstanceModel vmap = historicVariableManager.getByProcessInstanceIdAndVariableName(tenantId,
                processInstanceId, "infoOvert", "");

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
            List<TransactionFile> fileList = transactionFileService.getListByProcessSerialNumber(processSerialNumber);
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
            officeInfo.setDisabled(infoOvert.equals("0") ? "0" : "1");

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            List<TransactionFile> fileList = transactionFileService.getListByProcessSerialNumber(processSerialNumber);
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
            officeInfo.setDisabled(infoOvert.equals("0") ? "0" : "1");

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
