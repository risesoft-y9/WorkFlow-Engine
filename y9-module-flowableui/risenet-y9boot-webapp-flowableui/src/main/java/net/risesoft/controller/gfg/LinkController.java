package net.risesoft.controller.gfg;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.entity.CXLink;
import net.risesoft.entity.GLJ;
import net.risesoft.entity.ProcessModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.fgw.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/link", produces = MediaType.APPLICATION_JSON_VALUE)
public class LinkController {


    @Autowired
    private LinkService linkService;

    /**
     * 查询可以关联的流程
     * @param processInstanceId
     * @return
     */
    @GetMapping("/allProcces")
    public Y9Result<List<ProcessModel>> allProccesId(@RequestParam final String processInstanceId) {
        List<ProcessModel> list = linkService.allProccesId(processInstanceId);
        return Y9Result.success(list);
    }


    /**
     * 根据所选流程查询可以关联的文件
     * @param processId
     * @param processInstanceId
     * @param searchMapStr
     * @param page
     * @param row
     * @return
     */
    @PostMapping("/findPiByProcessId")
    public Y9Page<Map<String, Object>> findPiByProcessId(@RequestParam String processId, @RequestParam  String processInstanceId,
                                                         @RequestParam(required = false) String searchMapStr, @RequestParam Integer page,
                                                         @RequestParam Integer row){
        return linkService.findPiByProcessId(processId,processInstanceId,searchMapStr,page,row);
    }

    /**
     * 保存关联文件
     * @param processInstanceId
     * @param glj
     * @return
     */
    @PostMapping("/saveLink")
    public Y9Result<Object> saveLink(@RequestParam String processInstanceId , @RequestParam String glj){
        JSONArray jsonArray = new JSONArray(glj.replace("&amp;quot;","\""));
        List<GLJ> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            GLJ one = new GLJ();
            one.setId(jsonObject.get("instanceId") != null ? jsonObject.get("instanceId").toString():"");
            one.setInstanceId(jsonObject.get("instanceId") != null ? jsonObject.get("instanceId").toString():"");
            one.setTitle(jsonObject.get("title") != null ? jsonObject.get("title").toString():"");
            one.setSerialNumber(jsonObject.get("serialNumber") != null ? jsonObject.get("serialNumber").toString():"");
            one.setFwwh(jsonObject.get("fwwh") != null ? jsonObject.get("fwwh").toString():"");
            list.add(one);
        }
        linkService.saveLink(processInstanceId,list);
        return Y9Result.success();
    }

    /**
     * 根据linkid删除关联关系
     * @param linkid
     * @return
     */
    @GetMapping("/deleteById")
    public Y9Result<Object> deleteById(@RequestParam String linkId){
        linkService.deleteById(linkId);
        return Y9Result.success();
    }

    /**
     * 查询所有关联文件
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/list")
    public Y9Result<Map<String, List<CXLink>>> list(@RequestParam String processInstanceId) {
        Map<String, List<CXLink>> map= linkService.findByInstanceId(processInstanceId);
        return Y9Result.success(map);
    }

}
