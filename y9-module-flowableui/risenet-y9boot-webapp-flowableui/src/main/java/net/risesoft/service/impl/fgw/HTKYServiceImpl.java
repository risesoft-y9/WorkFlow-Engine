package net.risesoft.service.impl.fgw;

import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code128Encoder;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.WidthCodedPainter;
import org.jbarcode.util.ImageUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import com.htky.pdf417.BarCodeEntity;
import com.htky.pdf417.BarCodeImgCreator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.service.fgw.HTKYService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.RemoteCallUtil;

@Slf4j
@RequiredArgsConstructor
@Service(value = "hTKYService")
@Transactional(readOnly = true)
public class HTKYServiceImpl implements HTKYService {

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Resource(name = "jdbcTemplate4Dedicated")
    private JdbcTemplate jdbcTemplate4Dedicated;

    /**
     * 判断是否已在老系统生成过待办，已生成的件不能关联
     *
     * @param wnbh
     * @return
     */
    @Override
    public Boolean findIsExist(String wnbh) {
        Boolean flag = false;
        List<Map<String, Object>> lw = jdbcTemplate4Dedicated
            .queryForList("select lwcode,lwinfouid,wnbh,lwtitle,lwdept from D_GW_LWINFO where wnbh = '" + wnbh + "'");
        if (lw.isEmpty()) {
            flag = false;
            return flag;
        }
        Map<String, Object> lwMap = lw.get(0);
        String lwinfouid = String.valueOf(lwMap.get("lwinfouid"));
        String sqlRuntime = "select INSTANCEID from BPM_PROCESSINSTANCERUNTIME where BUSINESSKEY = '" + lwinfouid + "'";
        List<Object> runtime = jdbcTemplate4Dedicated.query(sqlRuntime, (rs, rowNum) -> rs.getString("INSTANCEID"));
        String sqlHistory = "select INSTANCEID from BPM_PROCESSINSTANCEHISTORY where BUSINESSKEY = '" + lwinfouid + "'";
        List<Object> history = jdbcTemplate4Dedicated.query(sqlHistory, (rs, rowNum) -> rs.getString("INSTANCEID"));
        String sqlDone = "select INSTANCEID from BPM_PROCESSINSTANCEDONE where BUSINESSKEY = '" + lwinfouid + "'";
        List<Object> done = jdbcTemplate4Dedicated.query(sqlDone, (rs, rowNum) -> rs.getString("INSTANCEID"));
        if (runtime.size() > 0 || done.size() > 0 || history.size() > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 智能交换 获取司局名称list
     *
     * @param tmh
     * @return
     */
    @Override
    public List<String> getFileOnMove(String tmh) {
        String url = Y9Context.getProperty("y9.common.getSJMC");
        List<String> deptname = new ArrayList<>();
        if (StringUtils.isNotBlank(tmh)) {
            DocumentFactory factory = new DocumentFactory();
            Document document = factory.createDocument();
            Element root = document.addElement("comefileinfo");
            root.addElement("Sysid").setText(Y9Context.getProperty("y9.common.gwsysid"));
            root.addElement("fileCode").setText(tmh);
            String xml = root.asXML();
            LOGGER.info("智能交换中取得的司局名称List,发送的XML：" + xml + ",url:" + url);
            String result = RemoteCallUtil.postXml(url, xml, String.class);
            LOGGER.info("智能交换中取得的司局名称List,航天开元返回result：" + result);
            try {
                Document xmlResult = (new SAXReader()).read(new InputSource(new StringReader(result)));
                Element xmlResultRoot = xmlResult.getRootElement();
                List<Element> list = xmlResultRoot.elements();
                for (Element e : list) {
                    List<Element> elements = e.elements();
                    for (Element file : elements) {
                        deptname.add(file.elementText("dept"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return deptname;
        }
        return deptname;
    }

    /**
     * 清样生成条码号图片
     * 
     * @param map
     * @return
     */
    @Override
    public byte[] getQYTmhPicture(Map<String, Object> map) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        BarCodeImgCreator bar = new BarCodeImgCreator();
        byte[] bytes = null;
        try {
            BarCodeEntity entity = new BarCodeEntity();
            entity.setVerStr("GB0626-2005");
            String tmh = map.get("tmh") == null ? "" : map.get("tmh").toString();
            if (tmh.length() == 16) {
                tmh = tmh + "001";
            }
            entity.setBarcode(tmh);
            entity.setSendDept("办公厅");
            entity.setFileName("");
            if (StringUtils.isNotBlank((String)map.get("wh"))) {
                entity.setFileNum((String)map.get("wh"));
            } else {
                entity.setFileNum("无");
            }

            String zsdept = (String)map.get("zsdw");
            if (StringUtils.isNotBlank(zsdept)) {
                if (zsdept.length() > 20) {
                    entity.setMainSendDept("见报头");
                } else {
                    entity.setMainSendDept(zsdept);
                }
            } else {
                entity.setMainSendDept("");
            }

            String fwtitle = (String)map.get("title");
            if (StringUtils.isNotBlank(fwtitle)) {
                if (fwtitle.length() > 160) {
                    entity.setTitle(fwtitle.substring(160));
                } else {
                    entity.setTitle(fwtitle);
                }
            } else {
                entity.setTitle("");
            }
            String mijiz = (String)map.get("miji");
            String miji = "无";
            if (StringUtils.isNotBlank((String)map.get("miji"))) {
                if ("0".equals(mijiz)) {
                    miji = "无";
                } else if ("1".equals(mijiz)) {
                    miji = "内部";
                } else if ("2".equals(mijiz)) {
                    miji = "秘密";
                } else if ("3".equals(mijiz)) {
                    miji = "机密";
                }
            }
            entity.setSecret(miji);
            String jjcdz = (String)map.get("jjcdz");
            String jjcd = "无";
            if (StringUtils.isNotBlank(jjcdz)) {
                if ("0".equals(mijiz)) {
                    jjcd = "无";
                } else if ("1".equals(jjcdz)) {
                    jjcd = "特急";
                } else if ("2".equals(jjcdz)) {
                    jjcd = "加急";
                }
            }
            entity.setHurry(jjcd);

            if (map.get("cwdate") != null) {
                entity.setFileCreateDate(sdf.format((Date)map.get("cwdate")));
            } else {
                entity.setFileCreateDate("");
            }

            entity.setSendDegree("");
            entity.setBarCreateDept("国家发展和改革委员会秘书二处");
            entity.setBarCreateDate(sdf.format(new Date()));
            entity.setContact("");
            entity.setTel("");
            entity.setOther("");
            bytes = bar.createByte(entity, BarCodeImgCreator.ImgType.JPEG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 获取条码号
     *
     * @param processSerialNumber
     * @return
     */
    @Override
    public String getTMH(String processSerialNumber) {
        String tmh = "";
        try {
            String url = Y9Context.getProperty("y9.common.getTMHUrl");
            String sql =
                "select title,jjcd,miji,zzjfs,gkfs,jzjgnw,zbwbjmc,nigaoren2,dh1,tzjhxdlwj from y9_form_fw where guid = '"
                    + processSerialNumber + "'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> map : list) {
                    // 来文标题（必填）
                    String wjbt = map.get("title") == null ? "" : map.get("title").toString();
                    // 缓急（必填）
                    String hj = "";
                    String jjcd = map.get("jjcd") == null ? "" : map.get("jjcd").toString();
                    if (StringUtils.isNotBlank(jjcd)) {
                        if ("0".equals(jjcd)) {
                            hj = "无";
                        } else if ("1".equals(jjcd)) {
                            hj = "特急";
                        } else if ("2".equals(jjcd)) {
                            hj = "加急";
                        }
                    }
                    // 密级（必填）
                    String mj = "";
                    String miji = map.get("miji") == null ? "" : map.get("miji").toString();
                    if (StringUtils.isNotBlank(miji)) {
                        if ("0".equals(miji)) {
                            mj = "无";
                        } else if ("1".equals(miji)) {
                            mj = "内部";
                        } else if ("2".equals(miji)) {
                            mj = "秘密";
                        } else if ("3".equals(miji)) {
                            mj = "机密";
                        }
                    }
                    // 纸质件份数
                    String fs = map.get("zzjfs") == null ? "" : map.get("zzjfs").toString();
                    // 公开方式
                    String gkfs = "";
                    String pdgkfs = map.get("gkfs") == null ? "" : map.get("gkfs").toString();
                    if (StringUtils.isNotBlank(pdgkfs)) {
                        if ("0".equals(pdgkfs)) {
                            gkfs = "主动公开";
                        } else if ("1".equals(pdgkfs)) {
                            gkfs = "依申请公开";
                        } else if ("2".equals(pdgkfs)) {
                            gkfs = "不公开";
                        }
                    }
                    // 是否加载内网 值为：加载内网
                    String wljzfs = "";
                    String jzjgnw = map.get("jzjgnw") == null ? "" : map.get("jzjgnw").toString();
                    if (StringUtils.isNotBlank(jzjgnw)) {
                        if ("[\"1\"]".equals(jzjgnw)) {
                            wljzfs = "加载内网";
                        }
                    }
                    // 主办单位 xx司（必填）
                    String zbdw = map.get("zbwbjmc") == null ? "" : map.get("zbwbjmc").toString();
                    // 登记人
                    String djr = Y9LoginUserHolder.getUserInfo().getName();
                    // 拟稿人
                    String ngr = map.get("nigaoren2") == null ? "" : map.get("nigaoren2").toString();
                    // 拟稿人电话
                    String ngrdh = map.get("dh1") == null ? "" : map.get("dh1").toString();
                    // 是否是中央预算内投资计划下达类文件，是=1，否=2
                    String istzjh = "2";
                    String tzjhxdlwj = map.get("tzjhxdlwj") == null ? "" : map.get("tzjhxdlwj").toString();
                    if (StringUtils.isNotBlank(tzjhxdlwj)) {
                        if ("[\"1\"]".equals(tzjhxdlwj)) {
                            istzjh = "1";
                        }
                    }
                    // 年份
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                    String nf = sdf.format(new Date());
                    // 调用接口系统名称，0是交换文件（必填）
                    String filetype = "0";
                    // 签报号，filetype为0时可以为空
                    String qbh = "";
                    // 生成xml
                    DocumentFactory factory = new DocumentFactory();
                    Document document = factory.createDocument();
                    Element root = document.addElement("root");
                    Element Sysid = root.addElement("Sysid");
                    Sysid.setText(Y9Context.getProperty("y9.common.gwsysid"));
                    Element comefileinfo = root.addElement("comefileinfo");
                    comefileinfo.addElement("wjbt").setText(wjbt);
                    comefileinfo.addElement("mj").setText(mj);
                    comefileinfo.addElement("hj").setText(hj);
                    comefileinfo.addElement("fs").setText(fs);
                    comefileinfo.addElement("gkfs").setText(gkfs);
                    comefileinfo.addElement("wljzfs").setText(wljzfs);
                    comefileinfo.addElement("zbdw").setText(zbdw);
                    comefileinfo.addElement("djr").setText(djr);
                    comefileinfo.addElement("ngr").setText(ngr);
                    comefileinfo.addElement("ngrdh").setText(ngrdh);
                    comefileinfo.addElement("nf").setText(nf);
                    comefileinfo.addElement("filetype").setText(filetype);
                    comefileinfo.addElement("qbh").setText(qbh);
                    comefileinfo.addElement("istzjh").setText(istzjh);
                    String xml = document.asXML();
                    LOGGER.info("发文获取条码,发送的XML：" + xml + ",url:" + url);
                    String result = RemoteCallUtil.postXml(url, xml, String.class);
                    LOGGER.info("发文获取条码,航天开元返回result：" + result);
                    Document xmlResult = (new SAXReader()).read(new InputSource(new StringReader(result)));
                    Element xmlResultRoot = xmlResult.getRootElement();
                    String barCode = xmlResultRoot.elementTextTrim("tmbh");
                    String status = xmlResultRoot.elementTextTrim("status");
                    String banwenNO = xmlResultRoot.elementTextTrim("bwwh");
                    if ("100".equals(status) && barCode != null && !barCode.trim().equals("")) {
                        if (barCode.length() == 18) {
                            tmh = barCode.substring(0, 15);
                            banwenNO = barCode.substring(9, 15);
                        }
                        if (barCode.length() == 19) {
                            tmh = barCode.substring(0, 16);
                            banwenNO = barCode.substring(10, 16);
                        } else {
                            tmh = barCode;
                            banwenNO = barCode.substring(tmh.length() - 6);
                        }
                        String updateSql =
                            "update y9_form_fw set bwwh = '" + banwenNO + "' where guid='" + processSerialNumber + "'";
                        jdbcTemplate.update(updateSql);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmh;
    }

    /**
     * 生成条码号图片
     *
     * @param tmh
     * @return
     */
    @Override
    public byte[] getTmhPicture(String tmh) {
        JBarcode jbcode = new JBarcode(Code128Encoder.getInstance(), WidthCodedPainter.getInstance(),
            BaseLineTextPainter.getInstance());
        jbcode.setShowCheckDigit(false);
        jbcode.setShowText(false);
        jbcode.setBarHeight(12.0D);
        BufferedImage image = null;
        byte[] bytes = null;
        try {
            if (tmh.length() == 16) {
                tmh = tmh + "001";
            }
            image = jbcode.createBarcode(tmh);
            bytes = ImageUtil.encode(image, "jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 判断当前人是否有权限在来文信息中关联来文
     * 
     * @param bianhao 委内编号
     * @return
     */
    @Override
    public Boolean isAssociated(String bianhao) {
        Boolean flag = false;
        if ("_0000000000000000000000".equals(Y9LoginUserHolder.getUserInfo().getPersonId())) {
            flag = true;
            return flag;
        }
        String dn = Y9LoginUserHolder.getUserInfo().getDn();
        List<Map<String, Object>> lwcodes = jdbcTemplate4Dedicated.query(
            "select lwcode,lwinfouid,wnbh,lwtitle,lwdept from D_GW_LWINFO where wnbh = '" + bianhao + "'",
            (rs, row) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("lwcode", rs.getString("lwcode"));
                map.put("lwinfouid", rs.getString("lwinfouid"));
                map.put("wnbh", rs.getString("wnbh"));
                map.put("lwtitle", rs.getString("lwtitle"));
                map.put("lwdept", rs.getString("lwdept"));
                return map;
            });
        if (lwcodes.size() < 1) {
            flag = true;
            return flag;
        }
        Map<String, Object> lwCodeMap = lwcodes.get(0);
        String tmh = String.valueOf(lwCodeMap.get("lwcode"));
        String deptAliasnameList = "";
        // 智能交换中取得的司局名称List
        List<String> list = getFileOnMove(tmh);
        for (int i = 0; i < list.size(); i++) {
            String deptAliasname = list.get(i);
            if (i == list.size() - 1) {
                deptAliasnameList = deptAliasnameList + "'" + deptAliasname + "'";
            } else {
                deptAliasnameList = deptAliasnameList + "'" + deptAliasname + "',";
            }
        }
        if (deptAliasnameList.length() > 1) {
            String sqlDn = "select DN from Y9_ORG_DEPARTMENT WHERE ALIAS_NAME IN (" + deptAliasnameList + ")";
            List<Map<String, Object>> dnList = jdbcTemplate.query(sqlDn, (rs, rowNum) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("DN", rs.getObject("DN"));
                return map;
            });
            for (Map<String, Object> map : dnList) {
                if (dn.contains(String.valueOf(map.get("DN")))) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}
