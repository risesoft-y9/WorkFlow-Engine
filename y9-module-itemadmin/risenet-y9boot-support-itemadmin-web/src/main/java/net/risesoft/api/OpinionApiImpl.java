package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.OpinionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.Opinion;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.platform.Person;
import net.risesoft.service.ItemOpinionFrameBindService;
import net.risesoft.service.OpinionService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/opinion")
public class OpinionApiImpl implements OpinionApi {

    @Autowired
    private OpinionService opinionService;

    @Autowired
    private ItemOpinionFrameBindService itemOpinionFrameBindService;

    @Autowired
    private PersonApi personManager;

    @Override
    @GetMapping(value = "/checkSignOpinion", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean checkSignOpinion(String tenantId, String userId, String processSerialNumber, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Boolean checkSignOpinion = opinionService.checkSignOpinion(processSerialNumber, taskId);
        return checkSignOpinion;
    }

    @Override
    @GetMapping(value = "/countOpinionHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countOpinionHistory(String tenantId, String processSerialNumber, String opinionFrameMark) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return opinionService.countOpinionHistory(processSerialNumber, opinionFrameMark);
    }

    @Override
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(String tenantId, String userId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        opinionService.delete(id);
    }

    @Override
    @GetMapping(value = "/getBindOpinionFrame", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getBindOpinionFrame(String tenantId, String itemId, String processDefinitionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return itemOpinionFrameBindService.getBindOpinionFrame(itemId, processDefinitionId);
    }

    @Override
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    public OpinionModel getById(String tenantId, String userId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Opinion opinion = opinionService.findOne(id);
        OpinionModel opinionModel = ItemAdminModelConvertUtil.opinion2Model(opinion);
        return opinionModel;
    }

    @Override
    @GetMapping(value = "/getByTaskId", produces = MediaType.APPLICATION_JSON_VALUE)
    public OpinionModel getByTaskId(String tenantId, String userId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Opinion> opinion = opinionService.findByTaskIdAndUserIdAndProcessTrackIdIsNull(taskId, userId);
        OpinionModel opinionModel = new OpinionModel();
        if (opinion.size() > 0) {
            opinionModel = ItemAdminModelConvertUtil.opinion2Model(opinion.get(0));
        }
        return opinionModel;
    }

    @Override
    @GetMapping(value = "/opinionHistoryList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OpinionHistoryModel> opinionHistoryList(String tenantId, String processSerialNumber,
        String opinionFrameMark) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return opinionService.opinionHistoryList(processSerialNumber, opinionFrameMark);
    }

    @Override
    @GetMapping(value = "/personCommentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> personCommentList(String tenantId, String userId, String processSerialNumber,
        String taskId, String itembox, String opinionFrameMark, String itemId, String taskDefinitionKey,
        String activitiUser) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = opinionService.personCommentList(processSerialNumber, taskId, itembox,
            opinionFrameMark, itemId, taskDefinitionKey, activitiUser);
        return listMap;
    }

    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(String tenantId, String userId, @RequestBody OpinionModel opinionModel) throws Exception {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        Opinion opinion = ItemAdminModelConvertUtil.opinionModel2Opinion(opinionModel);
        opinionService.save(opinion);
    }

    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public OpinionModel saveOrUpdate(String tenantId, String userId, @RequestBody OpinionModel opinionModel)
        throws Exception {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        Opinion opinion = ItemAdminModelConvertUtil.opinionModel2Opinion(opinionModel);
        opinion = opinionService.saveOrUpdate(opinion);
        Y9BeanUtil.copyProperties(opinion, opinionModel);
        return opinionModel;
    }

}
