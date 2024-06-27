package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.Y9BusinessException;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processTrack", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessTrackApiImpl implements ProcessTrackApi {

    private final ProcessTrackService processTrackService;

    private final PersonApi personManager;

    @Override
    @PostMapping(value = "/deleteById")
    public Y9Result<Object> deleteById(String tenantId, String userId, String id) throws Exception {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        processTrackService.deleteById(id);
        return Y9Result.success();
    }

    @Override
    public Y9Result<List<ProcessTrackModel>> findByTaskId(String tenantId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ProcessTrackModel> items = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(taskId)) {
                List<ProcessTrack> list = processTrackService.findByTaskId(taskId);
                items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
            }
            return Y9Result.success(items);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Y9BusinessException(500, "获取自定义历程,错误信息为：" + e.getMessage());
        }

    }

    @Override
    public Y9Result<List<ProcessTrackModel>> findByTaskIdAsc(String tenantId, String userId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<ProcessTrackModel> items = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(taskId)) {
                List<ProcessTrack> list = processTrackService.findByTaskIdAsc(taskId);
                items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
            }
            return Y9Result.success(items);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Y9BusinessException(500, "获取自定义历程,错误信息为：" + e.getMessage());
        }

    }

    @Override
    public Y9Result<List<HistoryProcessModel>> processTrackList(String tenantId, String userId,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<HistoryProcessModel> items = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                items = processTrackService.getListMap(processInstanceId);
            }
            return Y9Result.success(items);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Y9BusinessException(500, "获取历程列表,错误信息为：" + e.getMessage());
        }

    }

    @Override
    @GetMapping(value = "/processTrackList4Simple")
    public Y9Result<List<HistoryProcessModel>> processTrackList4Simple(String tenantId, String userId,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<HistoryProcessModel> items = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                items = processTrackService.getListMap4Simple(processInstanceId);
            }
            return Y9Result.success(items);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Y9BusinessException(500, "获取历程列表,错误信息为：" + e.getMessage());
        }

    }

    @Override
    public Y9Result<ProcessTrackModel> saveOrUpdate(String tenantId, @RequestBody ProcessTrackModel processTrackModel)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessTrack processTrack = ItemAdminModelConvertUtil.processTrackModel2ProcessTrack(processTrackModel);
        ProcessTrack ptTemp = processTrackService.saveOrUpdate(processTrack);
        ProcessTrackModel ptModelTemp = ItemAdminModelConvertUtil.processTrack2Model(ptTemp);
        return Y9Result.success(ptModelTemp);
    }
}
