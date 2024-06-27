package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.AssociatedFile;
import net.risesoft.entity.ProcessParam;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.AssociatedFileRepository;
import net.risesoft.service.AssociatedFileService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

import y9.client.rest.processadmin.HistoricProcessApiClient;
import y9.client.rest.processadmin.HistoricTaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "associatedFileService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AssociatedFileServiceImpl implements AssociatedFileService {

    @Autowired
    private AssociatedFileRepository associatedFileRepository;

    @Autowired
    HistoricProcessApiClient historicProcessManager;

    @Autowired
    HistoricTaskApiClient historicTaskManager;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @Override
    public int countAssociatedFile(String processSerialNumber) {
        try {
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null && StringUtils.isNotBlank(associatedFile.getAssociatedId())) {
                String associatedId = associatedFile.getAssociatedId();
                String[] associatedIds = associatedId.split(SysVariables.COMMA);
                return associatedIds.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Transactional(readOnly = false)
    @Override
    public boolean deleteAllAssociatedFile(String processSerialNumber, String delIds) {
        try {
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null && associatedFile.getId() != null) {
                String associatedId = associatedFile.getAssociatedId();
                String newAssociatedId = "";
                String[] associatedIds = associatedId.split(SysVariables.COMMA);
                String[] delAssociatedIds = delIds.split(SysVariables.COMMA);
                for (String id : associatedIds) {
                    Boolean isDel = false;
                    for (String delId : delAssociatedIds) {
                        if (id.equals(delId)) {
                            isDel = true;
                        }
                    }
                    if (!isDel) {
                        newAssociatedId = Y9Util.genCustomStr(newAssociatedId, id);
                    }
                }
                associatedFile.setUserId(Y9LoginUserHolder.getPersonId());
                associatedFile.setUserName(Y9LoginUserHolder.getUserInfo().getName());
                associatedFile.setCreateTime(new Date());
                associatedFile.setAssociatedId(newAssociatedId);
                associatedFileRepository.save(associatedFile);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Transactional(readOnly = false)
    @Override
    public boolean deleteAssociatedFile(String processSerialNumber, String delId) {
        try {
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null && associatedFile.getId() != null) {
                String associatedId = associatedFile.getAssociatedId();
                String newAssociatedId = "";
                String[] associatedIds = associatedId.split(SysVariables.COMMA);
                for (String id : associatedIds) {
                    if (!id.equals(delId)) {
                        newAssociatedId = Y9Util.genCustomStr(newAssociatedId, id);
                    }
                }
                associatedFile.setUserId(Y9LoginUserHolder.getPersonId());
                associatedFile.setUserName(Y9LoginUserHolder.getUserInfo().getName());
                associatedFile.setCreateTime(new Date());
                associatedFile.setAssociatedId(newAssociatedId);
                associatedFileRepository.save(associatedFile);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, Object> getAssociatedFileAllList(String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "获取成功");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H	H:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null) {
                String associatedId = associatedFile.getAssociatedId();
                if (StringUtils.isNotBlank(associatedId)) {
                    String[] associatedIds = associatedId.split(SysVariables.COMMA);
                    for (String id : associatedIds) {
                        Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                        try {
                            HistoricProcessInstanceModel hpim = historicProcessManager.getById(tenantId, id);
                            ProcessParam processParam = processParamService.findByProcessInstanceId(id);
                            String startTime = "";
                            String startTime1 = "";
                            String endTime = "";
                            if (hpim != null) {
                                startTime = sdf.format(hpim.getStartTime());
                                startTime1 = sdf1.format(hpim.getStartTime());
                            } else {
                                OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(id);
                                if (officeDoneInfoModel != null) {
                                    startTime = sdf.format(sdf1.parse(officeDoneInfoModel.getStartTime()));
                                    startTime1 = officeDoneInfoModel.getStartTime();
                                } else {
                                    String year = processParam.getCreateTime().substring(0, 4);
                                    HistoricProcessInstanceModel hpi =
                                        historicProcessManager.getByIdAndYear(tenantId, id, year);
                                    startTime = sdf.format(hpi.getStartTime());
                                    startTime1 = sdf1.format(hpi.getStartTime());
                                }
                            }
                            String processSerialNumber1 = processParam.getProcessSerialNumber();
                            String itemId = processParam.getItemId();
                            String itemName = processParam.getItemName();
                            String documentTitle = processParam.getTitle();
                            String user4Complete =
                                StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter();

                            mapTemp.put("itemName", itemName);
                            mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber1);
                            mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                            mapTemp.put("processInstanceId", id);
                            mapTemp.put("startTime", startTime);
                            mapTemp.put("endTime", endTime);
                            // 排序用
                            mapTemp.put("startTimes", startTime1);
                            mapTemp.put("user4Complete", user4Complete);
                            mapTemp.put("itemId", itemId);
                            mapTemp.put("itemName", itemName);
                            mapTemp.put("level", processParam.getCustomLevel());
                            mapTemp.put("number", processParam.getCustomNumber());
                            items.add(mapTemp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Collections.sort(items, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date startTime1 = sdf.parse((String)o1.get("startTimes"));
                            Date startTime2 = sdf.parse((String)o2.get("startTimes"));
                            if (startTime1.getTime() < startTime2.getTime()) {
                                return 1;
                            } else if (startTime1.getTime() == startTime2.getTime()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return -1;
                    }
                });
            }
            map.put("rows", items);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "关联流程列表获取成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> getAssociatedFileList(String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "获取成功");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile != null) {
                String associatedId = associatedFile.getAssociatedId();
                if (StringUtils.isNotBlank(associatedId)) {
                    String[] associatedIds = associatedId.split(SysVariables.COMMA);
                    for (String id : associatedIds) {
                        Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                        try {
                            HistoricProcessInstanceModel hpim = historicProcessManager.getById(tenantId, id);
                            ProcessParam processParam = processParamService.findByProcessInstanceId(id);
                            String startTime = "";
                            String endTime1 = "";
                            String endTime = "";
                            if (hpim != null) {
                                startTime = sdf.format(hpim.getStartTime());
                                endTime1 = sdf1.format(hpim.getEndTime());
                                endTime = sdf.format(hpim.getEndTime());
                            } else {
                                OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(id);
                                if (officeDoneInfoModel != null) {
                                    startTime = sdf.format(sdf1.parse(officeDoneInfoModel.getStartTime()));
                                    endTime1 = officeDoneInfoModel.getEndTime();
                                    endTime = sdf.format(sdf1.parse(officeDoneInfoModel.getEndTime()));
                                } else {
                                    String year = processParam.getCreateTime().substring(0, 4);
                                    HistoricProcessInstanceModel hpi =
                                        historicProcessManager.getByIdAndYear(tenantId, id, year);
                                    startTime = sdf.format(hpi.getStartTime());
                                    endTime1 = sdf1.format(hpi.getEndTime());
                                    endTime = sdf.format(hpi.getEndTime());
                                }
                            }
                            String processSerialNumber1 = processParam.getProcessSerialNumber();
                            String itemId = processParam.getItemId();
                            String itemName = processParam.getItemName();
                            String documentTitle = processParam.getTitle();
                            String user4Complete =
                                StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter();
                            mapTemp.put("itemName", itemName);
                            mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber1);
                            mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                            mapTemp.put("processInstanceId", id);
                            mapTemp.put("startTime", startTime);
                            mapTemp.put("endTime", endTime);
                            mapTemp.put("endTimes", endTime1);
                            mapTemp.put("user4Complete", user4Complete);
                            mapTemp.put("itemId", itemId);
                            mapTemp.put("level", processParam.getCustomLevel());
                            mapTemp.put("number", processParam.getCustomNumber());
                            items.add(mapTemp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Collections.sort(items, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date endTimes1 = sdf.parse((String)o1.get("endTimes"));
                            Date endTimes2 = sdf.parse((String)o2.get("endTimes"));
                            if (endTimes1.getTime() < endTimes2.getTime()) {
                                return 1;
                            } else if (endTimes1.getTime() == endTimes2.getTime()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
            }
            map.put("rows", items);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取失败");
            e.printStackTrace();
        }
        return map;
    }

    @Transactional(readOnly = false)
    @Override
    public boolean saveAssociatedFile(String processSerialNumber, String processInstanceIds) {
        try {
            AssociatedFile associatedFile = associatedFileRepository.findByProcessSerialNumber(processSerialNumber);
            if (associatedFile == null || associatedFile.getId() == null) {
                associatedFile = new AssociatedFile();
                associatedFile.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                associatedFile.setCreateTime(new Date());
                associatedFile.setAssociatedId(processInstanceIds);
                associatedFile.setProcessSerialNumber(processSerialNumber);
                associatedFile.setUserId(Y9LoginUserHolder.getPersonId());
                associatedFile.setUserName(Y9LoginUserHolder.getUserInfo().getName());
                associatedFile.setTenantId(Y9LoginUserHolder.getTenantId());
            } else {
                String associatedId = associatedFile.getAssociatedId();
                String newAssociatedId = "";
                if (StringUtils.isNotBlank(associatedId)) {
                    String[] associatedIds = processInstanceIds.split(SysVariables.COMMA);
                    for (String id : associatedIds) {
                        if (!associatedId.contains(id)) {
                            newAssociatedId = Y9Util.genCustomStr(newAssociatedId, id);
                        }
                    }
                } else {
                    newAssociatedId = processInstanceIds;
                }
                newAssociatedId = Y9Util.genCustomStr(associatedId, newAssociatedId);
                associatedFile.setUserId(Y9LoginUserHolder.getPersonId());
                associatedFile.setUserName(Y9LoginUserHolder.getUserInfo().getName());
                associatedFile.setCreateTime(new Date());
                associatedFile.setAssociatedId(newAssociatedId);
            }
            associatedFileRepository.save(associatedFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
