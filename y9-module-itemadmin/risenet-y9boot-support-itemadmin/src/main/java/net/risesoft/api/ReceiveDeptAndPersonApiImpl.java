package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ReceiveDeptAndPersonApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.receive.ReceiveDepartment;
import net.risesoft.entity.receive.ReceivePerson;
import net.risesoft.model.itemadmin.ReceiveOrgUnit;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
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
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/receiveDeptAndPerson", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReceiveDeptAndPersonApiImpl implements ReceiveDeptAndPersonApi {

    private final DepartmentApi departmentApi;

    private final OrgUnitApi orgUnitApi;

    private final ReceivePersonRepository receivePersonRepository;

    private final ReceiveDepartmentRepository receiveDepartmentRepository;

    /**
     * 根据单位名称模糊查询收发单位
     *
     * @param tenantId 租户id
     * @param name 单位名称
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ReceiveOrgUnit>> findByDeptNameLike(@RequestParam String tenantId, @RequestParam String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ReceiveOrgUnit> listMap = new ArrayList<>();
        if (StringUtils.isBlank(name)) {
            name = "";
        }
        name = "%" + name + "%";
        List<ReceiveDepartment> list = receiveDepartmentRepository.findByDeptNameLikeOrderByTabIndex(name);
        for (ReceiveDepartment receiveDepartment : list) {
            Department department = departmentApi.get(tenantId, receiveDepartment.getDeptId()).getData();
            if (department == null || department.getId() == null) {
                continue;
            }
            ReceiveOrgUnit orgUnit = new ReceiveOrgUnit();
            orgUnit.setId(receiveDepartment.getDeptId());
            orgUnit.setDisabled(department.getDisabled());
            orgUnit.setParentId(receiveDepartment.getParentId());
            orgUnit.setName(department.getName());
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, department.getId()).getData();
            if (bureau != null && bureau.getId() != null && !bureau.getId().equals(department.getId())) {
                orgUnit.setNameWithBureau(department.getName() + "(" + bureau.getName() + ")");
            }
            orgUnit.setOrgType("Department");
            listMap.add(orgUnit);
        }
        return Y9Result.success(listMap);
    }

    /**
     * 获取所有收发单位
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ReceiveOrgUnit>> getReceiveDeptTree(@RequestParam String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ReceiveOrgUnit> listMap = new ArrayList<>();
        List<ReceiveDepartment> list = receiveDepartmentRepository.findAll();
        for (ReceiveDepartment receiveDepartment : list) {
            Department department = departmentApi.get(tenantId, receiveDepartment.getDeptId()).getData();
            if (department == null || department.getId() == null) {
                continue;
            }
            ReceiveOrgUnit orgUnit = new ReceiveOrgUnit();
            orgUnit.setId(receiveDepartment.getDeptId());
            orgUnit.setDisabled(department.getDisabled());
            orgUnit.setParentId(receiveDepartment.getParentId());
            orgUnit.setName(department.getName());
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, department.getId()).getData();
            if (bureau != null && bureau.getId() != null && !bureau.getId().equals(department.getId())) {
                orgUnit.setNameWithBureau(department.getName() + "(" + bureau.getName() + ")");
            }
            orgUnit.setOrgType("Department");
            listMap.add(orgUnit);
        }
        return Y9Result.success(listMap);
    }

    /**
     * 获取所有收发单位、子收发单位（可根据单位名称模糊查询）
     *
     * @param tenantId 租户id
     * @param orgUnitId 单位Id
     * @param name 名称
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ReceiveOrgUnit>> getReceiveDeptTreeById(@RequestParam String tenantId,
        @RequestParam String orgUnitId, String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ReceiveOrgUnit> listMap = new ArrayList<>();
        List<ReceiveDepartment> list;
        if (StringUtils.isNotBlank(name)) {
            list = receiveDepartmentRepository.findByDeptNameContainingOrderByTabIndex(name);
            for (ReceiveDepartment receiveDepartment : list) {
                Department department = departmentApi.get(tenantId, receiveDepartment.getDeptId()).getData();
                if (department == null || department.getId() == null) {
                    continue;
                }
                ReceiveOrgUnit orgUnit = new ReceiveOrgUnit();
                orgUnit.setId(receiveDepartment.getDeptId());
                orgUnit.setDisabled(department.getDisabled());
                orgUnit.setParentId(receiveDepartment.getParentId());
                orgUnit.setName(department.getName());
                OrgUnit bureau = orgUnitApi.getBureau(tenantId, department.getId()).getData();
                if (bureau != null && bureau.getId() != null && !bureau.getId().equals(department.getId())) {
                    orgUnit.setNameWithBureau(department.getName() + "(" + bureau.getName() + ")");
                }
                Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                orgUnit.setIsParent(count > 0);
                orgUnit.setOrgType("Department");
                if (listMap.contains(orgUnit)) {
                    continue;// 去重
                }
                listMap.add(orgUnit);
            }
        } else {
            if (StringUtils.isBlank(orgUnitId)) {
                list = receiveDepartmentRepository.findAll();
                for (ReceiveDepartment receiveDepartment : list) {
                    Department department = departmentApi.get(tenantId, receiveDepartment.getDeptId()).getData();
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    ReceiveOrgUnit orgUnit = new ReceiveOrgUnit();
                    orgUnit.setId(receiveDepartment.getDeptId());
                    orgUnit.setDisabled(department.getDisabled());
                    orgUnit.setParentId(receiveDepartment.getParentId());
                    orgUnit.setName(department.getName());
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    orgUnit.setIsParent(count > 0);
                    orgUnit.setOrgType("Department");
                    if (listMap.contains(orgUnit)) {
                        continue;// 去重
                    }
                    listMap.add(orgUnit);
                }
            } else {
                list = receiveDepartmentRepository.findByParentIdOrderByTabIndex(orgUnitId);
                for (ReceiveDepartment receiveDepartment : list) {
                    Department department = departmentApi.get(tenantId, receiveDepartment.getDeptId()).getData();
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    ReceiveOrgUnit orgUnit = new ReceiveOrgUnit();
                    orgUnit.setId(receiveDepartment.getDeptId());
                    orgUnit.setDisabled(department.getDisabled());
                    orgUnit.setParentId(orgUnitId);
                    orgUnit.setName(department.getName());
                    orgUnit.setIsParent(count > 0);
                    orgUnit.setOrgType("Department");
                    if (listMap.contains(orgUnit)) {
                        continue;// 去重
                    }
                    listMap.add(orgUnit);
                }
            }
        }
        return Y9Result.success(listMap);
    }

    /**
     * 根据收发单位id获取单位下未禁用的人员集合
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<OrgUnit>> getSendReceiveByDeptId(@RequestParam String tenantId, @RequestParam String deptId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ReceivePerson> list = receivePersonRepository.findByDeptId(deptId);
        List<OrgUnit> users = new ArrayList<>();
        for (ReceivePerson receivePerson : list) {
            OrgUnit person = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, receivePerson.getPersonId()).getData();
            if (person != null && StringUtils.isNotBlank(person.getId())
                && !Boolean.TRUE.equals(person.getDisabled())) {
                users.add(person);
            }
        }
        return Y9Result.success(users);
    }

    /**
     * 根据组织id获取对应的收发单位
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<ReceiveOrgUnit>>} 通用请求返回对象 - data 是收发单位集合
     * @since 9.6.6
     */
    @Override
    @GetMapping(value = "/getSendReceiveByUserId")
    public Y9Result<List<ReceiveOrgUnit>> getSendReceiveByUserId(@RequestParam String tenantId,
        @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ReceiveOrgUnit> listMap = new ArrayList<>();
        if (StringUtils.isBlank(orgUnitId)) {
            orgUnitId = "";
        }
        orgUnitId = "%" + orgUnitId + "%";
        List<ReceivePerson> list = receivePersonRepository.findByPersonId(orgUnitId);
        if (!list.isEmpty()) {
            for (ReceivePerson receivePerson : list) {
                Department department = departmentApi.get(tenantId, receivePerson.getDeptId()).getData();
                if (department == null || department.getId() == null) {
                    continue;
                }
                ReceiveOrgUnit orgUnit = new ReceiveOrgUnit();
                orgUnit.setId(receivePerson.getDeptId());
                orgUnit.setName(department.getName());
                listMap.add(orgUnit);
            }
        }
        return Y9Result.success(listMap);
    }
}
