package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.PersonRoleApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.PunctuationConsts;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemOrganWordBind;
import net.risesoft.entity.OrganWord;
import net.risesoft.entity.OrganWordDetail;
import net.risesoft.entity.OrganWordProperty;
import net.risesoft.entity.OrganWordUseHistory;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OrganWordModel;
import net.risesoft.model.itemadmin.OrganWordPropertyModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.OrganWordRepository;
import net.risesoft.service.ItemOrganWordBindService;
import net.risesoft.service.OrganWordDetailService;
import net.risesoft.service.OrganWordPropertyService;
import net.risesoft.service.OrganWordService;
import net.risesoft.service.OrganWordUseHistoryService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OrganWordServiceImpl implements OrganWordService {

    private final OrganWordRepository organWordRepository;

    private final ProcessParamService processParamService;

    private final OrganWordPropertyService organWordPropertyService;

    private final OrganWordDetailService organWordDetailService;

    private final ItemOrganWordBindService itemOrganWordBindService;

    private final OrganWordUseHistoryService organWordUseHistoryService;

    private final OrgUnitApi orgUnitApi;

    private final PersonRoleApi personRoleApi;

    private final PositionRoleApi positionRoleApi;

    private final TaskApi taskManager;

    @Override
    public boolean checkCustom(String id, String custom) {
        if (StringUtils.isNotEmpty(id)) {
            OrganWord optional = organWordRepository.findById(id).orElse(null);
            OrganWord organWord = organWordRepository.findByCustom(custom);
            return organWord == null || optional.getId().equals(organWord.getId());
        }
        OrganWord organWord = organWordRepository.findByCustom(custom);
        return null == organWord;
    }

    @Override
    @Transactional
    public Integer checkNumberStr(String characterValue, String custom, Integer year, Integer numberTemp, String itemId,
        Integer common, String processSerialNumber) {
        int status = 3;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            OrganWord organWord = this.findByCustom(custom);
            if (null != organWord) {
                if (1 == common) {
                    itemId = "common";
                }
                OrganWordDetail owd = organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom,
                    characterValue, year, itemId);
                /**
                 * 1.使用详情存在
                 */
                if (null != owd) {
                    status =
                        checkOrganWordUseHistory(characterValue, custom, year, numberTemp, itemId, processSerialNumber);
                    /**
                     * 传入的数值等于使用详情的当前值+1时，保存使用详情，为的是下次正常编号的时候，会从编号的当前值+1开始
                     */
                    Integer number = owd.getNumber() + 1;
                    if (numberTemp.equals(number) && status == 1) {
                        owd.setNumber(numberTemp);
                        owd.setCreateTime(sdf.format(new Date()));
                        organWordDetailService.save(owd);
                    }
                } else {
                    OrganWordProperty property =
                        organWordPropertyService.findByOrganWordIdAndName(organWord.getId(), characterValue);
                    Integer num = property.getInitNumber();
                    OrganWordUseHistory owuh = new OrganWordUseHistory();
                    owuh.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    owuh.setItemId(itemId);
                    owuh.setCreateTime(sdf.format(new Date()));
                    owuh.setCustom(custom);
                    owuh.setProcessSerialNumber(processSerialNumber);
                    owuh.setTenantId(Y9LoginUserHolder.getTenantId());
                    owuh.setUserId(person.getParentId());
                    owuh.setUserName(person.getName());
                    owuh.setNumberString(characterValue + "〔" + year + "〕" + (num));
                    organWordUseHistoryService.save(owuh);

                    OrganWordDetail newowd = new OrganWordDetail();
                    newowd.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newowd.setItemId(itemId);
                    newowd.setTenantId(Y9LoginUserHolder.getTenantId());
                    newowd.setNumber(num);
                    newowd.setCreateTime(sdf.format(new Date()));
                    newowd.setItemId(itemId);
                    newowd.setYear(year);
                    newowd.setCharacterValue(characterValue);
                    newowd.setCustom(custom);
                    organWordDetailService.save(newowd);
                    return 1;
                }
            } else {
                status = 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public Integer checkNumberStr4DeptName(String custom, Integer year, Integer numberTemp, String itemId,
        Integer common, String processSerialNumber) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            OrgUnit bureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), person.getParentId()).getData();
            String deptName = bureau.getName();
            if (1 == common) {
                itemId = "common";
            }
            OrganWordDetail owd =
                organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom, deptName, year, itemId);
            if (null != owd) {
                Integer number = owd.getNumber();
                if (numberTemp <= number) {
                    return 0;
                } else {
                    OrganWordUseHistory owuh = new OrganWordUseHistory();
                    owuh.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    owuh.setItemId(itemId);
                    owuh.setCreateTime(sdf.format(new Date()));
                    owuh.setCustom(custom);
                    owuh.setProcessSerialNumber(processSerialNumber);
                    owuh.setTenantId(Y9LoginUserHolder.getTenantId());
                    owuh.setUserId(person.getParentId());
                    owuh.setUserName(person.getName());
                    owuh.setNumberString(deptName + "〔" + year + "〕" + (numberTemp));
                    organWordUseHistoryService.save(owuh);

                    owd.setNumber(numberTemp);
                    owd.setCreateTime(sdf.format(new Date()));
                    organWordDetailService.save(owd);
                    return 1;
                }
            } else {
                Integer num = numberTemp;

                OrganWordUseHistory owuh = new OrganWordUseHistory();
                owuh.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                owuh.setItemId(itemId);
                owuh.setCreateTime(sdf.format(new Date()));
                owuh.setCustom(custom);
                owuh.setProcessSerialNumber(processSerialNumber);
                owuh.setTenantId(Y9LoginUserHolder.getTenantId());
                owuh.setUserId(person.getParentId());
                owuh.setUserName(person.getName());
                owuh.setNumberString(deptName + "〔" + year + "〕" + (numberTemp));
                organWordUseHistoryService.save(owuh);

                OrganWordDetail newowd = new OrganWordDetail();
                newowd.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newowd.setItemId(itemId);
                newowd.setTenantId(Y9LoginUserHolder.getTenantId());
                newowd.setNumber(num);
                newowd.setCreateTime(sdf.format(new Date()));
                newowd.setItemId(itemId);
                newowd.setYear(year);
                newowd.setCharacterValue(deptName);
                newowd.setCustom(custom);
                organWordDetailService.save(newowd);
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 3;
        }
    }

    private Integer checkOrganWordUseHistory(String characterValue, String custom, Integer year, Integer numberTemp,
        String itemId, String processSerialNumber) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        /**
         * 1、检查当前的编号有没有被使用
         */
        OrganWordUseHistory owuh = organWordUseHistoryService.findByItemIdAndNumberString(itemId,
            characterValue + "〔" + year + "〕" + numberTemp);
        /**
         * 2当前号没有被使用，则检查当前文之前有没有编过号
         */
        if (null == owuh) {
            OrganWordUseHistory oldowuh =
                organWordUseHistoryService.findByProcessSerialNumberAndCustom(processSerialNumber, custom);
            /**
             * 3如果当前processSerialNumber对应的文已经编号（先编号，再修改编号保存）
             */
            if (null != oldowuh) {
                OrganWordDetail organWordDetail = organWordDetailService
                    .findByCustomAndCharacterValueAndYearAndItemId(custom, characterValue, year, itemId);
                /**
                 * 4如果之前的编号的数字等于使用详情的最大使用数字，则把使用详情的数字减一（因为会使用最新的编号，需要把之前占的号释放）
                 */
                if (null != organWordDetail
                    && oldowuh.getNumberString().equals(characterValue + PunctuationConsts.LEFTHEXBRACKETS + year
                        + PunctuationConsts.RIGHTHEXBRACKETS + organWordDetail.getNumber())) {
                    organWordDetail.setNumber(organWordDetail.getNumber() - 1);
                    organWordDetailService.save(organWordDetail);
                }
                oldowuh.setUserId(person.getPersonId());
                oldowuh.setUserName(person.getName());
                oldowuh.setNumberString(characterValue + "〔" + year + "〕" + numberTemp);
                organWordUseHistoryService.save(oldowuh);
            } else {
                /**
                 * 5没有被使用过
                 */
                owuh = new OrganWordUseHistory();
                owuh.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                owuh.setItemId(itemId);
                owuh.setCreateTime(sdf.format(new Date()));
                owuh.setCustom(custom);
                owuh.setProcessSerialNumber(processSerialNumber);
                owuh.setTenantId(Y9LoginUserHolder.getTenantId());
                owuh.setUserId(person.getPersonId());
                owuh.setUserName(person.getName());
                owuh.setNumberString(characterValue + "〔" + year + "〕" + numberTemp);
                organWordUseHistoryService.save(owuh);
            }
            return 1;
        } else {
            /**
             * 6当前processSerialNumber正在使用
             */
            if (processSerialNumber.equals(owuh.getProcessSerialNumber())) {
                return 1;
            } else {
                /**
                 * 7已被其他processSerialNumber使用
                 */
                return 0;
            }
        }
    }

    @Override
    public OrganWordModel exist(String custom, String processSerialNumber, String processInstanceId, String itembox) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        OrganWordUseHistory owuh =
            organWordUseHistoryService.findByProcessSerialNumberAndCustom(processSerialNumber, custom);
        OrganWordModel word = new OrganWordModel();
        word.setExist(false);

        if (null != owuh) {
            word.setExist(true);
            word.setNumberString(owuh.getNumberString());
            return word;
        }
        if (!itembox.equals(ItemBoxTypeEnum.TODO.getValue())) {
            return word;
        }
        word.setHasPermission(false);
        String taskDefKey = "";
        String itemId = "";
        String processDefinitionId = "";
        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
        taskDefKey = taskList.get(0).getTaskDefinitionKey();
        processDefinitionId = taskList.get(0).getProcessDefinitionId();
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        itemId = processParam.getItemId();
        OrganWord organWord = this.findByCustom(custom);
        List<OrganWordPropertyModel> listMap = new ArrayList<>();
        if (organWord != null) {
            boolean hasPermission = false;
            ItemOrganWordBind bind =
                itemOrganWordBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(itemId,
                    processDefinitionId, taskDefKey, custom);
            if (null != bind) {
                List<String> roleIds = bind.getRoleIds();
                for (String roleId : roleIds) {
                    hasPermission = personRoleApi.hasRole(tenantId, roleId, person.getPersonId()).getData();
                }
            }
            if (hasPermission) {
                word.setHasPermission(true);
                List<OrganWordProperty> propertyList = organWordPropertyService.findByOrganWordId(organWord.getId());
                for (OrganWordProperty op : propertyList) {
                    OrganWordPropertyModel proper = new OrganWordPropertyModel();
                    proper.setHasPermission(true);
                    proper.setName(op.getName());
                    proper.setInitNumber(op.getInitNumber());
                    listMap.add(proper);
                }
                word.setList(listMap);
            }
        }
        return word;
    }

    @Override
    public List<OrganWord> findAll() {
        return organWordRepository.findAllByOrderByCreateTimeAsc();
    }

    @Override
    public OrganWord findByCustom(String custom) {
        return organWordRepository.findByCustom(custom);
    }

    @Override
    public List<OrganWordPropertyModel> findByCustom(String itemId, String processDefinitionId, String taskDefKey,
        String custom) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        OrganWord organWord = this.findByCustom(custom);
        if (organWord != null) {
            boolean hasPermission = false;
            ItemOrganWordBind bind =
                itemOrganWordBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(itemId,
                    processDefinitionId, taskDefKey, custom);
            if (null != bind) {
                List<String> roleIds = bind.getRoleIds();
                if (roleIds.isEmpty()) {
                    hasPermission = true;
                } else {
                    for (String roleId : roleIds) {
                        hasPermission =
                            positionRoleApi.hasRole(tenantId, roleId, Y9LoginUserHolder.getPositionId()).getData();
                        if (hasPermission) {
                            break;
                        }
                    }
                }
            }
            if (!hasPermission) {
                return Collections.emptyList();
            } else {
                List<OrganWordProperty> propertyList = organWordPropertyService.findByOrganWordId(organWord.getId());
                List<OrganWordPropertyModel> retList = new ArrayList<>();
                for (OrganWordProperty op : propertyList) {
                    OrganWordPropertyModel editMap = new OrganWordPropertyModel();
                    editMap.setHasPermission(true);
                    editMap.setName(op.getName());
                    editMap.setInitNumber(op.getInitNumber());
                    retList.add(editMap);
                }
                return retList;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public OrganWord findOne(String id) {
        return organWordRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Integer getNumber(String custom, String characterValue, Integer year, Integer common, String itemId) {
        Integer number = 0;
        OrganWordDetail owd = null;
        try {
            OrganWord organWord = this.findByCustom(custom);
            if (null != organWord) {
                if (1 == common) {
                    itemId = "common";
                }
                owd = organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom, characterValue, year,
                    itemId);
                if (null != owd) {
                    number = owd.getNumber() + 1;
                } else {
                    OrganWordProperty property =
                        organWordPropertyService.findByOrganWordIdAndName(organWord.getId(), characterValue);
                    if (null != property) {
                        number = property.getInitNumber();
                    } else {
                        /**
                         * 标示不存在，则编号对应的机关代字也是不存在的，再保存编号对应的机关代字
                         */
                        property = new OrganWordProperty();
                        property.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        property.setInitNumber(1);
                        property.setName(characterValue);
                        property.setOrganWordId(organWord.getId());
                        property.setTabIndex(1);
                        organWordPropertyService.save(property);
                        number = 1;
                    }
                }
            } else {
                /**
                 * 标示不存在，说明是第一次编号，保存当前的编号
                 */
                UserInfo person = Y9LoginUserHolder.getUserInfo();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String organWordId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                organWord = new OrganWord();
                organWord.setId(organWordId);
                organWord.setCustom(custom);
                organWord.setName(characterValue);
                organWord.setCreateTime(sdf.format(new Date()));
                organWord.setUserName(person.getName());
                this.save(organWord);
                /**
                 * 标示不存在，则编号对应的机关代字也是不存在的，再保存编号对应的机关代字
                 */
                OrganWordProperty property = new OrganWordProperty();
                property.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                property.setInitNumber(1);
                property.setName(characterValue);
                property.setOrganWordId(organWordId);
                property.setTabIndex(1);

                organWordPropertyService.save(property);
                number = 1;
            }
            OrganWordUseHistory owuh = null;
            boolean isChange = false;
            do {
                owuh = organWordUseHistoryService.findByItemIdAndNumberString(itemId,
                    characterValue + "〔" + year + "〕" + number);
                if (null != owuh) {
                    number++;
                    isChange = true;
                }
            } while (null != owuh);
            if (isChange && null != owd) {
                owd.setNumber(number - 1);
                organWordDetailService.save(owd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    @Override
    @Transactional
    public Map<String, Object> getNumber4DeptName(String custom, Integer year, Integer common, String itemId) {
        Map<String, Object> map = new HashMap<>(16);
        String numberStr = "";
        Integer number = 0;
        Integer numberTemp = 0;
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            OrgUnit bureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), person.getParentId()).getData();
            String deptName = bureau.getName();
            if (1 == common) {
                itemId = "common";
            }
            OrganWordDetail owd =
                organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom, deptName, year, itemId);
            if (null != owd) {
                number = owd.getNumber();
                numberTemp = number + 1;
                numberStr = deptName + "〔" + year + "〕" + (numberTemp);
                map.put("numberStr", numberStr);
                map.put("numberTemp", numberTemp);

                return map;
            } else {
                number = 1;
                numberTemp = number;
                numberStr = deptName + "〔" + year + "〕" + (number);
                map.put("numberStr", numberStr);
                map.put("numberTemp", numberTemp);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("numberStr", "编号错误");
            map.put("numberTemp", 0);
        }
        return map;
    }

    @Override
    @Transactional
    public Integer getNumberOnly(String custom, String characterValue, Integer year, Integer common, String itemId) {
        Integer number = 0;
        OrganWordDetail owd = null;
        try {
            OrganWord organWord = this.findByCustom(custom);
            if (null != organWord) {
                if (1 == common) {
                    itemId = "common";
                }
                owd = organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom, characterValue, year,
                    itemId);
                if (null != owd) {
                    number = owd.getNumber() + 1;
                } else {
                    OrganWordProperty property =
                        organWordPropertyService.findByOrganWordIdAndName(organWord.getId(), characterValue);
                    if (null != property) {
                        number = property.getInitNumber();
                    } else {
                        /**
                         * 标示不存在，则编号对应的机关代字也是不存在的，再保存编号对应的机关代字
                         */
                        property = new OrganWordProperty();
                        property.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        property.setInitNumber(1);
                        property.setName(characterValue);
                        property.setOrganWordId(organWord.getId());
                        property.setTabIndex(1);
                        organWordPropertyService.save(property);
                        number = 1;
                    }
                }
            } else {
                /**
                 * 标示不存在，说明是第一次编号，保存当前的编号
                 */
                UserInfo person = Y9LoginUserHolder.getUserInfo();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String organWordId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                organWord = new OrganWord();
                organWord.setId(organWordId);
                organWord.setCustom(custom);
                organWord.setName(characterValue);
                organWord.setCreateTime(sdf.format(new Date()));
                organWord.setUserName(person.getName());
                this.save(organWord);
                /**
                 * 标示不存在，则编号对应的机关代字也是不存在的，再保存编号对应的机关代字
                 */
                OrganWordProperty property = new OrganWordProperty();
                property.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                property.setInitNumber(1);
                property.setName(characterValue);
                property.setOrganWordId(organWordId);
                property.setTabIndex(1);

                organWordPropertyService.save(property);

                number = 1;
            }
            OrganWordUseHistory owuh = null;
            boolean isChange = false;
            do {
                owuh = organWordUseHistoryService.findByItemIdAndNumberString(itemId,
                    characterValue + "〔" + year + "〕" + number);
                if (null != owuh) {
                    number++;
                    isChange = true;
                }
            } while (null != owuh);
            if (isChange && null != owd) {
                owd.setNumber(number - 1);
                organWordDetailService.save(owd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    @Override
    public Page<OrganWord> list(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return organWordRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void removeOrganWords(String[] organWordIds) {
        for (String organWordId : organWordIds) {
            organWordRepository.deleteById(organWordId);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> save(OrganWord organWord) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String id = organWord.getId(), personName = person.getName();
            if (StringUtils.isNotEmpty(id)) {
                OrganWord oldow = this.findOne(id);
                if (null != oldow) {
                    oldow.setCustom(organWord.getCustom());
                    oldow.setUserName(personName);
                    oldow.setName(organWord.getName());
                    organWordRepository.save(oldow);
                } else {
                    organWordRepository.save(organWord);
                }
                map.put(UtilConsts.SUCCESS, true);
                return map;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                OrganWord newOw = new OrganWord();
                newOw.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newOw.setCreateTime(sdf.format(new Date()));
                newOw.setUserName(person.getName());
                newOw.setCustom(organWord.getCustom());
                newOw.setName(organWord.getName());
                organWordRepository.save(newOw);

                map.put(UtilConsts.SUCCESS, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
