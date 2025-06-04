package net.risesoft.service.impl.fgw;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.LwInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.CXLink;
import net.risesoft.entity.GLJ;
import net.risesoft.entity.LinkModel;
import net.risesoft.entity.ProcessInstance;
import net.risesoft.entity.ProcessModel;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.WorkList4GfgService;
import net.risesoft.service.fgw.LinkService;
import net.risesoft.util.gfg.OldUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

import cn.idev.excel.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service(value = "linkService")
@Transactional(readOnly = true)
public class LinkServiceImpl implements LinkService {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final WorkList4GfgService workList4GfgService;
    private final LwInfoApi lwInfoApi;
    private final ProcessParamApi processParamApi;
    private final PositionApi positionApi;
    private final OldUtil oldUtil;
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Resource(name = "jdbcTemplate4Dedicated")
    private JdbcTemplate jdbcTemplate4Dedicated;

    /**
     * 查询可以关联的流程
     * 
     * @param processInstanceId
     * @return
     */
    @Override
    public List<ProcessModel> allProccesId(String processInstanceId) {
        List<ProcessModel> list = OldUtil.allProccesId();
        return list;
    }

    @Override
    public void deleteById(String linkid) {
        String sql = "select *  from bpm_linkruntime where linkid='" + linkid + "'";
        List<LinkModel> results = jdbcTemplate4Dedicated.query(sql, new BeanPropertyRowMapper<>(LinkModel.class));
        LinkModel linkModel = results.isEmpty() ? null : results.get(0);
        if (linkModel == null) {
            return;
        }
        String delete = "delete from bpm_linkruntime where linkid='" + linkid + "'";
        jdbcTemplate4Dedicated.update(delete);
        ProcessInstance from = oldUtil.getOldProcessModel(linkModel.getFrominstanceid());
        ProcessInstance to = oldUtil.getOldProcessModel(linkModel.getToinstanceid());
        ProcessInstance nfrom = fwchange(Y9LoginUserHolder.getTenantId(), linkModel.getFrominstanceid());
        ProcessInstance nto = fwchange(Y9LoginUserHolder.getTenantId(), linkModel.getToinstanceid());
        ProcessInstance cxfrom = null;
        ProcessInstance cxto = null;
        if (from != null) {
            cxfrom = from;
        }
        if (nfrom != null) {
            cxfrom = nfrom;
        }
        if (to != null) {
            cxto = to;
        }
        if (nto != null) {
            cxto = nto;
        }
        if ("NORMAL".equals(linkModel.getLinktype())) {
            if (OldUtil.ljProcessId.equals(cxfrom.getProcessId()) && (OldUtil.fwProcessId.equals(cxto.getProcessId())
                || OldUtil.newFwProcessId.equals(cxto.getProcessId()))) {
                String lwinfoUid = cxfrom.getBusinesskey();
                // 需要修改 大厅来件
                String cx = "select name from D_GW_GJJ_CONFIG";
                List<String> cxList = jdbcTemplate4Dedicated.query(cx, (rs, rowNum) -> rs.getString("name"));
                String lwsql = "select ZBDEPT from D_GW_LWINFO where lwinfouid='" + lwinfoUid + "'";
                List<String> res = jdbcTemplate4Dedicated.query(lwsql, (rs, rowNum) -> rs.getString("ZBDEPT"));
                String lwzbdept = res.isEmpty() ? null : res.get(0);
                String xgSql = "";
                if (cxList.contains(lwzbdept)) {// 办结方式制空，办理方式不制空
                    xgSql = " update D_GW_LWINFO set finishtype=0 where lwinfouid ='" + from.getBusinesskey() + "'";
                } else {
                    xgSql = " update D_GW_LWINFO set finishtype=0,handletype=0 where lwinfouid ='"
                        + from.getBusinesskey() + "'";
                }
                jdbcTemplate4Dedicated.update(xgSql);
            }
        }
        // 非电子件删除办文信息表的数据
        if (!"NORMAL_OFFICELINE".equals(linkModel.getLinktype())) {
            return;
        }

        lwInfoApi.delLwInfo(Y9LoginUserHolder.getTenantId(), cxfrom.getBusinesskey());
    }

