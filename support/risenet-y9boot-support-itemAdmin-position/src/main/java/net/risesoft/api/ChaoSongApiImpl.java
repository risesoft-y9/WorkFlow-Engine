package net.risesoft.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.entity.ChaoSong;
import net.risesoft.model.Person;
import net.risesoft.service.ChaoSongService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/chaoSong")
public class ChaoSongApiImpl implements ChaoSongApi {

    @Autowired
    private ChaoSongService chaoSongService;

    @Autowired
    private PersonApi personManager;

    @Override
    @PostMapping(value = "/changeChaoSongState", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeChaoSongState(String tenantId, String id, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.changeChaoSongState(id, type);
    }

    @Override
    @PostMapping(value = "/changeStatus", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changeStatus(String tenantId, String userId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.changeStatus(ids, 1);
    }

    @Override
    @PostMapping(value = "/changeStatus2read", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeStatus2read(String tenantId, String chaoSongId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.changeStatus(chaoSongId, 1);
    }

    @Override
    @GetMapping(value = "/countByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByProcessInstanceId(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.countByProcessInstanceId(userId, processInstanceId);
    }

    @Override
    @GetMapping(value = "/countByUserIdAndProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByUserIdAndProcessInstanceId(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.countByUserIdAndProcessInstanceId(userId, processInstanceId);
    }

    @Override
    @PostMapping(value = "/deleteByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteByProcessInstanceId(String tenantId, String processInstanceId, String year) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.deleteByProcessInstanceId(processInstanceId, year);
    }

    @Override
    @PostMapping(value = "/deleteList", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteList(String tenantId, @RequestBody String[] ids, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.deleteList(ids, processInstanceId);
    }

    @Override
    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> detail(String tenantId, String userId, String id, String processInstanceId,
        Integer status, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = chaoSongService.detail(processInstanceId, status, mobile);
        map.put("id", id);
        map.put("status", status);
        ChaoSong chaoSong = chaoSongService.findOne(id);
        /**
         * 点开即设为已阅
         */
        if (null != chaoSong && chaoSong.getStatus() != 1) {
            chaoSongService.changeStatus(id, 1);
        }
        return map;
    }

    @Override
    @GetMapping(value = "/getDone4OpinionCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDone4OpinionCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getDone4OpinionCountByUserId(userId);
    }

    @Override
    @GetMapping(value = "/getDoneCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDoneCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = chaoSongService.getDoneCountByUserId(userId);
        return count;
    }

    @Override
    @GetMapping(value = "/getDoneCountByUserIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDoneCountByUserIdAndItemId(String tenantId, String userId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = chaoSongService.getDoneCountByUserIdAndItemId(userId, itemId);
        return count;
    }

    @Override
    @GetMapping(value = "/getDoneCountByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDoneCountByUserIdAndSystemName(String tenantId, String userId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = chaoSongService.getDoneCountByUserIdAndSystemName(userId, systemName);
        return count;
    }

    @Override
    @GetMapping(value = "/getDoneListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDoneListByUserId(String tenantId, String userId, String year, String documentTitle,
        int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongService.getDoneListByUserId(userId, year, documentTitle, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getDoneListByUserIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDoneListByUserIdAndItemId(String tenantId, String userId, String itemId, int rows,
        int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongService.getDoneListByUserIdAndItemId(userId, itemId, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getDoneListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDoneListByUserIdAndSystemName(String tenantId, String userId, String systemName,
        int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongService.getDoneListByUserIdAndSystemName(userId, systemName, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getListByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByProcessInstanceId(String tenantId, String processInstanceId, String userName,
        int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongService.getListByProcessInstanceId(processInstanceId, userName, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getListBySenderIdAndProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListBySenderIdAndProcessInstanceId(String tenantId, String senderId,
        String processInstanceId, String userName, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map =
            chaoSongService.getListBySenderIdAndProcessInstanceId(senderId, processInstanceId, userName, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getOpinionChaosongByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getOpinionChaosongByUserId(String tenantId, String userId, String year,
        String documentTitle, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongService.getOpinionChaosongByUserId(userId, year, documentTitle, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getTodoCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getTodoCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = chaoSongService.getTodoCountByUserId(userId);
        return count;
    }

    @Override
    @GetMapping(value = "/getTodoCountByUserIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getTodoCountByUserIdAndItemId(String tenantId, String userId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = chaoSongService.getTodoCountByUserIdAndItemId(userId, itemId);
        return count;
    }

    @Override
    @GetMapping(value = "/getTodoCountByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getTodoCountByUserIdAndSystemName(String tenantId, String userId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongService.getTodoCountByUserIdAndSystemName(userId, systemName);
    }

    @Override
    @GetMapping(value = "/getTodoListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTodoListByUserId(String tenantId, String userId, String documentTitle, int rows,
        int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongService.getTodoListByUserId(userId, documentTitle, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getTodoListByUserIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTodoListByUserIdAndItemId(String tenantId, String userId, String itemId, int rows,
        int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongService.getTodoListByUserIdAndItemId(userId, itemId, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getTodoListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTodoListByUserIdAndSystemName(String tenantId, String userId, String systemName,
        String title, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        if (StringUtils.isNotBlank(title)) {
            map = chaoSongService.getTodoListByUserIdAndSystemNameAndTitle(userId, systemName, title, rows, page);
        } else {
            map = chaoSongService.getTodoListByUserIdAndSystemName(userId, systemName, rows, page);
        }
        return map;
    }

    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> save(String tenantId, String userId, String processInstanceId, String users,
        String isSendSms, String isShuMing, String smsContent, String smsPersonId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map =
            chaoSongService.save(processInstanceId, users, isSendSms, isShuMing, smsContent, smsPersonId);
        return map;
    }

    @Override
    @PostMapping(value = "/updateTitle", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTitle(String tenantId, String processInstanceId, String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongService.updateTitle(processInstanceId, documentTitle);
    }
}
