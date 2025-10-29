package net.risesoft.service.organword.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.cache.PersonRoleApi;
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.PunctuationConsts;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.organword.ItemOrganWordBind;
import net.risesoft.entity.organword.OrganWord;
import net.risesoft.entity.organword.OrganWordDetail;
import net.risesoft.entity.organword.OrganWordProperty;
import net.risesoft.entity.organword.OrganWordUseHistory;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemOrganWordEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OrganWordModel;
import net.risesoft.model.itemadmin.OrganWordPropertyModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.organword.OrganWordRepository;
import net.risesoft.service.config.ItemOrganWordBindService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.organword.OrganWordDetailService;
import net.risesoft.service.organword.OrganWordPropertyService;
import net.risesoft.service.organword.OrganWordService;
import net.risesoft.service.organword.OrganWordUseHistoryService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
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

    private final TaskApi taskApi;

    private final OrganWordService self;

    public OrganWordServiceImpl(
        OrganWordRepository organWordRepository,
        ProcessParamService processParamService,
        OrganWordPropertyService organWordPropertyService,
        OrganWordDetailService organWordDetailService,
        ItemOrganWordBindService itemOrganWordBindService,
        OrganWordUseHistoryService organWordUseHistoryService,
        OrgUnitApi orgUnitApi,
        PersonRoleApi personRoleApi,
        PositionRoleApi positionRoleApi,
        TaskApi taskApi,
        @Lazy OrganWordService self) {
        this.organWordRepository = organWordRepository;
        this.processParamService = processParamService;
        this.organWordPropertyService = organWordPropertyService;
        this.organWordDetailService = organWordDetailService;
        this.itemOrganWordBindService = itemOrganWordBindService;
        this.organWordUseHistoryService = organWordUseHistoryService;
        this.orgUnitApi = orgUnitApi;
        this.personRoleApi = personRoleApi;
        this.positionRoleApi = positionRoleApi;
        this.taskApi = taskApi;
        this.self = self;
    }

    @Override
    public boolean checkCustom(String id, String custom) {
        OrganWord organWord = organWordRepository.findByCustom(custom);
        if (null == organWord) {
            return true;
        }
        if (StringUtils.isBlank(id)) {
            return false;
        }
        Optional<OrganWord> optional = organWordRepository.findById(id);
        return optional.filter(word -> organWord.getId().equals(word.getId())).isPresent();
    }

    @Override
    @Transactional
    public Integer checkNumberStr(String characterValue, String custom, Integer year, Integer numberTemp, String itemId,
        Integer common, String processSerialNumber) {
        int status = 3;
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            OrganWord organWord = this.findByCustom(custom);
            if (null != organWord) {
                if (1 == common) {
                    itemId = ItemConsts.COMMON_KEY;
                }
                OrganWordDetail owd = organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom,
                    characterValue, year, itemId);
                /*
                 * 1.使用详情存在
                 */
                if (null != owd) {
                    status =
                        checkOrganWordUseHistory(characterValue, custom, year, numberTemp, itemId, processSerialNumber);
                    /*
                     * 传入的数值等于使用详情的当前值+1时，保存使用详情，为的是下次正常编号的时候，会从编号的当前值+1开始
                     */
                    Integer number = owd.getNumber() + 1;
                    if (numberTemp.equals(number) && status == 1) {
                        owd.setNumber(numberTemp);
                        owd.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                        organWordDetailService.save(owd);
                    }
                } else {
                    OrganWordProperty property =
                        organWordPropertyService.findByOrganWordIdAndName(organWord.getId(), characterValue);
                    Integer num = property.getInitNumber();
                    OrganWordUseHistory organWordUseHistory = new OrganWordUseHistory();
                    organWordUseHistory.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    organWordUseHistory.setItemId(itemId);
                    organWordUseHistory.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                    organWordUseHistory.setCustom(custom);
                    organWordUseHistory.setProcessSerialNumber(processSerialNumber);
                    organWordUseHistory.setTenantId(Y9LoginUserHolder.getTenantId());
                    organWordUseHistory.setUserId(person.getParentId());
                    organWordUseHistory.setUserName(person.getName());
                    organWordUseHistory.setNumberString(characterValue + "〔" + year + "〕" + (num));
                    organWordUseHistoryService.save(organWordUseHistory);

                    OrganWordDetail newOrganWordDetail = new OrganWordDetail();
                    newOrganWordDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newOrganWordDetail.setItemId(itemId);
                    newOrganWordDetail.setTenantId(Y9LoginUserHolder.getTenantId());
                    newOrganWordDetail.setNumber(num);
                    newOrganWordDetail.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                    newOrganWordDetail.setItemId(itemId);
                    newOrganWordDetail.setYear(year);
                    newOrganWordDetail.setCharacterValue(characterValue);
                    newOrganWordDetail.setCustom(custom);
                    organWordDetailService.save(newOrganWordDetail);
                    return 1;
                }
            } else {
                status = 2;
            }
        } catch (Exception e) {
            LOGGER.error("验证编号失败", e);
        }
        return status;
    }

    @Override
    @Transactional
    public Integer checkNumberStr4DeptName(String custom, Integer year, Integer numberTemp, String itemId,
        Integer common, String processSerialNumber) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            OrgUnit bureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), person.getParentId()).getData();
            String deptName = bureau.getName();
            if (1 == common) {
                itemId = ItemConsts.COMMON_KEY;
            }
            OrganWordDetail owd =
                organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom, deptName, year, itemId);
            if (null != owd) {
                Integer number = owd.getNumber();
                if (numberTemp <= number) {
                    return 0;
                } else {
                    OrganWordUseHistory organWordUseHistory = new OrganWordUseHistory();
                    organWordUseHistory.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    organWordUseHistory.setItemId(itemId);
                    organWordUseHistory.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                    organWordUseHistory.setCustom(custom);
                    organWordUseHistory.setProcessSerialNumber(processSerialNumber);
                    organWordUseHistory.setTenantId(Y9LoginUserHolder.getTenantId());
                    organWordUseHistory.setUserId(person.getParentId());
                    organWordUseHistory.setUserName(person.getName());
                    organWordUseHistory.setNumberString(deptName + "〔" + year + "〕" + (numberTemp));
                    organWordUseHistoryService.save(organWordUseHistory);

                    owd.setNumber(numberTemp);
                    owd.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                    organWordDetailService.save(owd);
                    return 1;
                }
            } else {
                OrganWordUseHistory organWordUseHistory = new OrganWordUseHistory();
                organWordUseHistory.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                organWordUseHistory.setItemId(itemId);
                organWordUseHistory.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                organWordUseHistory.setCustom(custom);
                organWordUseHistory.setProcessSerialNumber(processSerialNumber);
                organWordUseHistory.setTenantId(Y9LoginUserHolder.getTenantId());
                organWordUseHistory.setUserId(person.getParentId());
                organWordUseHistory.setUserName(person.getName());
                organWordUseHistory.setNumberString(deptName + "〔" + year + "〕" + (numberTemp));
                organWordUseHistoryService.save(organWordUseHistory);

                OrganWordDetail newOrganWordDetail = new OrganWordDetail();
                newOrganWordDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newOrganWordDetail.setItemId(itemId);
                newOrganWordDetail.setTenantId(Y9LoginUserHolder.getTenantId());
                newOrganWordDetail.setNumber(numberTemp);
                newOrganWordDetail.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                newOrganWordDetail.setItemId(itemId);
                newOrganWordDetail.setYear(year);
                newOrganWordDetail.setCharacterValue(deptName);
                newOrganWordDetail.setCustom(custom);
                organWordDetailService.save(newOrganWordDetail);
                return 1;
            }
        } catch (Exception e) {
            LOGGER.error("验证部门名称编号失败", e);
            return 3;
        }
    }

    public Integer checkOrganWordUseHistory(String characterValue, String custom, Integer year, Integer numberTemp,
        String itemId, String processSerialNumber) {

        UserInfo person = Y9LoginUserHolder.getUserInfo();
        /*
         * 1、检查当前的编号有没有被使用
         */
        OrganWordUseHistory organWordUseHistory = organWordUseHistoryService.findByItemIdAndNumberString(itemId,
            characterValue + "〔" + year + "〕" + numberTemp);
        /*
         * 2当前号没有被使用，则检查当前文之前有没有编过号
         */
        if (null == organWordUseHistory) {
            OrganWordUseHistory oldOrganWordUseHistory =
                organWordUseHistoryService.findByProcessSerialNumberAndCustom(processSerialNumber, custom);
            /*
             * 3如果当前processSerialNumber对应的文已经编号（先编号，再修改编号保存）
             */
            if (null != oldOrganWordUseHistory) {
                OrganWordDetail organWordDetail = organWordDetailService
                    .findByCustomAndCharacterValueAndYearAndItemId(custom, characterValue, year, itemId);
                /*
                 * 4如果之前的编号的数字等于使用详情的最大使用数字，则把使用详情的数字减一（因为会使用最新的编号，需要把之前占的号释放）
                 */
                if (null != organWordDetail && oldOrganWordUseHistory.getNumberString()
                    .equals(characterValue + PunctuationConsts.LEFTHEXBRACKETS + year
                        + PunctuationConsts.RIGHTHEXBRACKETS + organWordDetail.getNumber())) {
                    organWordDetail.setNumber(organWordDetail.getNumber() - 1);
                    organWordDetailService.save(organWordDetail);
                }
                oldOrganWordUseHistory.setUserId(person.getPersonId());
                oldOrganWordUseHistory.setUserName(person.getName());
                oldOrganWordUseHistory.setNumberString(characterValue + "〔" + year + "〕" + numberTemp);
                organWordUseHistoryService.save(oldOrganWordUseHistory);
            } else {
                /*
                 * 5没有被使用过
                 */
                organWordUseHistory = new OrganWordUseHistory();
                organWordUseHistory.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                organWordUseHistory.setItemId(itemId);
                organWordUseHistory.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                organWordUseHistory.setCustom(custom);
                organWordUseHistory.setProcessSerialNumber(processSerialNumber);
                organWordUseHistory.setTenantId(Y9LoginUserHolder.getTenantId());
                organWordUseHistory.setUserId(person.getPersonId());
                organWordUseHistory.setUserName(person.getName());
                organWordUseHistory.setNumberString(characterValue + "〔" + year + "〕" + numberTemp);
                organWordUseHistoryService.save(organWordUseHistory);
            }
            return 1;
        } else {
            /*
             * 6当前processSerialNumber正在使用
             */
            if (processSerialNumber.equals(organWordUseHistory.getProcessSerialNumber())) {
                return 1;
            } else {
                /*
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
        OrganWordUseHistory organWordUseHistory =
            organWordUseHistoryService.findByProcessSerialNumberAndCustom(processSerialNumber, custom);
        OrganWordModel word = new OrganWordModel();
        word.setExist(false);

        if (null != organWordUseHistory) {
            word.setExist(true);
            word.setNumberString(organWordUseHistory.getNumberString());
            return word;
        }
        if (!itembox.equals(ItemBoxTypeEnum.TODO.getValue())) {
            return word;
        }
        word.setHasPermission(false);
        String taskDefKey;
        String itemId;
        String processDefinitionId;
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
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
                List<OrganWordProperty> propertyList = organWordPropertyService.listByOrganWordId(organWord.getId());
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
    public OrganWord findByCustom(String custom) {
        return organWordRepository.findByCustom(custom);
    }

    @Override
    @Transactional
    public OrganWord findOne(String id) {
        return organWordRepository.findById(id).orElse(null);
    }

    @Override
    public Integer getNumber(String custom, String characterValue, Integer year, Integer common, String itemId) {
        try {
            if (1 == common) {
                itemId = ItemConsts.COMMON_KEY;
            }

            OrganWord organWord = this.findByCustom(custom);
            Integer currentNumber = getCurrentNumber(organWord, custom, characterValue, year, itemId);

            // 检查编号是否已被使用，如果已被使用则递增查找可用编号
            String numberString;
            boolean isChange = false;
            do {
                numberString = characterValue + "〔" + year + "〕" + currentNumber;
                OrganWordUseHistory organWordUseHistory =
                    organWordUseHistoryService.findByItemIdAndNumberString(itemId, numberString);
                if (organWordUseHistory != null) {
                    currentNumber++;
                    isChange = true;
                } else {
                    break;
                }
            } while (true);

            // 如果编号有变化且存在详情记录，则更新详情记录
            if (isChange) {
                updateOrganWordDetailIfChanged(organWord, custom, characterValue, year, itemId, currentNumber);
            }

            return currentNumber;
        } catch (Exception e) {
            LOGGER.error("获取编号失败", e);
            return 0;
        }
    }

    /**
     * 获取当前编号值
     */
    private Integer getCurrentNumber(OrganWord organWord, String custom, String characterValue, Integer year,
        String itemId) {
        if (organWord != null) {
            return getCurrentNumberForExistingOrganWord(organWord, custom, characterValue, year, itemId);
        } else {
            return createNewOrganWordAndReturnInitialNumber(custom, characterValue);
        }
    }

    /**
     * 为已存在的机关字获取当前编号
     */
    private Integer getCurrentNumberForExistingOrganWord(OrganWord organWord, String custom, String characterValue,
        Integer year, String itemId) {
        OrganWordDetail owd =
            organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom, characterValue, year, itemId);

        if (owd != null) {
            return owd.getNumber() + 1;
        } else {
            return getInitialNumberFromProperty(organWord, characterValue);
        }
    }

    /**
     * 从属性中获取初始编号
     */
    private Integer getInitialNumberFromProperty(OrganWord organWord, String characterValue) {
        OrganWordProperty property =
            organWordPropertyService.findByOrganWordIdAndName(organWord.getId(), characterValue);
        if (property != null) {
            return property.getInitNumber();
        } else {
            // 属性不存在，创建新的属性
            createNewOrganWordProperty(organWord.getId(), characterValue);
            return 1;
        }
    }

    /**
     * 创建新的机关字属性
     */
    private void createNewOrganWordProperty(String organWordId, String characterValue) {
        OrganWordProperty property = new OrganWordProperty();
        property.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        property.setInitNumber(1);
        property.setName(characterValue);
        property.setOrganWordId(organWordId);
        property.setTabIndex(1);
        organWordPropertyService.save(property);
    }

    /**
     * 创建新的机关字并返回初始编号
     */
    private Integer createNewOrganWordAndReturnInitialNumber(String custom, String characterValue) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String organWordId = Y9IdGenerator.genId(IdType.SNOWFLAKE);

        OrganWord organWord = new OrganWord();
        organWord.setId(organWordId);
        organWord.setCustom(custom);
        organWord.setName(characterValue);
        organWord.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        organWord.setUserName(person.getName());
        self.save(organWord);

        // 创建对应的机关字属性
        createNewOrganWordProperty(organWordId, characterValue);

        return 1;
    }

    /**
     * 如果编号有变化且存在详情记录，则更新详情记录
     */
    private void updateOrganWordDetailIfChanged(OrganWord organWord, String custom, String characterValue, Integer year,
        String itemId, Integer currentNumber) {
        if (organWord != null) {
            OrganWordDetail owd = organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom,
                characterValue, year, itemId);
            if (owd != null) {
                owd.setNumber(currentNumber - 1);
                organWordDetailService.save(owd);
            }
        }
    }

    @Override
    public Map<String, Object> getNumber4DeptName(String custom, Integer year, Integer common, String itemId) {
        Map<String, Object> map = new HashMap<>(16);
        String numberStr;
        Integer number;
        int numberTemp;
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            OrgUnit bureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), person.getParentId()).getData();
            String deptName = bureau.getName();
            if (1 == common) {
                itemId = ItemConsts.COMMON_KEY;
            }
            OrganWordDetail owd =
                organWordDetailService.findByCustomAndCharacterValueAndYearAndItemId(custom, deptName, year, itemId);
            if (null != owd) {
                number = owd.getNumber();
                numberTemp = number + 1;
                numberStr = deptName + "〔" + year + "〕" + (numberTemp);
            } else {
                number = 1;
                numberTemp = number;
                numberStr = deptName + "〔" + year + "〕" + (number);
            }
            map.put("numberStr", numberStr);
            map.put("numberTemp", numberTemp);
            return map;
        } catch (Exception e) {
            LOGGER.error("获取部门名称编号失败", e);
            map.put("numberStr", "编号错误");
            map.put("numberTemp", 0);
        }
        return map;
    }

    @Override
    @Transactional
    public Integer getNumberOnly(String custom, String characterValue, Integer year, Integer common, String itemId) {
        try {
            if (1 == common) {
                itemId = ItemConsts.COMMON_KEY;
            }
            OrganWord organWord = this.findByCustom(custom);
            Integer currentNumber = getCurrentNumber(organWord, custom, characterValue, year, itemId);
            // 检查编号是否已被使用，如果已被使用则递增查找可用编号
            String numberString;
            boolean isChange = false;
            do {
                numberString = characterValue + "〔" + year + "〕" + currentNumber;
                OrganWordUseHistory organWordUseHistory =
                    organWordUseHistoryService.findByItemIdAndNumberString(itemId, numberString);
                if (organWordUseHistory != null) {
                    currentNumber++;
                    isChange = true;
                } else {
                    break;
                }
            } while (true);
            // 如果编号有变化且存在详情记录，则更新详情记录
            if (isChange) {
                updateOrganWordDetailIfChanged(organWord, custom, characterValue, year, itemId, currentNumber);
            }

            return currentNumber;
        } catch (Exception e) {
            LOGGER.error("获取编号失败:{}", e.getMessage());
            return 0;
        }
    }

    @Override
    public String getTempNumber(String custom, String characterValue, String itemId) {
        String numberStr = "";
        int number;
        try {
            OrganWord organWord = this.findByCustom(custom);
            if (null != organWord) {
                String numberType = organWord.getNumberType();
                if (numberType.equals(ItemOrganWordEnum.PURE_NUMBER.getValue())) {
                    Integer oldNumber = organWordDetailService.getMaxNumber(custom, itemId);
                    if (null != oldNumber) {
                        number = oldNumber + 1;
                    } else {
                        number = 1;
                    }
                    numberStr = String.format("%0" + organWord.getNumberLength() + "d", number);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取临时编号失败", e);
        }
        return numberStr;
    }

    @Override
    public List<OrganWord> listAll() {
        return organWordRepository.findAllByOrderByCreateTimeAsc();
    }

    @Override
    public List<OrganWordPropertyModel> listByCustom(String itemId, String processDefinitionId, String taskDefKey,
        String custom) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        OrganWord organWord = this.findByCustom(custom);
        // 如果机关字不存在，返回空列表
        if (organWord == null) {
            return Collections.emptyList();
        }
        // 检查用户是否有权限
        if (!hasPermission(tenantId, itemId, processDefinitionId, taskDefKey, custom)) {
            return Collections.emptyList();
        }
        // 根据机关字类型返回相应属性列表
        return buildOrganWordPropertyList(organWord, custom);
    }

    /**
     * 检查用户是否有权限访问指定的机关字
     */
    private boolean hasPermission(String tenantId, String itemId, String processDefinitionId, String taskDefKey,
        String custom) {
        ItemOrganWordBind bind =
            itemOrganWordBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(itemId,
                processDefinitionId, taskDefKey, custom);

        // 如果没有绑定权限配置，默认有权限
        if (bind == null) {
            return true;
        }

        List<String> roleIds = bind.getRoleIds();
        // 如果角色列表为空，默认有权限
        if (roleIds.isEmpty()) {
            return true;
        }

        // 检查用户是否拥有任意一个角色
        String userId = Y9FlowableHolder.getOrgUnitId();
        return roleIds.stream()
            .map(roleId -> checkRolePermission(tenantId, roleId, userId))
            .anyMatch(Boolean.TRUE::equals);
    }

    /**
     * 构建机关字属性列表
     */
    private List<OrganWordPropertyModel> buildOrganWordPropertyList(OrganWord organWord, String custom) {
        List<OrganWordPropertyModel> retList = new ArrayList<>();

        // 纯数字类型特殊处理
        if (custom.equals(ItemOrganWordEnum.PURE_NUMBER.getValue())) {
            OrganWordPropertyModel model = new OrganWordPropertyModel();
            model.setHasPermission(true);
            model.setName(ItemOrganWordEnum.PURE_NUMBER.getName());
            model.setCustom(custom);
            retList.add(model);
            return retList;
        }

        // 普通类型处理
        List<OrganWordProperty> propertyList = organWordPropertyService.listByOrganWordId(organWord.getId());
        for (OrganWordProperty op : propertyList) {
            OrganWordPropertyModel model = new OrganWordPropertyModel();
            model.setHasPermission(true);
            model.setName(op.getName());
            model.setCustom(organWord.getCustom());
            model.setInitNumber(op.getInitNumber());
            retList.add(model);
        }

        return retList;
    }

    @Override
    public List<OrganWordPropertyModel> listByCustomNumber(String itemId, String processDefinitionId,
        String taskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9FlowableHolder.getOrgUnitId();

        List<ItemOrganWordBind> bindList = itemOrganWordBindService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);

        if (bindList.isEmpty()) {
            return createEmptyPermissionList();
        }

        return buildOrganWordPropertyListWithPermission(tenantId, userId, bindList);
    }

    /**
     * 创建无权限的属性列表
     */
    private List<OrganWordPropertyModel> createEmptyPermissionList() {
        List<OrganWordPropertyModel> retList = new ArrayList<>();
        OrganWordPropertyModel model = new OrganWordPropertyModel();
        model.setHasPermission(false);
        retList.add(model);
        return retList;
    }

    /**
     * 构建带权限检查的机关字属性列表
     */
    private List<OrganWordPropertyModel> buildOrganWordPropertyListWithPermission(String tenantId, String userId,
        List<ItemOrganWordBind> bindList) {
        List<OrganWordPropertyModel> retList = new ArrayList<>();

        for (ItemOrganWordBind bind : bindList) {
            OrganWordPropertyModel model = new OrganWordPropertyModel();
            model.setCustom(bind.getOrganWordCustom());
            model.setHasPermission(checkPermission(tenantId, userId, bind.getRoleIds()));
            retList.add(model);
        }

        return retList;
    }

    /**
     * 检查用户权限
     */
    private boolean checkPermission(String tenantId, String userId, List<String> roleIds) {
        // 如果角色列表为空，默认有权限
        if (roleIds.isEmpty()) {
            return true;
        }

        // 检查用户是否拥有任意一个角色
        return roleIds.stream()
            .map(roleId -> checkRolePermission(tenantId, roleId, userId))
            .anyMatch(Boolean.TRUE::equals);
    }

    /**
     * 检查用户是否拥有指定角色（复用已有的方法）
     */
    private Boolean checkRolePermission(String tenantId, String roleId, String userId) {
        try {
            return positionRoleApi.hasRole(tenantId, roleId, userId).getData();
        } catch (Exception e) {
            LOGGER.warn("检查角色权限失败，tenantId: {}, roleId: {}, userId: {}", tenantId, roleId, userId, e);
            return false;
        }
    }

    @Override
    public Page<OrganWord> pageAll(int page, int rows) {
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
    public OrganWord save(OrganWord organWord) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String id = organWord.getId(), personName = person.getName();
        if (StringUtils.isNotEmpty(id)) {
            OrganWord oldOrganWord = self.findOne(id);
            if (null != oldOrganWord) {
                oldOrganWord.setCustom(organWord.getCustom());
                oldOrganWord.setUserName(personName);
                oldOrganWord.setName(organWord.getName());
                oldOrganWord.setNumberType(organWord.getNumberType());
                oldOrganWord.setNumberLength(organWord.getNumberLength());
                return organWordRepository.save(oldOrganWord);
            } else {
                return organWordRepository.save(organWord);
            }
        }

        OrganWord newOw = new OrganWord();
        newOw.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newOw.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        newOw.setUserName(person.getName());
        newOw.setCustom(organWord.getCustom());
        newOw.setName(organWord.getName());
        newOw.setNumberType(organWord.getNumberType());
        newOw.setNumberLength(organWord.getNumberLength());
        return organWordRepository.save(newOw);

    }

    @Override
    @Transactional
    public Map<String, Object> saveNumberString(String custom, String numberString, String itemId,
        String processSerialNumber) {

        Map<String, Object> retMap = new HashMap<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String tenantId = Y9LoginUserHolder.getTenantId();
        UserInfo person = Y9LoginUserHolder.getUserInfo();

        try {
            // 检查编号是否已存在
            OrganWordUseHistory existingHistory =
                organWordUseHistoryService.findByItemIdAndNumberStringAndCustom(itemId, numberString, custom);

            if (existingHistory == null) {
                // 编号不存在，创建新的编号记录
                createNewNumberRecord(custom, numberString, itemId, processSerialNumber, tenantId, person, year,
                    retMap);
            } else {
                // 编号已存在，检查是否属于当前流程
                checkExistingNumberRecord(itemId, numberString, custom, processSerialNumber, retMap);
            }
        } catch (Exception e) {
            LOGGER.error("保存编号失败", e);
            retMap.put(ItemConsts.SUCCESS_KEY, false);
            retMap.put("msg", "出现异常，保存编号失败");
        }

        return retMap;
    }

    /**
     * 创建新的编号记录
     */
    private void createNewNumberRecord(String custom, String numberString, String itemId, String processSerialNumber,
        String tenantId, UserInfo person, int year, Map<String, Object> retMap) {
        OrganWord organWord = this.findByCustom(custom);
        String numberType = organWord.getNumberType();

        // 根据编号类型处理不同的逻辑
        if (numberType.equals(ItemOrganWordEnum.PURE_NUMBER.getValue())) {
            handlePureNumberType(custom, numberString, itemId, tenantId, year);
        } else if (numberType.equals(ItemOrganWordEnum.BUREAU_AREA_NUMBER.getValue())) {
            handleBureauAreaNumberType(custom, numberString, itemId, tenantId, year);
        }

        // 保存使用历史
        saveOrganWordUseHistory(custom, numberString, itemId, processSerialNumber, tenantId, person);

        retMap.put(ItemConsts.SUCCESS_KEY, true);
        retMap.put("msg", "保存编号成功");
    }

    /**
     * 处理纯数字类型编号
     */
    private void handlePureNumberType(String custom, String numberString, String itemId, String tenantId, int year) {
        OrganWordDetail oldDetail = organWordDetailService.findByCustomAndItemId(custom, itemId);
        if (oldDetail == null) {
            OrganWordDetail detail = new OrganWordDetail();
            detail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            detail.setNumber(1);
            detail.setCustom(custom);
            detail.setTenantId(tenantId);
            detail.setItemId(itemId);
            detail.setCharacterValue(numberString);
            detail.setYear(year);
            detail.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
            organWordDetailService.save(detail);
        } else {
            oldDetail.setNumber(oldDetail.getNumber() + 1);
            organWordDetailService.save(oldDetail);
        }
    }

    /**
     * 处理 bureau 区域编号类型
     */
    private void handleBureauAreaNumberType(String custom, String numberString, String itemId, String tenantId,
        int year) {
        String characterValue = numberString.substring(0, 2);
        String str = numberString.substring(2);

        Pattern pattern = Pattern.compile("(?<=\\d{2})(\\d+)");
        Matcher matcher = pattern.matcher(str);
        String replaceStr = matcher.find() ? matcher.group() : str;

        Integer number = Integer.valueOf(replaceStr);

        OrganWordDetail oldDetail =
            organWordDetailService.findByCustomAndCharacterValueAndItemId(custom, characterValue, itemId);

        if (oldDetail == null) {
            OrganWordDetail detail = new OrganWordDetail();
            detail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            detail.setNumber(number);
            detail.setCustom(custom);
            detail.setTenantId(tenantId);
            detail.setCharacterValue(characterValue);
            detail.setItemId(itemId);
            detail.setYear(year);
            detail.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
            organWordDetailService.save(detail);
        } else {
            oldDetail.setNumber(oldDetail.getNumber() + 1);
            organWordDetailService.save(oldDetail);
        }
    }

    /**
     * 保存机关字使用历史
     */
    private void saveOrganWordUseHistory(String custom, String numberString, String itemId, String processSerialNumber,
        String tenantId, UserInfo person) {
        OrganWordUseHistory history = new OrganWordUseHistory();
        history.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        history.setNumberString(numberString);
        history.setCustom(custom);
        history.setProcessSerialNumber(processSerialNumber);
        history.setItemId(itemId);
        history.setUserId(person.getPersonId());
        history.setUserName(person.getName());
        history.setTenantId(tenantId);
        history.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        organWordUseHistoryService.save(history);
    }

    /**
     * 检查已存在的编号记录
     */
    private void checkExistingNumberRecord(String itemId, String numberString, String custom,
        String processSerialNumber, Map<String, Object> retMap) {
        OrganWordUseHistory history = organWordUseHistoryService
            .findByItemIdAndNumberStrAndCustomAndProcessSerialNumber(itemId, numberString, custom, processSerialNumber);

        if (history != null) {
            retMap.put(ItemConsts.SUCCESS_KEY, true);
            retMap.put("msg", "当前编号已经保存");
        } else {
            retMap.put(ItemConsts.SUCCESS_KEY, false);
            retMap.put("msg", "当前编号已经被占用，请双击编号框重新生成新的编号！");
        }
    }
}