    @Override
    public Map<String, List<CXLink>> findByInstanceId(String processInstanceId) {
        String fromSql = "select * from bpm_linkruntime a where a.fromInstanceId = '" + processInstanceId
            + "' order by a.created desc ";
        List<LinkModel> fromlist = jdbcTemplate4Dedicated.query(fromSql, new BeanPropertyRowMapper<>(LinkModel.class));
        Map<String, List<CXLink>> fromLinkMap = linkMap(processInstanceId, fromlist, 0);
        String toSql = "select * from bpm_linkruntime a where a.toInstanceId = '" + processInstanceId
            + "' order by a.created desc ";
        List<LinkModel> tolist = jdbcTemplate4Dedicated.query(toSql, new BeanPropertyRowMapper<>(LinkModel.class));
        Map<String, List<CXLink>> toLinkMap = linkMap(processInstanceId, tolist, 1);
        Map<String, List<CXLink>> map = new HashMap<String, List<CXLink>>();
        List<ProcessModel> processList = allProccesId(processInstanceId);
        for (ProcessModel processModel : processList) {
            List<CXLink> fromList = fromLinkMap.get(processModel.getProcessName());
            List<CXLink> toList = toLinkMap.get(processModel.getProcessName());
            Set<CXLink> set = new HashSet<>();
            set.addAll(fromList);
            set.addAll(toList);
            List<CXLink> zhList = new ArrayList<>(set);// 整合
            map.put(processModel.getProcessName(), zhList);
        }
        return map;
    }

    /**
     * 根据流程id获取流程实例.获取可关联的文件只限于本人办理经手过的文件
     *
     * @param fromInstanceId //关联件的id
     * @param processId //关联的流程
     * @param searchMapStr //查询条件
     * @param page 页数
     * @param row 一页多少条
     * @return
     */
    @Override
    public Y9Page<Map<String, Object>> findPiByProcessId(String processId, String fromInstanceId, String searchMapStr,
        Integer page, Integer row) {
        if (OldUtil.newFwProcessId.equals(processId)) {
            if (searchMapStr != null && !searchMapStr.isEmpty()) {
                searchMapStr = searchMapStr.replaceAll("serialNumber", "lsh");
            }
            Y9Page<Map<String, Object>> res = workList4GfgService.allList(processId, searchMapStr, false, page, row);
            List<Map<String, Object>> list = res.getRows();
            List<Map<String, Object>> dataList = new ArrayList();
            for (Map<String, Object> data : list) {
                HashMap map = new HashMap();
                map.put("instanceId", data.get("processInstanceId"));
                map.put("id", data.get("processInstanceId"));
                map.put("title", data.get("title"));
                map.put("fwwh", data.get("fwwh"));
                map.put("serialNumber", data.get("lsh"));
                dataList.add(map);
            }
            res.setRows(dataList);
            return res;
        } else {
            // 旧系统 根据老系统的逻辑查，修改部分查询代码
            String userId =
                Y9LoginUserHolder.getUserInfo().getCaid().replace("-", "").replace("{", "").replace("}", "");
            String sql_count = linkListSql(processId, userId, 0, searchMapStr);
            int totalRows = jdbcTemplate4Dedicated.queryForObject(sql_count, Integer.class);
            int totalPages = 0;
            if (totalRows > 0) {
                totalPages = (int)Math.ceil((double)totalRows / row);
            }
            String sql_content = "select * from (" + "select query.*, rownum rnum from ("
                + linkListSql(processId, userId, 1, searchMapStr) + ") query where rownum <= " + page * row
                + ") where rnum >" + (page - 1) * row + " order by rnum";
            List<Map<String, Object>> list = jdbcTemplate4Dedicated.queryForList(sql_content);
            List<Map<String, Object>> dataList = new ArrayList();
            for (Map<String, Object> listData : list) {
                HashMap map = new HashMap();
                map.put("instanceId", listData.get("instanceId"));
                map.put("id", listData.get("instanceId"));
                map.put("title", listData.get("title"));
                if (processId.equals(OldUtil.fwProcessId)) {
                    map.put("fwwh", listData.get("fwwh"));
                }
                map.put("serialNumber", listData.get("serialNumber"));
                dataList.add(map);
            }
            return Y9Page.success(page, totalPages, totalRows, dataList, "获取列表成功");
        }
    }

