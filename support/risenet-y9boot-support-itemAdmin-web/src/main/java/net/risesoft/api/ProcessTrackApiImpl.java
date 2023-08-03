package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/processTrack")
public class ProcessTrackApiImpl implements ProcessTrackApi {

    @Autowired
    private ProcessTrackService processTrackService;

    @Autowired
    private PersonApi personManager;

    @Override
    @PostMapping(value = "/deleteById", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteById(String tenantId, String userId, String id) throws Exception {
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        processTrackService.deleteById(id);
    }

    @Override
    @GetMapping(value = "/findByTaskId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessTrackModel> findByTaskId(String tenantId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ProcessTrackModel> items = new ArrayList<ProcessTrackModel>();
        try {
            if (StringUtils.isNotBlank(taskId)) {
                List<ProcessTrack> list = processTrackService.findByTaskId(taskId);
                items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    @GetMapping(value = "/findByTaskIdAsc", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessTrackModel> findByTaskIdAsc(String tenantId, String userId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        List<ProcessTrackModel> items = new ArrayList<ProcessTrackModel>();
        try {
            if (StringUtils.isNotBlank(taskId)) {
                List<ProcessTrack> list = processTrackService.findByTaskIdAsc(taskId);
                items = ItemAdminModelConvertUtil.processTrackList2ModelList(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    @GetMapping(value = "/processTrackList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> processTrackList(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, false);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                items = processTrackService.getListMap(processInstanceId);
                retMap.put(UtilConsts.SUCCESS, true);
                retMap.put("rows", items);
            }
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    @GetMapping(value = "/processTrackList4Simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> processTrackList4Simple(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        retMap.put(UtilConsts.SUCCESS, false);
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        try {
            if (StringUtils.isNotBlank(processInstanceId)) {
                items = processTrackService.getListMap4Simple(processInstanceId);
                retMap.put(UtilConsts.SUCCESS, true);
                retMap.put("rows", items);
            }
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProcessTrackModel saveOrUpdate(String tenantId, @RequestBody ProcessTrackModel processTrackModel)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        ProcessTrack processTrack = ItemAdminModelConvertUtil.processTrackModel2ProcessTrack(processTrackModel);
        ProcessTrack ptTemp = processTrackService.saveOrUpdate(processTrack);
        ProcessTrackModel ptModelTemp = ItemAdminModelConvertUtil.processTrack2Model(ptTemp);
        return ptModelTemp;
    }
}
