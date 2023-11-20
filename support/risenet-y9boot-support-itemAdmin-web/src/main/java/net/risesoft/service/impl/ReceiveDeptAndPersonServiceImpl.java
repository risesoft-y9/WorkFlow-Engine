package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ReceiveDepartment;
import net.risesoft.entity.ReceivePerson;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.repository.jpa.ReceiveDepartmentRepository;
import net.risesoft.repository.jpa.ReceivePersonRepository;
import net.risesoft.service.ReceiveDeptAndPersonService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "receiveDeptAndPersonService")
public class ReceiveDeptAndPersonServiceImpl implements ReceiveDeptAndPersonService {

    @Autowired
    private ReceiveDepartmentRepository receiveDepartmentRepository;

    @Autowired
    private ReceivePersonRepository receivePersonRepository;

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private PersonApi personManager;

    @Override
    public Integer countByDeptId(String deptId) {
        return receivePersonRepository.countByDeptId(deptId);
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delDepartment(String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "取消成功");
        try {
            ReceiveDepartment receiveDeptAndPerson = receiveDepartmentRepository.findByDeptId(id);
            setParentId(id, receiveDeptAndPerson.getParentId());
            receiveDepartmentRepository.delete(receiveDeptAndPerson);
            List<ReceivePerson> personList = receivePersonRepository.findByDeptId(id);
            for (ReceivePerson receivePerson : personList) {
                receivePersonRepository.delete(receivePerson);
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "取消失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delPerson(String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "删除成功");
        try {
            receivePersonRepository.deleteById(id);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        return map;
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
            Department dept = departmentManager.getDepartment(Y9LoginUserHolder.getTenantId(), deptId).getData();
            if (dept != null && dept.getId() != null) {
                list = getParentId(dept.getParentId(), list);
            } else {
                return list;
            }
        } else {
            list = new ArrayList<Object>();
            list.add(true);
            list.add(deptId);
            return list;
        }
        return list;
    }

    @Override
    public Map<String, Object> personList(String deptId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            List<ReceivePerson> personList = receivePersonRepository.findByDeptId(deptId);
            String tenantId = Y9LoginUserHolder.getTenantId();
            for (ReceivePerson receivePerson : personList) {
                Map<String, Object> m = new HashMap<String, Object>(16);
                Person person = personManager.getPerson(tenantId, receivePerson.getPersonId()).getData();
                if (person == null || person.getId() == null || person.getDisabled()) {
                    receivePersonRepository.delete(receivePerson);
                    continue;
                }
                m.put("id", receivePerson.getId());
                m.put("sex", person.getSex());
                m.put("userId", person.getId());
                m.put("parentId", receivePerson.getPersonDeptId());
                m.put("name", person.getName());
                m.put("duty", StringUtils.isBlank(person.getDuty()) ? "" : person.getDuty());
                m.put("send", receivePerson.isSend() ? "是" : "否");
                m.put("receive", receivePerson.isReceive() ? "是" : "否");
                list.add(m);
            }
            map.put("rows", list);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public ReceiveDepartment save(ReceiveDepartment receiveDepartment) {
        return receiveDepartmentRepository.save(receiveDepartment);
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveDepartment(String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "设置成功");
        try {
            ReceiveDepartment receiveDeptAndPerson = receiveDepartmentRepository.findByDeptId(id);
            if (receiveDeptAndPerson == null || receiveDeptAndPerson.getId() == null) {
                receiveDeptAndPerson = new ReceiveDepartment();
                receiveDeptAndPerson.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                Integer tabIndex = receiveDepartmentRepository.getMaxTabIndex();
                receiveDeptAndPerson.setTabIndex(tabIndex == null ? 0 : tabIndex + 1);
            }
            String tenantId = Y9LoginUserHolder.getTenantId();
            Department dept = departmentManager.getDepartment(tenantId, id).getData();
            OrgUnit orgUnit = departmentManager.getBureau(tenantId, id).getData();
            receiveDeptAndPerson.setBureauId(orgUnit.getId());
            receiveDeptAndPerson.setDeptName(dept.getName());
            receiveDeptAndPerson.setDeptId(id);
            receiveDeptAndPerson.setParentId(dept.getParentId());
            receiveDeptAndPerson.setCreateDate(new Date());
            receiveDepartmentRepository.save(receiveDeptAndPerson);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "设置失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveOrder(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            if (StringUtils.isNotBlank(ids)) {
                String[] idArr = ids.split(",");
                Integer tabIndex = 0;
                for (String guid : idArr) {
                    ReceiveDepartment receiveDeptAndPerson = receiveDepartmentRepository.findById(guid).orElse(null);
                    Department dept = departmentManager
                        .getDepartment(Y9LoginUserHolder.getTenantId(), receiveDeptAndPerson.getDeptId()).getData();
                    receiveDeptAndPerson.setDeptName(dept.getName());
                    receiveDeptAndPerson.setTabIndex(tabIndex);
                    tabIndex += 1;
                    receiveDepartmentRepository.save(receiveDeptAndPerson);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> savePerson(String deptId, String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "保存成功");
        try {
            String[] id = ids.split(",");
            String msg = "";
            String idTemp = "";
            String tenantId = Y9LoginUserHolder.getTenantId();
            Department dept = departmentManager.getDepartment(tenantId, deptId).getData();
            for (String userId : id) {
                Person user = personManager.getPerson(tenantId, userId).getData();
                List<ReceivePerson> list = receivePersonRepository.findByPersonId(userId);
                if (list != null && list.size() > 0) {
                    boolean isAdd = true;
                    for (ReceivePerson receivePerson : list) {
                        OrgUnit orgUnit = departmentManager
                            .getBureau(Y9LoginUserHolder.getTenantId(), receivePerson.getDeptId()).getData();
                        OrgUnit orgUnit1 =
                            departmentManager.getBureau(Y9LoginUserHolder.getTenantId(), deptId).getData();
                        // 委办局相同，且部门不相同则，不可添加该收文员
                        if (orgUnit.getId().equals(orgUnit1.getId()) && !receivePerson.getDeptId().equals(deptId)) {
                            isAdd = false;
                            // 同一个委办局，不能设置一个人为两个单位的收文员
                            Person person = personManager.getPerson(Y9LoginUserHolder.getTenantId(), userId).getData();
                            msg += "[" + person.getName() + "已是" + receivePerson.getDeptName() + "部门的收发员]<br>";
                        }
                    }
                    if (isAdd) {
                        idTemp = Y9Util.genCustomStr(idTemp, userId);
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
                    idTemp = Y9Util.genCustomStr(idTemp, userId);
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
            if (!"".equals(msg)) {
                map.put("msg", msg);
            }
            ReceiveDepartment receiveDepartment = receiveDepartmentRepository.findByDeptId(deptId);
            if (receiveDepartment != null && receiveDepartment.getId() != null) {
                // 修改部门父节点
                if (StringUtils.isNotBlank(idTemp)) {
                    setParentId(deptId, deptId);
                } else {
                    setParentId(deptId, receiveDepartment.getParentId());
                }
                receiveDepartment.setParentId("");
                List<Object> list = new ArrayList<Object>();
                list.add(false);
                list = getParentId(dept.getParentId(), list);
                if ((boolean)list.get(0)) {
                    receiveDepartment.setParentId((String)list.get(1));
                }
            } else {
                OrgUnit orgUnit = departmentManager.getBureau(tenantId, deptId).getData();
                receiveDepartment = new ReceiveDepartment();
                receiveDepartment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                receiveDepartment.setDeptName(dept.getName());
                receiveDepartment.setBureauId(orgUnit.getId());
                receiveDepartment.setDeptId(deptId);
                receiveDepartment.setCreateDate(new Date());
                // 修改部门父节点
                if (StringUtils.isNotBlank(idTemp)) {
                    setParentId(deptId, deptId);
                } else {
                    setParentId(deptId, receiveDepartment.getParentId());
                }
                receiveDepartment.setParentId("");
                List<Object> list = new ArrayList<Object>();
                list.add(false);
                list = getParentId(dept.getParentId(), list);
                if ((boolean)list.get(0)) {
                    receiveDepartment.setParentId((String)list.get(1));
                }
            }
            receiveDepartmentRepository.save(receiveDepartment);
        } catch (Exception e) {
            map.put("msg", "保存失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 往下设parentId
     *
     * @param deptId
     * @param parentId
     */
    public void setParentId(String deptId, String parentId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Department> list = departmentManager.listSubDepartments(tenantId, deptId).getData();
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
    public Map<String, Object> setReceive(boolean receive, String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
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
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> setSend(boolean send, String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
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
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

}