    /**
     * 新发文转换老发文格式转需要的就行
     *
     * @param tenantId
     * @param instanceId
     * @return
     */
    private ProcessInstance fwchange(String tenantId, String instanceId) {
        ProcessInstance pi = null;
        try {
            if (StringUtils.isNotBlank(instanceId)) {
                ProcessParamModel pm = processParamApi.findByProcessInstanceId(tenantId, instanceId).getData();
                if (pm != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String lshSql =
                        "select lsh,guid from y9_form_fw where guid = '" + pm.getProcessSerialNumber() + "'";

                    List<String> res =
                        jdbcTemplate.query(lshSql, (rs, rowNum) -> rs.getString("lsh") + "," + rs.getString("guid"));
                    String lsh = "";
                    String guid = "";
                    for (String userInfo : res) {
                        // 按逗号分割字符串，获取id和name
                        String[] parts = userInfo.split(",");
                        lsh = parts[0];
                        guid = parts[1];
                    }
                    Person person = positionApi.listPersonsByPositionId(tenantId, pm.getStartor()).getData().get(0);
                    pi = new ProcessInstance();
                    pi.setInstanceid(instanceId);
                    pi.setBpmserver("new");
                    pi.setBusinesskey(guid);
                    pi.setCreated(sdf.parse(pm.getCreateTime()));
                    pi.setCreaterDn(person.getDn());
                    pi.setCreaterName(pm.getStartorName());
                    pi.setCreaterUid(pm.getStartor());
                    pi.setSerialnumber(lsh);
                    pi.setTitle(pm.getTitle());
                    pi.setProcessId(OldUtil.newFwProcessId);
                    pi.setProcessName("新发文");
                    pi.setBingan(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    private String getBmjc(String personDn) {
        String jc = "";
        String departmentName = "";
        String[] dns = personDn.split(",");
        String dmDN = "";
        if (dns.length == 1) {
            dmDN = dns[0];
            departmentName = dns[0];
            departmentName = departmentName.substring(2);
            return null;
        } else {
            departmentName = dns[dns.length - 2];
            dmDN = dns[dns.length - 2] + "," + dns[dns.length - 1];
            departmentName = departmentName.substring(3);
        }

        String sql = "select ALIAS_NAME from Y9_ORG_DEPARTMENT where name = '" + departmentName + "'";
        List<String> res = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("ALIAS_NAME"));
        jc = res.isEmpty() ? null : res.get(0);
        return jc;
    }

    private String linkListSql(String processId, String userId, int tag, String searchMapStr) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchMap = Y9JsonUtil.readHashMap(searchMapStr);
        String fwwh = searchMap.get("fwwh") != null ? searchMap.get("fwwh").toString() : null;
        String serialnumber = searchMap.get("serialnumber") != null ? searchMap.get("serialnumber").toString() : null;
        String title = searchMap.get("title") != null ? searchMap.get("title").toString() : null;
        if (tag == 0) {
            sb.append(" select count(*) from ( ");
        }
        sb.append(" select * from ( ");
        // 来文特殊处理
        if (!processId.equals(OldUtil.zqyjProcessId)) {
            sb.append(" select distinct pi.instanceId,pi.SERIALNUMBER,pi.TITLE,pi.created,pi.updated ");
            if (processId.equals("_N7pw8DY8EeO5EqWURXzpRA") || StringUtils.isNotBlank(fwwh)) {
                sb.append(",d_gw_fawen.fwwh");
            }
            sb.append(" FROM BPM_ACTIVITYINSTANCERUNTIME ai,BPM_PROCESSINSTANCERUNTIME pi,BPM_WORKITEMRUNTIME wi ");
            if (processId.equals("_N7pw8DY8EeO5EqWURXzpRA") || StringUtils.isNotBlank(fwwh)) {
                sb.append(",d_gw_fawen");
            }
            sb.append(" WHERE pi.instanceId=ai.instanceId");
            sb.append(" AND ai.ACTIVITYINSTANCEPK=wi.ACTIVITYINSTANCEPK");
            if (processId.equals("_N7pw8DY8EeO5EqWURXzpRA") || StringUtils.isNotBlank(fwwh)) {
                sb.append(" AND pi.businesskey = d_gw_fawen.bwduid");
            }
            sb.append(" AND pi.processId='" + processId + "'");
            sb.append(" AND pi.running=1");
            sb.append(" AND pi.deleted=0");
            sb.append(" AND wi.work<100");
            sb.append(" AND ( wi.recipientId='" + userId + "' or pi.createruid='" + userId + "' )");
            if ("null".equals(serialnumber)) {
                sb.append(" AND pi.SERIALNUMBER like '%" + serialnumber + "%'");
            }
            if ("null".equals(title)) {
                sb.append(" AND pi.title like '%" + title + "%'");
            }
            if ("null".equals(fwwh)) {
                sb.append(" AND fwwh like '%" + fwwh + "%'");
            }
            sb.append(" union ");
        } // 办结
        sb.append(" select distinct pi.instanceId,pi.SERIALNUMBER,pi.TITLE,pi.created,pi.updated ");
        if (processId.equals("_N7pw8DY8EeO5EqWURXzpRA") || StringUtils.isNotBlank(fwwh)) {
            sb.append(",d_gw_fawen.fwwh");
        }
        sb.append(" FROM BPM_ACTIVITYINSTANCEDONE ai,BPM_PROCESSINSTANCEDONE pi,BPM_WORKITEMDONE wi ");
        if (processId.equals("_N7pw8DY8EeO5EqWURXzpRA") || StringUtils.isNotBlank(fwwh)) {
            sb.append(",d_gw_fawen");
        }
        sb.append(" WHERE pi.instanceId=ai.instanceId");
        sb.append(" AND ai.ACTIVITYINSTANCEPK=wi.ACTIVITYINSTANCEPK");
        if (processId.equals("_N7pw8DY8EeO5EqWURXzpRA") || StringUtils.isNotBlank(fwwh)) {
            sb.append(" AND pi.businesskey=d_gw_fawen.bwduid");
        }
        sb.append(" AND pi.processId='" + processId + "'");
        sb.append(" AND pi.deleted=0");
        sb.append(" AND wi.work<100");
        sb.append(" AND ( wi.recipientId='" + userId + "' or pi.createruid='" + userId + "' )");
        if ("null".equals(serialnumber)) {
            sb.append(" AND pi.SERIALNUMBER like '%" + serialnumber + "%'");
        }
        if ("null".equals(title)) {
            sb.append(" AND pi.title like '%" + title + "%'");
        }
        if ("null".equals(fwwh)) {
            sb.append(" AND fwwh like '%" + fwwh + "%'");
        }
        sb.append(" ) ");
        sb.append(" order by updated desc  ");
        if (tag == 0) {
            sb.append(" ) ");
        }
        LOGGER.info("查询老系统可关联的关联文件的sql:- " + sb);
        return sb.toString();
    }

    private Map<String, List<CXLink>> linkMap(String instanceId, List<LinkModel> links, int listType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 封装的查询linkMap
        Map<String, List<CXLink>> cxmap = new LinkedHashMap<>();
        List<ProcessModel> processList = allProccesId(instanceId);
        for (ProcessModel processModel : processList) {
            List<CXLink> list = cxmap.get(processModel.getProcessName());
            if (list == null) {
                list = new ArrayList<CXLink>();
            }
            cxmap.put(processModel.getProcessName(), list);
        }
        for (LinkModel linkModel : links) {
            String fromInstanceId = linkModel.getFrominstanceid();
            String toInstanceId = linkModel.getToinstanceid();
            ProcessInstance from = oldUtil.getOldProcessModel(fromInstanceId); // 老系统查询的fpi
            ProcessInstance to = oldUtil.getOldProcessModel(toInstanceId); // 老系统查询的tpi
            ProcessInstance nfrom = fwchange(tenantId, fromInstanceId); // 新系统查询的fpi
            ProcessInstance nto = fwchange(tenantId, toInstanceId); // 新系统查询的tpi
            // 这个是最后实际用的pi
            ProcessInstance ccFrom = new ProcessInstance();
            ProcessInstance ccTo = new ProcessInstance();
            if (from != null || nfrom != null) {
                if (from != null) {
                    ccFrom = from;
                }
                if (nfrom != null) {
                    ccFrom = nfrom;
                }
            }
            Boolean isSl = "NORMAL_OFFICELINE".equals(linkModel.getLinktype()); // 是否是非实例
            if (isSl) {
                to = new ProcessInstance();
                to.setInstanceid(linkModel.getToinstanceid());
                to.setTitle(linkModel.getDescription());
                to.setSerialnumber(linkModel.getName());
                to.setCreaterDn(ccFrom.getCreaterDn());
                to.setProcessId(OldUtil.ljProcessId);
                to.setProcessName("大厅来件");
                to.setBingan(0);
            }
            if (to != null || nto != null) {
                if (to != null) {
                    ccTo = to;
                }
                if (nto != null) {
                    ccTo = nto;
                }
            }
            for (ProcessModel process : processList) {
                CXLink CXLink = new CXLink();
                String departmentName = "";// 展示的件的主办司局名称
                String activityInstanceId = ""; // 老系统打开办件的id
                String type = "";// 办件新老 0旧系统，1新系统
                if (listType == 0 && isSl && !process.getProcessId().equals(OldUtil.ljProcessId)) {
                    continue;
                }
                if (listType == 0 && ccTo != null) {
                    departmentName = getBmjc(ccTo.getCreaterDn());
                    if (to != null) {
                        if (!to.getProcessId().equals(process.getProcessId())) {
                            continue;
                        }
                        type = "0";
                        activityInstanceId = to.getActivityInstanceId();
                    } else if (nto != null) {
                        type = "1";
                        if (!nto.getProcessId().equals(process.getProcessId())) {
                            continue;
                        }
                    }
                }
                if (listType == 0 && isSl && process.getProcessId().equals(OldUtil.ljProcessId)) {
                    departmentName = getBmjc(ccFrom.getCreaterDn());
                    if (from != null) {
                        type = "0";
                    } else {
                        type = "1";
                    }

                }
                if (listType == 1 && (from != null || nfrom != null)) {
                    departmentName = getBmjc(ccFrom.getCreaterDn());
                    if (from != null) {
                        if (!from.getProcessId().equals(process.getProcessId())) {
                            continue;
                        }
                        type = "0";
                        activityInstanceId = from.getActivityInstanceId();
                    } else {
                        type = "1";
                        if (!nfrom.getProcessId().equals(process.getProcessId())) {
                            continue;
                        }
                    }
                }
                if (ccFrom != null && ccTo != null) {
                    List<CXLink> list = cxmap.get(process.getProcessName());
                    CXLink.setLinkId(linkModel.getLinkid());
                    CXLink.setLinkType(linkModel.getLinktype());
                    if (listType == 0) {
                        CXLink.setName(ccTo.getSerialnumber());
                        CXLink.setTitle(ccTo.getTitle());
                        CXLink.setProcessInstanceId(ccTo.getInstanceid());
                    } else {
                        CXLink.setName(ccFrom.getSerialnumber());
                        CXLink.setTitle(ccFrom.getTitle());
                        CXLink.setProcessInstanceId(ccFrom.getInstanceid());
                    }
                    CXLink.setType(type);
                    CXLink.setDepartmentName(departmentName);
                    CXLink.setCreated(sdf.format(linkModel.getCreated()));
                    CXLink.setCreaterName(linkModel.getCreatername());
                    CXLink.setActivityInstanceId(activityInstanceId);
                    if ("0".equals(CXLink.getType())) {
                        // if (StringUtils.isNotBlank(activityInstanceId)){
                        CXLink.setLinkUrl(
                            "http://192.168.50.22/bpm/client/instance_open?activityInstanceId=" + activityInstanceId);
                        // }
                    }
                    CXLink.setFrom(ccFrom);
                    CXLink.setTo(ccTo);
                    list.add(CXLink);
                }
            }
        }
        // 处理并案件
        for (String processName : cxmap.keySet()) {
            List<CXLink> CXLinks2 = new ArrayList<CXLink>();
            List<CXLink> CXLinks = cxmap.get(processName);
            for (CXLink CXLink : CXLinks) {
                ProcessInstance from = CXLink.getFrom();
                ProcessInstance to = CXLink.getTo();
                CXLinks2.add(CXLink);
                if ((to == null || to.getBingan() == 2) && listType == 1) {
                    continue;
                }
                if (from.getBingan() == 1 && listType == 1) {
                    String baSql = "select * from bpm_linkruntime a where a.fromInstanceId = '" + from.getInstanceid()
                        + "' order by a.created desc ";
                    List<LinkModel> fromlist =
                        jdbcTemplate4Dedicated.query(baSql, new BeanPropertyRowMapper<>(LinkModel.class));
                    for (LinkModel link2 : fromlist) {
                        if ("BING_AN".equals(link2.getLinktype())) {
                            CXLink CXLink2 = new CXLink();
                            BeanUtils.copyProperties(CXLink, CXLink2);
                            ProcessInstance toPi = oldUtil.getOldProcessModel(link2.getToinstanceid());
                            if (toPi != null) {
                                CXLink2.setType("0");
                                CXLink2.setLinkId(link2.getLinkid());
                                CXLink2.setLinkType(link2.getLinktype());
                                CXLink2.setName(toPi.getSerialnumber());
                                CXLink2.setTitle(toPi.getTitle());
                                CXLink2.setDepartmentName(getBmjc(toPi.getCreaterDn()));
                                CXLink2.setCreaterName(link2.getCreatername());
                                CXLink2.setLinkUrl("http://192.168.50.22/bpm/client/instance_open?activityInstanceId=");
                                CXLink2.setCreated(sdf.format(link2.getCreated()));
                                CXLink2.setActivityInstanceId(toPi.getActivityInstanceId());
                                CXLink2.setProcessInstanceId(toPi.getInstanceid());
                                CXLink2.setTo(toPi);
                                CXLink2.setFrom(from);
                                CXLinks2.add(CXLink2);
                            }
                        }
                    }
                }
            }
            cxmap.put(processName, CXLinks2);
        }
        return cxmap;
    }

    @Override
    public void saveLink(String processInstanceId, List<GLJ> to) {
        for (GLJ glj : to) {
            String sql = "SELECT linkId FROM bpm_linkruntime a " + "WHERE a.fromInstanceId = '" + processInstanceId
                + "' " + "AND a.toInstanceId = '" + glj.getInstanceId() + "' " + "ORDER BY a.created DESC";
            List<String> results = jdbcTemplate4Dedicated.query(sql, (rs, rowNum) -> rs.getString("linkId"));
            String linkId = results.isEmpty() ? null : results.get(0);
            if (linkId != null && !"null".equals(linkId)) {
                deleteById(linkId);
            }
            Date date = new Date();
            String time = sdf.format(date);
            UserInfo user = Y9LoginUserHolder.getUserInfo();
            String userId = user.getCaid().replace("-", "").replace("{", "").replace("}", "");
            String tenantId = Y9LoginUserHolder.getTenantId();
            String insertSql = "insert into bpm_linkruntime ( LINKID,DESCRIPTION,FROMINSTANCEID,NAME,"
                + "TOINSTANCEID,BPMSERVER,CREATED,CREATERDN," + "CREATERNAME,CREATERUID,TENANTID,UPDATED,"
                + "UPDATERDN,UPDATERNAME,UPDATERUID,LINKACTIVITYID,LINKTYPE) " + "values ('" + Y9IdGenerator.genId()
                + "','" + glj.getTitle() + "','" + processInstanceId + "','" + glj.getSerialNumber() + "'," + "'"
                + glj.getInstanceId() + "','new','" + time + "','" + user.getDn() + "'," + "'" + user.getName() + "','"
                + userId + "','" + tenantId + "','" + time + "'," + "'" + user.getDn() + "','" + user.getName() + "','"
                + userId + "','" + time + "'," + "'NORMAL')";
            jdbcTemplate4Dedicated.update(insertSql);
        }
    }
}
