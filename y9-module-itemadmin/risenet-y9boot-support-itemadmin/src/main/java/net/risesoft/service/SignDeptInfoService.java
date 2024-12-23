package net.risesoft.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.SignDeptInfo;
import net.risesoft.entity.SignOutDept;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SignDeptInfoService {

    void deleteById(String id);

    List<SignDeptInfo> getSignDeptList(String processSerialNumber, String deptType);

    /**
     * 获取委外会签部门列表
     *
     * @param name
     * @param page
     * @param rows
     * @return
     */
    Page<SignOutDept> listAll(String name, Integer page, Integer rows);

    /**
     * 删除委外会签部门
     *
     * @param ids
     */
    void remove(String[] ids);

    /**
     * 保存或更新委外会签部门
     *
     * @param info
     * @return Y9Result<String>
     */
    void saveOrUpdate(SignOutDept info);

    void saveSignDept(String processSerialNumber, String deptType, String deptIds);

    void saveSignDeptInfo(String id, String userName);
}
