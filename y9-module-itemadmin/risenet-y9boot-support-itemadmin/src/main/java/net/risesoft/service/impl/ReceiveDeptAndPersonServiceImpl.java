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

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.ReceiveDepartment;
import net.risesoft.entity.ReceivePerson;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ReceiveDepartmentRepository;
import net.risesoft.repository.jpa.ReceivePersonRepository;
import net.risesoft.service.ReceiveDeptAndPersonService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
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
    @Transactional(readOnly = false)
    public Y9Result<String> delDepartment(String id) {
        try {
            ReceiveDepartment receiveDeptAndPerson = receiveDepartmentRepository.findByDeptId(id);
            setParentId(id, receiveDeptAndPerson.getParentId());
            receiveDepartmentRepository.delete(receiveDeptAndPerson);
            List<ReceivePerson> personList = receivePersonRepository.findByDeptId(id);
            receivePersonRepository.deleteAll(personList);

            return Y9Result.successMsg("取消成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("取消失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> delPerson(String id) {
        try {
            receivePersonRepository.deleteById(id);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
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
     * @param deptId
     * @param list
     * @return
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
            OrgUnit person = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, receivePerson.getPersonId()).getData();
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
    @Transactional(readOnly = false)
    public ReceiveDepartment save(ReceiveDepartment receiveDepartment) {
        return receiveDepartmentRepository.save(receiveDepartment);
    }

    @Override
    @Transactional(readOnly = false)
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
            OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, id).getData();
            receiveDeptAndPerson.setBureauId(orgUnit.getId());
            receiveDeptAndPerson.setDeptName(dept.getName());
            receiveDeptAndPerson.setDeptId(id);
            receiveDeptAndPerson.setParentId(dept.getParentId());
            receiveDeptAndPerson.setCreateDate(new Date());
            receiveDepartmentRepository.save(receiveDeptAndPerson);

            return Y9Result.successMsg("设置成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("设置失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> saveOrder(String ids) {
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] idArr = ids.split(",");
                Integer tabIndex = 0;
                for (String guid : idArr) {
                    Optional<ReceiveDepartment> receiveDeptAndPersonOpt = receiveDepartmentRepository.findById(guid);
                    if (receiveDeptAndPersonOpt.isPresent()) {
                        ReceiveDepartment receiveDeptAndPerson = receiveDeptAndPersonOpt.get();
                        Department dept = departmentApi
                            .get(Y9LoginUserHolder.getTenantId(), receiveDeptAndPerson.getDeptId()).getData();
                        receiveDeptAndPerson.setDeptName(dept.getName());
                        receiveDeptAndPerson.setTabIndex(tabIndex);
                        tabIndex += 1;
                        receiveDepartmentRepository.save(receiveDeptAndPerson);
                    }
                }
            }
            return Y9Result.successMsg("保存排序成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存排序失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> savePosition(String deptId, String ids) {
        try {
            String[] id = ids.split(",");
            StringBuilder msg = new StringBuilder();

            String idsTemp = "";
            String tenantId = Y9LoginUserHolder.getTenantId();
            Department dept = departmentApi.get(tenantId, deptId).getData();
            for (String userId : id) {
                OrgUnit user = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                List<ReceivePerson> list = receivePersonRepository.findByPersonId(userId);
                if (list != null && !list.isEmpty()) {
                    boolean isAdd = true;
                    for (ReceivePerson receivePerson : list) {
                        OrgUnit orgUnit =
                            orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), receivePerson.getDeptId()).getData();
                        OrgUnit orgUnit1 = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), deptId).getData();
                        // 委办局相同，且部门不相同则，不可添加该收文员
                        if (orgUnit.getId().equals(orgUnit1.getId()) && !receivePerson.getDeptId().equals(deptId)) {
                            isAdd = false;
                            // 同一个委办局，不能设置一个人为两个单位的收文员
                            OrgUnit person = orgUnitApi
                                .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), userId).getData();
                            msg.append("[" + person.getName() + "已是" + receivePerson.getDeptName() + "部门的收发员]<br>");
                        }
                    }
                    if (isAdd) {
                        idsTemp = Y9Util.genCustomStr(idsTemp, userId);
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
                } else {
                    idsTemp = Y9Util.genCustomStr(idsTemp, userId);
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
            }
            ReceiveDepartment receiveDepartment = receiveDepartmentRepository.findByDeptId(deptId);
            if (receiveDepartment != null && receiveDepartment.getId() != null) {
                // 修改部门父节点
                if (StringUtils.isNotBlank(idsTemp)) {
                    setParentId(deptId, deptId);
                } else {
                    setParentId(deptId, receiveDepartment.getParentId());
                }
                receiveDepartment.setParentId("");
                List<Object> list = new ArrayList<>();
                list.add(false);
                list = getParentId(dept.getParentId(), list);
                if ((boolean)list.get(0)) {
                    receiveDepartment.setParentId((String)list.get(1));
                }
            } else {
                OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, deptId).getData();
                receiveDepartment = new ReceiveDepartment();
                receiveDepartment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                receiveDepartment.setDeptName(dept.getName());
                receiveDepartment.setBureauId(orgUnit.getId());
                receiveDepartment.setDeptId(deptId);
                receiveDepartment.setCreateDate(new Date());
                // 修改部门父节点
                if (StringUtils.isNotBlank(idsTemp)) {
                    setParentId(deptId, deptId);
                } else {
                    setParentId(deptId, receiveDepartment.getParentId());
                }
                receiveDepartment.setParentId("");
                List<Object> list = new ArrayList<>();
                list.add(false);
                list = getParentId(dept.getParentId(), list);
                if ((boolean)list.get(0)) {
                    receiveDepartment.setParentId((String)list.get(1));
                }
            }
            receiveDepartmentRepository.save(receiveDepartment);

            return Y9Result
                .successMsg((msg.length() > 0 && StringUtils.isNotBlank(msg.toString())) ? msg.toString() : "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }
    }

    /**
     * 往下设parentId
     *
     * @param deptId
     * @param parentId
     */
    @Transactional(readOnly = false)
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
    @Transactional(readOnly = false)
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
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
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
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }
    }

}
