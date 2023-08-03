package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.service.AutoFormSequenceService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Controller
@RequestMapping("/wf/autoFormSequence")
public class AutoFormSequenceController {

    @Autowired
    private AutoFormSequenceService autoFormSequenceService;

    @Autowired
    @Qualifier("jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    /**
     * 自动获取日期
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/autoDate")
    public Map<String, String> autoDate() {
        Map<String, String> map = new HashMap<String, String>(16);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        map.put("dateStr", dateStr);
        return map;
    }

    /**
     * 自动获取报销单号
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/docNumber")
    public Map<String, String> characterValues4Select() {
        Date date = new Date();
        SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sqlDate = sqlDateFormat.format(date);
        String sql = "select COUNT(*) from ff_form_bx t where t.createTime='" + sqlDate + "'";
        int count = jdbcTemplate.queryForObject(sql, Integer.class);
        String countStr = "";
        boolean b = count <= 9;
        if (b) {
            countStr = "0" + count;
        } else {
            countStr = count + "";
        }
        Map<String, String> map = new HashMap<String, String>(16);

        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        sb.append(sdf.format(date));
        sb.append(countStr);

        map.put("docNumber", sb.toString());
        return map;
    }

    /**
     * 从后台获取页面的自动编号
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "/docSequence")
    public String genDocSequence(@RequestParam String labelName, @RequestParam(required = false) String character,
        Model model) {
        model.addAttribute("labelName", labelName);
        try {
            character = java.net.URLDecoder.decode(character, "UTF-8");
            String sequence =
                autoFormSequenceService.genSequence(Y9LoginUserHolder.getTenantId(), labelName, character);
            if (StringUtils.isBlank(sequence)) {
                sequence = "0.0";
            }
            model.addAttribute("sequence", sequence);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "intranet/doc-sequence";
    }

    /**
     * 更新指定的序列号
     * 
     * @param labelName
     * @param tenantId
     */
    @RequestMapping(value = "/update")
    public void update(@RequestParam String labelName, @RequestParam(required = false) String character) {
        autoFormSequenceService.updateSequence(Y9LoginUserHolder.getTenantId(), labelName, character);
    }
}
