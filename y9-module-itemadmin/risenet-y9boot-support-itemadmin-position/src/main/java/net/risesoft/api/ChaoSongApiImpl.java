package net.risesoft.api;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.ChaoSong;
import net.risesoft.model.platform.Person;
import net.risesoft.service.ChaoSongService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 抄送接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Deprecated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/chaoSong")
public class ChaoSongApiImpl implements ChaoSongApi {

    private final ChaoSongService chaoSongService;

    private final PersonApi personManager;

    /**
     * 改变抄送状态
     *
     * @param tenantId 租户id
     * @param id 唯一标示
     * @param type 类型
     */
    @Override
    @PostMapping(value = "/changeChaoSongState", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeChaoSongState(String tenantId, String id, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.changeChaoSongState(id, type);
    }

    /**
     * 改变抄送状态（批量）
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param ids 唯一标示数组
     */
    @Override
    @PostMapping(value = "/changeStatus", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changeStatus(String tenantId, String userId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.changeStatus(ids, 1);
    }

    /**
     * 设置抄送为已阅
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     */
    @Override
    @PostMapping(value = "/changeStatus2read", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeStatus2read(String tenantId, String chaoSongId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.changeStatus(chaoSongId, 1);
    }

    /**
     * 获取抄送数量（根据流程实例id）
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @return int
     */
    @Override
    @GetMapping(value = "/countByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByProcessInstanceId(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.countByProcessInstanceId(userId, processInstanceId);
    }

    /**
     * 获取抄送数量（根据用户id和流程实例id）
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @return
     */
    @Override
    @GetMapping(value = "/countByUserIdAndProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByUserIdAndProcessInstanceId(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.countByUserIdAndProcessInstanceId(userId, processInstanceId);
    }

    /**
     * 删除抄送记录（根据流程实例id）
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return
     */
    @Override
    @PostMapping(value = "/deleteByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteByProcessInstanceId(String tenantId, String processInstanceId, String year) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.deleteByProcessInstanceId(processInstanceId, year);
    }

    /**
     * 删除抄送记录
     *
     * @param tenantId 租户id
     * @param ids ids
     * @param processInstanceId 流程实例id
     */
    @Override
    @PostMapping(value = "/deleteList", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteList(String tenantId, @RequestBody String[] ids, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.deleteList(ids, processInstanceId);
    }

    /**
     * 获取抄送详情
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param id id
     * @param processInstanceId 流程实例id
     * @param status 状态
     * @param mobile 是否发送手机
     * @return
     */
    @Override
    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> detail(String tenantId, String userId, String id, String processInstanceId, Integer status, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = chaoSongService.detail(processInstanceId, status, mobile);
        map.put("id", id);
        map.put("status", status);
        ChaoSong chaoSong = chaoSongService.findOne(id);
        /*
         * 点开即设为已阅
         */
        if (null != chaoSong && chaoSong.getStatus() != 1) {
            chaoSongService.changeStatus(id, 1);
        }
        return map;
    }

    /**
     * 获取批阅件计数
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return
     */
    @Override
    @GetMapping(value = "/getDone4OpinionCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDone4OpinionCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getDone4OpinionCountByUserId(userId);
    }

    /**
     * 根据人员唯一标示查找已阅数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return
     */
    @Override
    @GetMapping(value = "/getDoneCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDoneCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getDoneCountByUserId(userId);
    }

    /**
     * 根据人员唯一标示查找已阅数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @return
     */
    @Override
    @GetMapping(value = "/getDoneCountByUserIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDoneCountByUserIdAndItemId(String tenantId, String userId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getDoneCountByUserIdAndItemId(userId, itemId);
    }

    /**
     * 根据人员唯一标示和系统名称查找已阅数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return
     */
    @Override
    @GetMapping(value = "/getDoneCountByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDoneCountByUserIdAndSystemName(String tenantId, String userId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getDoneCountByUserIdAndSystemName(userId, systemName);
    }

    /**
     * 获取已阅列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param year 年份
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return
     */
    @Override
    @GetMapping(value = "/getDoneListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDoneListByUserId(String tenantId, String userId, String year, String documentTitle, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getDoneListByUserId(userId, year, documentTitle, rows, page);
    }

    /**
     * 查找抄送目标所有的抄送已阅件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @param rows 条数
     * @param page 页数
     * @return
     */
    @Override
    @GetMapping(value = "/getDoneListByUserIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDoneListByUserIdAndItemId(String tenantId, String userId, String itemId, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getDoneListByUserIdAndItemId(userId, itemId, rows, page);
    }

    /**
     * 根据人员唯一标示和系统名称查找已阅数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param rows rows
     * @param page page
     * @return
     */
    @Override
    @GetMapping(value = "/getDoneListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDoneListByUserIdAndSystemName(String tenantId, String userId, String systemName, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getDoneListByUserIdAndSystemName(userId, systemName, rows, page);
    }

    /**
     * 根据流程实例id获取抄送列表
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return
     */
    @Override
    @GetMapping(value = "/getListByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByProcessInstanceId(String tenantId, String processInstanceId, String userName, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getListByProcessInstanceId(processInstanceId, userName, rows, page);
    }

    /**
     * 根据发送人id和流程实例id获取抄送列表
     *
     * @param tenantId 租户id
     * @param senderId 发送人id
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return
     */
    @Override
    @GetMapping(value = "/getListBySenderIdAndProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListBySenderIdAndProcessInstanceId(String tenantId, String senderId, String processInstanceId, String userName, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getListBySenderIdAndProcessInstanceId(senderId, processInstanceId, userName, rows, page);
    }

    /**
     * 批阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param year 年份
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return
     */
    @Override
    @GetMapping(value = "/getOpinionChaosongByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getOpinionChaosongByUserId(String tenantId, String userId, String year, String documentTitle, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getOpinionChaosongByUserId(userId, year, documentTitle, rows, page);
    }

    /**
     * 根据人员唯一标示查找待阅数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return
     */
    @Override
    @GetMapping(value = "/getTodoCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getTodoCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getTodoCountByUserId(userId);
    }

    /**
     * 根据人员唯一标示查找待阅数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @return
     */
    @Override
    @GetMapping(value = "/getTodoCountByUserIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getTodoCountByUserIdAndItemId(String tenantId, String userId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getTodoCountByUserIdAndItemId(userId, itemId);
    }

    /**
     * 根据人员唯一标示查找待阅数量
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return
     */
    @Override
    @GetMapping(value = "/getTodoCountByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getTodoCountByUserIdAndSystemName(String tenantId, String userId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getTodoCountByUserIdAndSystemName(userId, systemName);
    }

    /**
     * 查找抄送目标所有的抄送待阅件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return
     */
    @Override
    @GetMapping(value = "/getTodoListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTodoListByUserId(String tenantId, String userId, String documentTitle, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getTodoListByUserId(userId, documentTitle, rows, page);
    }

    /**
     * 根据人员唯一标示和事项id查找待阅列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @param rows rows
     * @param page page
     * @return
     */
    @Override
    @GetMapping(value = "/getTodoListByUserIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTodoListByUserIdAndItemId(String tenantId, String userId, String itemId, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getTodoListByUserIdAndItemId(userId, itemId, rows, page);
    }

    /**
     * 根据人员唯一标示和系统名称查找待阅
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param title 标题
     * @param rows rows
     * @param page page
     * @return
     */
    @Override
    @GetMapping(value = "/getTodoListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTodoListByUserIdAndSystemName(String tenantId, String userId, String systemName, String title, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map;
        if (StringUtils.isNotBlank(title)) {
            map = chaoSongService.getTodoListByUserIdAndSystemNameAndTitle(userId, systemName, title, rows, page);
        } else {
            map = chaoSongService.getTodoListByUserIdAndSystemName(userId, systemName, rows, page);
        }
        return map;
    }

    /**
     * 保存抄送
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @param users users
     * @param isSendSms 是否发生短信
     * @param isShuMing isShuMing
     * @param smsContent 短信内容
     * @param smsPersonId 发送人
     * @return
     */
    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> save(String tenantId, String userId, String processInstanceId, String users, String isSendSms, String isShuMing, String smsContent, String smsPersonId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        return chaoSongService.save(processInstanceId, users, isSendSms, isShuMing, smsContent, smsPersonId);
    }

    /**
     * 更新抄送标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     */
    @Override
    @PostMapping(value = "/updateTitle", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTitle(String tenantId, String processInstanceId, String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.updateTitle(processInstanceId, documentTitle);
    }
}
