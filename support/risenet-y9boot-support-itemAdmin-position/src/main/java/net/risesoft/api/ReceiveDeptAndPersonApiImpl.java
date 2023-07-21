package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ReceiveDeptAndPersonApi;
import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.ReceiveDepartment;
import net.risesoft.entity.ReceivePerson;
import net.risesoft.model.Department;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.repository.jpa.ReceiveDepartmentRepository;
import net.risesoft.repository.jpa.ReceivePersonRepository;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 收发单位接口
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/receiveDeptAndPerson")
public class ReceiveDeptAndPersonApiImpl implements ReceiveDeptAndPersonApi {

    @Autowired
    private PersonApi personManager;

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private ReceivePersonRepository receivePersonRepository;

    @Autowired
    private ReceiveDepartmentRepository receiveDepartmentRepository;

    /**
     * 根据name模糊搜索收发单位
     *
     * @param tenantId 租户id
     * @param name 搜索名称
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/findByDeptNameLike", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findByDeptNameLike(String tenantId, String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(name)) {
            name = "";
        }
        name = "%" + name + "%";
        List<ReceiveDepartment> list = receiveDepartmentRepository.findByDeptNameLikeOrderByTabIndex(name);
        for (ReceiveDepartment receiveDepartment : list) {
            Map<String, Object> data = new HashMap<String, Object>(16);
            Department department = departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId());
            if (department == null || department.getId() == null) {
                continue;
            }
            data.put("id", receiveDepartment.getDeptId());
            data.put("parentId", receiveDepartment.getParentId());
            data.put("name", department.getName());
            OrgUnit bureau = departmentManager.getBureau(tenantId, department.getId());
            if (bureau != null && bureau.getId() != null && !bureau.getId().equals(department.getId())) {
                data.put("name", department.getName() + "(" + bureau.getName() + ")");
            }
            listMap.add(data);
        }
        return listMap;
    }

    /**
     * 获取所有收发单位
     *
     * @param tenantId 租户id
     * @return List<Map<String, Object>>
     */
    @Override
    @GetMapping(value = "/getReceiveDeptTree", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getReceiveDeptTree(String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        List<ReceiveDepartment> list = receiveDepartmentRepository.findAll();
        for (ReceiveDepartment receiveDepartment : list) {
            Map<String, Object> data = new HashMap<String, Object>(16);
            data.put("id", receiveDepartment.getDeptId());
            Department department = departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId());
            if (department == null || department.getId() == null) {
                continue;
            }
            data.put("parentId", receiveDepartment.getParentId());
            data.put("name", department.getName());
            OrgUnit bureau = departmentManager.getBureau(tenantId, department.getId());
            if (bureau != null && bureau.getId() != null && !bureau.getId().equals(department.getId())) {
                data.put("name", department.getName() + "(" + bureau.getName() + ")");
            }
            listMap.add(data);
        }
        return listMap;
    }

    /**
     * 获取所有收发单位
     *
     * @param tenantId 租户id
     * @param orgUnitId 单位Id
     * @param name 名称
     * @return List<Map<String, Object>>
     */
    @Override
    @GetMapping(value = "/getReceiveDeptTreeById", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getReceiveDeptTreeById(String tenantId, String orgUnitId, String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        List<ReceiveDepartment> list = null;
        if (StringUtils.isNotBlank(name)) {
            list = receiveDepartmentRepository.findByDeptNameContainingOrderByTabIndex(name);
            for (ReceiveDepartment receiveDepartment : list) {
                Map<String, Object> data = new HashMap<String, Object>(16);
                data.put("id", receiveDepartment.getDeptId());
                Department department = departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId());
                if (department == null || department.getId() == null) {
                    continue;
                }
                data.put("parentID", receiveDepartment.getParentId());
                data.put("name", department.getName());
                OrgUnit bureau = departmentManager.getBureau(tenantId, department.getId());
                if (bureau != null && bureau.getId() != null && !bureau.getId().equals(department.getId())) {
                    data.put("name", department.getName() + "(" + bureau.getName() + ")");
                }
                data.put("isPerm", true);
                Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                data.put("isParent", count > 0 ? true : false);
                data.put("orgType", "Department");
                if (listMap.contains(data)) {
                    continue;// 去重
                }
                listMap.add(data);
            }
        } else {
            if (StringUtils.isBlank(orgUnitId)) {
                list = receiveDepartmentRepository.findAll();
                for (ReceiveDepartment receiveDepartment : list) {
                    Map<String, Object> data = new HashMap<String, Object>(16);
                    data.put("id", receiveDepartment.getDeptId());
                    Department department = departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId());
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    data.put("parentID", receiveDepartment.getParentId());
                    data.put("name", department.getName());
                    data.put("isPerm", true);
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    data.put("isParent", count > 0 ? true : false);
                    data.put("orgType", "Department");
                    if (listMap.contains(data)) {
                        continue;// 去重
                    }
                    listMap.add(data);
                }
            } else {
                list = receiveDepartmentRepository.findByParentIdOrderByTabIndex(orgUnitId);
                for (ReceiveDepartment receiveDepartment : list) {
                    Map<String, Object> data = new HashMap<String, Object>(16);
                    data.put("id", receiveDepartment.getDeptId());
                    Department department = departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId());
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    data.put("parentID", orgUnitId);
                    data.put("name", department.getName());
                    data.put("isPerm", true);
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    data.put("isParent", count > 0 ? true : false);
                    data.put("orgType", "Department");
                    if (listMap.contains(data)) {
                        continue;// 去重
                    }
                    listMap.add(data);
                }
            }
        }
        return listMap;
    }

    /**
     * 根据收发单位id,获取人员集合
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @return List<Person>
     */
    @Override
    @GetMapping(value = "/getSendReceiveByDeptId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> getSendReceiveByDeptId(String tenantId, String deptId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ReceivePerson> list = receivePersonRepository.findByDeptId(deptId);
        List<Person> users = new ArrayList<Person>();
        for (ReceivePerson receivePerson : list) {
            Person person = personManager.getPerson(tenantId, receivePerson.getPersonId());
            if (person != null && StringUtils.isNotBlank(person.getId()) && !person.getDisabled()) {
                users.add(person);
            }
        }
        return users;
    }

    /**
     * 根据人员id,获取对应的收发单位
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return List<Map<String, Object>>
     */
    @Override
    @GetMapping(value = "/getSendReceiveByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getSendReceiveByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        Y9LoginUserHolder.setPerson(person);
        if (StringUtils.isBlank(userId)) {
            userId = "";
        }
        userId = "%" + userId + "%";
        List<ReceivePerson> list = receivePersonRepository.findByPersonId(userId);
        if (list.size() > 0) {
            for (ReceivePerson receivePerson : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                Department department = departmentManager.getDepartment(tenantId, receivePerson.getDeptId());
                if (department == null || department.getId() == null) {
                    continue;
                }
                map.put("deptId", receivePerson.getDeptId());
                map.put("deptName", department != null ? department.getName() : receivePerson.getDeptName() + "(该部门已不存在)");
                listMap.add(map);
            }
        }
        return listMap;
    }
}
