package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.receive.ReceiveDepartment;
import net.risesoft.entity.receive.ReceivePerson;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.receive.ReceiveDepartmentRepository;
import net.risesoft.repository.receive.ReceivePersonRepository;
import net.risesoft.service.ReceiveDeptAndPersonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ReceiveDeptAndPersonServiceImpl implements ReceiveDeptAndPersonService {

    private final ReceiveDepartmentRepository receiveDepartmentRepository;

    private final ReceivePersonRepository receivePersonRepository;

    private final DepartmentApi departmentApi;

    private final OrgUnitApi orgUnitApi;

    @Override
    public Integer countByDeptId(String deptId) {
        return receivePersonRepository.countByDeptId(deptId);
    }

    @Override
    @Transactional
    public Y9Result<String> delDepartment(String id) {
        try {
            ReceiveDepartment receiveDeptAndPerson = receiveDepartmentRepository.findByDeptId(id);
            setParentId(id, receiveDeptAndPerson.getParentId());
            receiveDepartmentRepository.delete(receiveDeptAndPerson);
            List<ReceivePerson> personList = receivePersonRepository.findByDeptId(id);
            receivePersonRepository.deleteAll(personList);

            return Y9Result.successMsg("取消成功");
        } catch (Exception e) {
            LOGGER.error("取消失败:id:{}", id, e);
            return Y9Result.failure("取消失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<String> delPerson(String id) {
        try {
            receivePersonRepository.deleteById(id);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除失败:id:{}", id, e);
            return Y9Result.failure("删除失败");
        }
    }

    @Override
    public ReceiveDepartment findByDeptId(String id) {
        return receiveDepartmentRepository.findByDeptId(id);
    }

    /**
     * 获取上一parentId
     *
     * @param deptId 部门id
     * @param list 父级部门信息
     * @return 父级部门信息
     */
    public List<Object> getParentId(String deptId, List<Object> list) {
        ReceiveDepartment receiveDept = receiveDepartmentRepository.findByDeptId(deptId);
        if (receiveDept == null || receiveDept.getId() == null) {
            Department dept = departmentApi.get(Y9LoginUserHolder.getTenantId(), deptId).getData();
            if (dept != null && dept.getId() != null) {
                list = getParentId(dept.getParentId(), list);
            } else {
                return list;
            }
        } else {
            list = new ArrayList<>();
            list.add(true);
            list.add(deptId);
            return list;
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> personList(String deptId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<ReceivePerson> personList = receivePersonRepository.findByDeptId(deptId);
        String tenantId = Y9LoginUserHolder.getTenantId();
        for (ReceivePerson receivePerson : personList) {
            Map<String, Object> m = new HashMap<>(16);
            OrgUnit person = orgUnitApi.getPersonOrPosition(tenantId, receivePerson.getPersonId()).getData();
            if (person == null || person.getId() == null || Boolean.TRUE.equals(person.getDisabled())) {
                receivePersonRepository.delete(receivePerson);
                continue;
            }
            m.put("id", receivePerson.getId());
            m.put("userId", person.getId());
            m.put("parentId", receivePerson.getPersonDeptId());
            m.put("name", person.getName());
            m.put("duty", "");
            m.put("send", receivePerson.isSend() ? "是" : "否");
            m.put("receive", receivePerson.isReceive() ? "是" : "否");
            list.add(m);
        }
        return list;
    }

    @Override
    @Transactional
    public ReceiveDepartment save(ReceiveDepartment receiveDepartment) {
        return receiveDepartmentRepository.save(receiveDepartment);
    }

    @Override
    @Transactional
    public Y9Result<String> saveDepartment(String id) {
        try {
            ReceiveDepartment receiveDeptAndPerson = receiveDepartmentRepository.findByDeptId(id);
            if (receiveDeptAndPerson == null || receiveDeptAndPerson.getId() == null) {
                receiveDeptAndPerson = new ReceiveDepartment();
                receiveDeptAndPerson.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                Integer tabIndex = receiveDepartmentRepository.getMaxTabIndex();
                receiveDeptAndPerson.setTabIndex(tabIndex == null ? 0 : tabIndex + 1);
            }
            String tenantId = Y9LoginUserHolder.getTenantId();
            Department dept = departmentApi.get(tenantId, id).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnitBureau(tenantId, id).getData();
            receiveDeptAndPerson.setBureauId(orgUnit.getId());
            receiveDeptAndPerson.setDeptName(dept.getName());
            receiveDeptAndPerson.setDeptId(id);
            receiveDeptAndPerson.setParentId(dept.getParentId());
            receiveDeptAndPerson.setCreateDate(new Date());
            receiveDepartmentRepository.save(receiveDeptAndPerson);

            return Y9Result.successMsg("设置成功");
        } catch (Exception e) {
            LOGGER.error("设置失败:id:{}", id, e);
            return Y9Result.failure("设置失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<String> saveOrder(String ids) {
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] idArr = ids.split(",");
                int tabIndex = 0;
                for (String guid : idArr) {
                    Optional<ReceiveDepartment> receiveDeptAndPersonOpt = receiveDepartmentRepository.findById(guid);
                    if (receiveDeptAndPersonOpt.isPresent()) {
                        ReceiveDepartment receiveDeptAndPerson = receiveDeptAndPersonOpt.get();
                        Department dept =
                            departmentApi.get(Y9LoginUserHolder.getTenantId(), receiveDeptAndPerson.getDeptId())
                                .getData();
                        receiveDeptAndPerson.setDeptName(dept.getName());
                        receiveDeptAndPerson.setTabIndex(tabIndex);
                        tabIndex += 1;
                        receiveDepartmentRepository.save(receiveDeptAndPerson);
                    }
                }
            }
            return Y9Result.successMsg("保存排序成功");
        } catch (Exception e) {
            LOGGER.error("保存排序失败");
            return Y9Result.failure("保存排序失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<String> savePosition(String deptId, String ids) {
        try {
            if (StringUtils.isBlank(ids)) {
                return Y9Result.successMsg("保存成功");
            }

            String[] userIds = ids.split(",");
            String tenantId = Y9LoginUserHolder.getTenantId();
            Department dept = departmentApi.get(tenantId, deptId).getData();

            if (dept == null) {
                return Y9Result.failure("部门信息不存在");
            }

            StringBuilder msg = new StringBuilder();
            String validUserIds = processUserPositions(userIds, deptId, dept, tenantId, msg);

            updateReceiveDepartment(deptId, dept, tenantId, validUserIds);

            return Y9Result
                .successMsg((msg.length() > 0 && StringUtils.isNotBlank(msg.toString())) ? msg.toString() : "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存收文员职位失败，deptId: {}", deptId, e);
            return Y9Result.failure("保存失败");
        }
    }

    /**
     * 处理用户职位信息
     */
    private String processUserPositions(String[] userIds, String deptId, Department dept, String tenantId,
        StringBuilder msg) {
        StringBuilder validUserIds = new StringBuilder();

        for (String userId : userIds) {
            try {
                OrgUnit user = orgUnitApi.getPersonOrPosition(tenantId, userId).getData();
                if (user == null) {
                    continue;
                }

                List<ReceivePerson> existingPersons = receivePersonRepository.findByPersonId(userId);
                boolean canAdd = checkUserAddPermission(existingPersons, deptId, userId, tenantId, msg);

                if (canAdd) {
                    if (validUserIds.length() > 0) {
                        validUserIds.append(",").append(userId);
                    } else {
                        validUserIds.append(userId);
                    }
                    saveOrUpdateReceivePerson(userId, deptId, dept, user);
                }
            } catch (Exception e) {
                LOGGER.warn("处理用户职位信息失败，userId: {}", userId, e);
            }
        }

        return validUserIds.toString();
    }

    /**
     * 检查用户添加权限
     */
    private boolean checkUserAddPermission(List<ReceivePerson> existingPersons, String deptId, String userId,
        String tenantId, StringBuilder msg) {
        if (existingPersons == null || existingPersons.isEmpty()) {
            return true;
        }

        boolean canAdd = true;
        for (ReceivePerson receivePerson : existingPersons) {
            try {
                OrgUnit orgUnit = orgUnitApi.getOrgUnitBureau(tenantId, receivePerson.getDeptId()).getData();
                OrgUnit orgUnit1 = orgUnitApi.getOrgUnitBureau(tenantId, deptId).getData();

                // 委办局相同，且部门不相同则，不可添加该收文员
                if (orgUnit.getId().equals(orgUnit1.getId()) && !receivePerson.getDeptId().equals(deptId)) {
                    canAdd = false;
                    // 同一个委办局，不能设置一个人为两个单位的收文员
                    OrgUnit person = orgUnitApi.getPersonOrPosition(tenantId, userId).getData();
                    msg.append("[")
                        .append(person != null ? person.getName() : userId)
                        .append("已是")
                        .append(receivePerson.getDeptName())
                        .append("部门的收发员]<br>");
                }
            } catch (Exception e) {
                LOGGER.warn("检查用户添加权限失败，userId: {}, deptId: {}", userId, receivePerson.getDeptId(), e);
            }
        }

        return canAdd;
    }

    /**
     * 保存或更新收文员信息
     */
    private void saveOrUpdateReceivePerson(String userId, String deptId, Department dept, OrgUnit user) {
        ReceivePerson receivePerson = receivePersonRepository.findByPersonIdAndDeptId(userId, deptId);

        if (receivePerson == null) {
            receivePerson = new ReceivePerson();
            receivePerson.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            receivePerson.setReceive(true);
            receivePerson.setSend(true);
        }

        receivePerson.setCreateDate(new Date());
        receivePerson.setDeptId(deptId);
        receivePerson.setPersonId(userId);
        receivePerson.setDeptName(dept.getName());
        receivePerson.setPersonDeptId(user.getParentId());

        receivePersonRepository.save(receivePerson);
    }

    /**
     * 更新收文部门信息
     */
    private void updateReceiveDepartment(String deptId, Department dept, String tenantId, String validUserIds) {
        ReceiveDepartment receiveDepartment = receiveDepartmentRepository.findByDeptId(deptId);
        boolean isNew = (receiveDepartment == null || receiveDepartment.getId() == null);

        if (isNew) {
            receiveDepartment = createNewReceiveDepartment(deptId, dept, tenantId);
        }

        // 修改部门父节点
        String parentId = StringUtils.isNotBlank(validUserIds) ? deptId : receiveDepartment.getParentId();
        setParentId(deptId, parentId);

        // 更新父节点信息
        updateParentInfo(receiveDepartment, dept);

        receiveDepartmentRepository.save(receiveDepartment);
    }

    /**
     * 创建新的收文部门
     */
    private ReceiveDepartment createNewReceiveDepartment(String deptId, Department dept, String tenantId) {
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitBureau(tenantId, deptId).getData();
            ReceiveDepartment receiveDepartment = new ReceiveDepartment();
            receiveDepartment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            receiveDepartment.setDeptName(dept.getName());
            receiveDepartment.setBureauId(orgUnit != null ? orgUnit.getId() : "");
            receiveDepartment.setDeptId(deptId);
            receiveDepartment.setCreateDate(new Date());
            receiveDepartment.setParentId("");
            return receiveDepartment;
        } catch (Exception e) {
            LOGGER.warn("创建新的收文部门失败，deptId: {}", deptId, e);
            ReceiveDepartment receiveDepartment = new ReceiveDepartment();
            receiveDepartment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            receiveDepartment.setDeptName(dept.getName());
            receiveDepartment.setDeptId(deptId);
            receiveDepartment.setCreateDate(new Date());
            receiveDepartment.setParentId("");
            return receiveDepartment;
        }
    }

    /**
     * 更新父节点信息
     */
    private void updateParentInfo(ReceiveDepartment receiveDepartment, Department dept) {
        try {
            receiveDepartment.setParentId("");
            List<Object> parentInfo = new ArrayList<>();
            parentInfo.add(false);
            parentInfo = getParentId(dept.getParentId(), parentInfo);

            if (!parentInfo.isEmpty() && parentInfo.get(0) instanceof Boolean && (Boolean)parentInfo.get(0)) {
                if (parentInfo.size() > 1 && parentInfo.get(1) instanceof String) {
                    receiveDepartment.setParentId((String)parentInfo.get(1));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("更新父节点信息失败，deptId: {}", dept.getId(), e);
        }
    }

    /**
     * 往下设parentId
     *
     * @param deptId 部门id
     * @param parentId 父部门id
     */
    public void setParentId(String deptId, String parentId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Department> list = departmentApi.listByParentId(tenantId, deptId).getData();
        for (Department dept : list) {
            ReceiveDepartment receiveDeptAndPerson = receiveDepartmentRepository.findByDeptId(dept.getId());
            if (receiveDeptAndPerson != null && receiveDeptAndPerson.getId() != null) {
                receiveDeptAndPerson.setParentId(parentId);
                receiveDepartmentRepository.save(receiveDeptAndPerson);
            } else {
                setParentId(dept.getId(), parentId);
            }
        }
    }

    @Override
    @Transactional
    public Y9Result<String> setReceive(boolean receive, String ids) {
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] idArr = ids.split(",");
                for (String id : idArr) {
                    ReceivePerson receivePerson = receivePersonRepository.findById(id).orElse(null);
                    if (receivePerson != null) {
                        receivePerson.setReceive(receive);
                        receivePersonRepository.save(receivePerson);
                    }
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
            return Y9Result.failure("保存失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<String> setSend(boolean send, String ids) {
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] idArr = ids.split(",");
                for (String id : idArr) {
                    ReceivePerson receivePerson = receivePersonRepository.findById(id).orElse(null);
                    if (receivePerson != null) {
                        receivePerson.setSend(send);
                        receivePersonRepository.save(receivePerson);
                    }
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
            return Y9Result.failure("保存失败");
        }
    }

}
