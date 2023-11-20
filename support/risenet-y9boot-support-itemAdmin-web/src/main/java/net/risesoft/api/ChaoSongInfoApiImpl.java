package net.risesoft.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.nosql.elastic.entity.ChaoSongInfo;
import net.risesoft.service.ChaoSongInfoService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/chaoSongInfo")
public class ChaoSongInfoApiImpl implements ChaoSongInfoApi {

    @Autowired
    private ChaoSongInfoService chaoSongInfoService;

    @Autowired
    private PersonApi personManager;

    @Override
    @PostMapping(value = "/changeChaoSongState", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeChaoSongState(String tenantId, String id, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.changeChaoSongState(id, type);
    }

    @Override
    @PostMapping(value = "/changeStatus", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changeStatus(String tenantId, String userId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.changeStatus(ids);
    }

    @Override
    @PostMapping(value = "/changeStatus2read", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeStatus2read(String tenantId, String chaoSongId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.changeStatus(chaoSongId);
    }

    @Override
    @GetMapping(value = "/countByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByProcessInstanceId(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.countByProcessInstanceId(userId, processInstanceId);
    }

    @Override
    @GetMapping(value = "/countByUserIdAndProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByUserIdAndProcessInstanceId(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.countByUserIdAndProcessInstanceId(userId, processInstanceId);
    }

    @Override
    @PostMapping(value = "/deleteByIds", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteByIds(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.deleteByIds(ids);
    }

    @Override
    @PostMapping(value = "/deleteByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.deleteByProcessInstanceId(processInstanceId);
    }

    @Override
    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> detail(String tenantId, String userId, String id, String processInstanceId,
        Integer status, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = chaoSongInfoService.detail(processInstanceId, status, mobile);
        map.put("id", id);
        map.put("status", status);
        ChaoSongInfo chaoSong = chaoSongInfoService.findOne(id);
        // 点开即设为已阅
        if (null != chaoSong && chaoSong.getStatus() != 1) {
            chaoSongInfoService.changeStatus(id);
        }
        return map;
    }

    @Override
    @GetMapping(value = "/getDone4OpinionCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDone4OpinionCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.getDone4OpinionCountByUserId(userId);
    }

    @Override
    @GetMapping(value = "/getDoneCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getDoneCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = chaoSongInfoService.getDoneCountByUserId(userId);
        return count;
    }

    @Override
    @GetMapping(value = "/getDoneListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getDoneListByUserId(String tenantId, String userId, String documentTitle, int rows,
        int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongInfoService.getDoneListByUserId(userId, documentTitle, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getListByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByProcessInstanceId(String tenantId, String processInstanceId, String userName,
        int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map =
            chaoSongInfoService.getListByProcessInstanceId(processInstanceId, userName, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getListBySenderIdAndProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListBySenderIdAndProcessInstanceId(String tenantId, String senderId,
        String processInstanceId, String userName, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongInfoService.getListBySenderIdAndProcessInstanceId(senderId, processInstanceId,
            userName, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getOpinionChaosongByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getOpinionChaosongByUserId(String tenantId, String userId, String documentTitle,
        int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongInfoService.getOpinionChaosongByUserId(userId, documentTitle, rows, page);
        return map;
    }

    @Override
    @GetMapping(value = "/getTodoCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int getTodoCountByUserId(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = chaoSongInfoService.getTodoCountByUserId(userId);
        return count;
    }

    @Override
    @GetMapping(value = "/getTodoListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getTodoListByUserId(String tenantId, String userId, String documentTitle, int rows,
        int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = chaoSongInfoService.getTodoListByUserId(userId, documentTitle, rows, page);
        return map;
    }

    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> save(String tenantId, String userId, String processInstanceId, String users,
        String isSendSms, String isShuMing, String smsContent, String smsPersonId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map =
            chaoSongInfoService.save(processInstanceId, users, isSendSms, isShuMing, smsContent, smsPersonId);
        return map;
    }

    @Override
    @GetMapping(value = "/searchAllByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchAllByUserId(String tenantId, String userId, String searchName, String itemId,
        String userName, String state, String year, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map =
            chaoSongInfoService.searchAllByUserId(searchName, itemId, userName, state, year, page, rows);
        return map;
    }

    @Override
    @GetMapping(value = "/searchAllList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchAllList(String tenantId, String searchName, String itemId, String senderName,
        String userName, String state, String year, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map =
            chaoSongInfoService.searchAllList(searchName, itemId, senderName, userName, state, year, page, rows);
        return map;
    }

    @Override
    @PostMapping(value = "/updateTitle", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTitle(String tenantId, String processInstanceId, String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.updateTitle(processInstanceId, documentTitle);
    }
}
