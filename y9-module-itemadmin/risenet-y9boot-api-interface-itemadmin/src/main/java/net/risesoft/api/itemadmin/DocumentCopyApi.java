package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.DocumentCopyStatusEnum;
import net.risesoft.model.itemadmin.DocumentCopyModel;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author : qinman
 * @date : 2025-02-10
 * @since 9.6.8
 **/
public interface DocumentCopyApi {

    @PostMapping("/deleteByProcessSerialNumber")
    Y9Result<Object> deleteByProcessSerialNumber(@RequestParam("processSerialNumber") String processSerialNumber);

    @GetMapping("/findByProcessSerialNumberAndSenderId")
    Y9Result<List<DocumentCopyModel>>
        findByProcessSerialNumberAndSenderId(@RequestParam("processSerialNumber") String processSerialNumber);

    @PostMapping("/findByProcessSerialNumbers")
    Y9Result<List<DocumentCopyModel>>
        findByProcessSerialNumbers(@RequestParam("processSerialNumbers") String[] processSerialNumbers);

    @PostMapping("/findByUserId")
    Y9Page<DocumentCopyModel> findByUserId(@RequestBody QueryParamModel queryParamModel);

    @PostMapping("/findListByUserId")
    Y9Result<List<DocumentCopyModel>> findListByUserId(@RequestBody(required = false) QueryParamModel queryParamModel);

    @PostMapping("/save")
    Y9Result<Object> save(@RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("users") String users, @RequestParam(value = "opinion", required = false) String opinion);

    @PostMapping("/setStatus")
    Y9Result<Object> setStatus(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("id") String id,
        @RequestParam(value = "status") DocumentCopyStatusEnum status);
}
