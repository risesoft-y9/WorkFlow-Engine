package net.risesoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/rejectReason")
public class RejectReasonController {

    /* @RequestMapping(value="/show")
    public String rejectReason(String processInstanceId,String taskId,String actionSign,String actionSignName,Model model){
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getID();
            RejectReasonModel rejectReason=ItemUtil.getRejectReasonManager().findByProcessInstanceIdAndTaskIdAndActionSign(tenantId,userId,taskId,actionSign,processInstanceId);
            if(null!=rejectReason){
                model.addAttribute("rejectReasonEntity", rejectReason);
            }
            model.addAttribute("processInstanceId", processInstanceId);
            model.addAttribute("taskId", taskId);
            model.addAttribute("actionSign", actionSign);
            model.addAttribute("actionSignName", actionSignName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/approve/rejectReason";
    }
    
    @ResponseBody
    @RequestMapping(value="/saveOrUpdate")
    public Map<String,Object> saveOrUpdate(RejectReasonModel rejectReasonModel){
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getID();
        Map<String,Object> map= ItemUtil.getRejectReasonManager().save(tenantId,userId,rejectReasonModel);
        return map;
    }*/
}
