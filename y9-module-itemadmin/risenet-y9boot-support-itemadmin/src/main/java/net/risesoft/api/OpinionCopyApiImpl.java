package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.opinion.OpinionCopyApi;
import net.risesoft.entity.DocumentCopy;
import net.risesoft.entity.opinion.OpinionCopy;
import net.risesoft.enums.DocumentCopyStatusEnum;
import net.risesoft.model.itemadmin.OpinionCopyModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DocumentCopyService;
import net.risesoft.service.opinion.OpinionCopyService;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author : qinman
 * @date : 2025-02-12
 * @since 9.6.8
 **/
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/opinionCopy", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpinionCopyApiImpl implements OpinionCopyApi {

    private final DocumentCopyService documentCopyService;

    private final OpinionCopyService opinionCopyService;

    @Override
    public Y9Result<Object> deleteById(@RequestParam String id) {
        opinionCopyService.deleteById(id);
        return Y9Result.successMsg("删除成功");
    }

    @Override
    public Y9Result<List<OpinionCopyModel>> findByProcessSerialNumber(String processSerialNumber) {
        List<OpinionCopy> ocList = opinionCopyService.findByProcessSerialNumber(processSerialNumber);
        List<OpinionCopyModel> modelList = new ArrayList<>();
        ocList.forEach(opinionCopy -> {
            OpinionCopyModel model = new OpinionCopyModel();
            Y9BeanUtil.copyProperties(opinionCopy, model);
            if (opinionCopy.isSend()) {
                List<DocumentCopy> dcList = documentCopyService.findByOpinionCopyId(opinionCopy.getId());
                StringBuffer userNames = new StringBuffer();
                dcList.stream()
                    .filter(dc -> dc.getStatus().getValue() < DocumentCopyStatusEnum.CANCEL.getValue())
                    .collect(Collectors.toList())
                    .forEach(dc -> {
                        if (userNames.toString().isEmpty()) {
                            userNames.append(dc.getUserName());
                        } else {
                            userNames.append("，").append(dc.getUserName());
                        }
                    });
                model.setUserNames(userNames.toString());
            }
            modelList.add(model);
        });
        return Y9Result.success(modelList);
    }

    @Override
    public Y9Result<OpinionCopyModel> saveOrUpdate(@RequestBody OpinionCopyModel opinionCopyModel) {
        OpinionCopy opinionCopy = new OpinionCopy();
        Y9BeanUtil.copyProperties(opinionCopyModel, opinionCopy);
        Optional<OpinionCopy> optional = opinionCopyService.saveOrUpdate(opinionCopy);
        if (optional.isPresent()) {
            List<DocumentCopy> dcList =
                documentCopyService.findByProcessSerialNumberAndUserIdAndStatus(opinionCopy.getProcessSerialNumber(),
                    Y9FlowableHolder.getPositionId(), DocumentCopyStatusEnum.TODO_SIGN);
            dcList.forEach(dc -> {
                dc.setStatus(DocumentCopyStatusEnum.SIGN);
                documentCopyService.save(dc);
            });
            Y9BeanUtil.copyProperties(optional.get(), opinionCopyModel);
            return Y9Result.success(opinionCopyModel);
        }
        return Y9Result.failure("保存失败");
    }
}
