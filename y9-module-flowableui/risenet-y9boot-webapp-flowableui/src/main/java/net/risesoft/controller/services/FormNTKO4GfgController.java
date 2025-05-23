package net.risesoft.controller.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DocumentWordApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.MergeFileApi;
import net.risesoft.api.itemadmin.OpinionApi;
import net.risesoft.api.itemadmin.PaperAttachmentApi;
import net.risesoft.api.itemadmin.PrintLogApi;
import net.risesoft.api.itemadmin.SecretLevelRecordApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.SignDeptInfoApi;
import net.risesoft.api.itemadmin.TmhPictureApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.TypeSettingInfoApi;
import net.risesoft.api.itemadmin.WordTemplateApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.DocumentWordModel;
import net.risesoft.model.itemadmin.MergeFileModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.PaperAttachmentModel;
import net.risesoft.model.itemadmin.PrintLogModel;
import net.risesoft.model.itemadmin.SecretLevelModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.SignDeptModel;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.TmhPictureModel;
import net.risesoft.model.itemadmin.TypeSettingInfoModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.fgw.HTKYService;
import net.risesoft.util.ToolUtil;
import net.risesoft.util.gfg.OpinionUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

import cn.hutool.core.date.DateUtil;

/**
 * 正文相关接口
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/services/ntkoForm/gfg")
@Slf4j
public class FormNTKO4GfgController {

    private final Y9FileStoreService y9FileStoreService;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    private final DocumentWordApi documentWordApi;

    private final WordTemplateApi wordTemplateApi;

    private final TransactionWordApi transactionWordApi;

    private final SignDeptDetailApi signDeptDetailApi;

    private final FormDataApi formDataApi;

    private final OpinionApi opinionApi;

    private final PrintLogApi printLogApi;

    private final TypeSettingInfoApi typeSettingInfoApi;

    private final PaperAttachmentApi paperAttachmentApi;

    private final MergeFileApi mergeFileApi;

    private final TmhPictureApi tmhPictureApi;

    private final SecretLevelRecordApi secretLevelRecordApi;

    private final SignDeptInfoApi signDeptInfoApi;
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HTKYService htkyService;

    public static HashMap<String, String> getConfigByExcel(String rowNum, String cellNum, String sheetNum) {
        HashMap rowMap = new HashMap();
        try {
            Workbook workbook = getWorkbook();
            Sheet sheet = workbook.getSheetAt(Integer.parseInt(sheetNum));

            Row row = sheet.getRow(0);
            int rowNumber = Integer.parseInt(rowNum);
            Row rowConfig = sheet.getRow(rowNumber);
            if (rowConfig == null) {
                LOGGER.error("从EXCEL中获取配置失败,查看是否没有配置所需的行，自动读取最后一行有效数据");
                for (int num = rowNumber - 1; (rowConfig == null) && (num > 0); num--) {
                    rowConfig = sheet.getRow(num);
                }

            }

            for (int a = 0; a < Integer.parseInt(cellNum); a++) {
                row.getCell(a).setCellType(CellType.STRING);
                rowConfig.getCell(a).setCellType(CellType.STRING);
                rowMap.put(row.getCell((short)a).getStringCellValue(),
                    rowConfig.getCell((short)a).getStringCellValue());
            }
        } catch (Exception e) {
            LOGGER.error("从EXCEL中获取配置失败", e);
        }
        return rowMap;
    }

    private static Workbook getWorkbook() throws Exception {
        String rootDir = Y9Context.getProperty("y9.feature.file.local.basePath");
        FileInputStream fileInputStream = new FileInputStream(rootDir + "/template/清样配置/印厂清样模板配置表.xls");
        Workbook workbook = new HSSFWorkbook(fileInputStream);
        fileInputStream.close();
        return workbook;
    }

    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam String id, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            DocumentWordModel model = documentWordApi.findWordById(tenantId, id).getData();
            String title = model.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), StandardCharsets.ISO_8859_1);
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (userAgent.contains("Firefox")) {
                    title =
                        "=?UTF-8?B?" + (new String(Base64.encodeBase64(title.getBytes(StandardCharsets.UTF_8)))) + "?=";
                } else {
                    title = URLEncoder.encode(title, StandardCharsets.UTF_8);
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(model.getFileStoreId(), out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文失败", e);
        }
    }

    /**
     * 获取正文数据
     *
     * @param processSerialNumber 流程编号id
     * @param itemId 表单id
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @return 正文数据
     */
    @RequestMapping(value = "/getFormData")
    public Y9Result<Map<String, Object>> getFormData(@RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String processInstanceId,
        @RequestParam String tenantId, @RequestParam String userId, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String itembox, @RequestParam(required = false) String qingyangId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
        // 办文要报-厅领导意见
        String bwybBgtfzryj = "tingLeaderComment";
        List<OpinionListModel> data1 = opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId,
            itembox, bwybBgtfzryj, itemId, null, null, null).getData();
        String tldContent = OpinionUtil.generateOpinions(data1);
        // 办文要报-核稿人意见
        String bwybBgthgyj = "reviewerComment";
        List<OpinionListModel> data2 = opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId,
            itembox, bwybBgthgyj, itemId, null, null, null).getData();
        String hgrContent = OpinionUtil.generateOpinions(data2);
        // 办文要报-领导批示
        String bwybLdps = "leaderComment";
        List<OpinionListModel> data3 = opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId,
            itembox, bwybLdps, itemId, null, null, null).getData();
        String ldContent = OpinionUtil.generateOpinions(data3);

        // 办文要报-司长意见
        String bwybSzyj = "directorComment";
        List<OpinionListModel> data4 = opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId,
            itembox, bwybSzyj, itemId, null, null, null).getData();
        String szContent = OpinionUtil.generateOpinions(data4);
        // 办文要报-处长意见
        String bwybCzyj = "deptLeaderComment";
        List<OpinionListModel> data5 = opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId,
            itembox, bwybCzyj, itemId, null, null, null).getData();
        String czContent = OpinionUtil.generateOpinions(data5);

        // 获取办文信息纸质附件清单
        List<PaperAttachmentModel> paperAttList =
            paperAttachmentApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();

        // 发文稿纸-定密依据、具体事项、说明（定密记录）
        List<SecretLevelModel> secretLevelRecord =
            secretLevelRecordApi.getRecord(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();

        // 单独处理清样文件书签
        if (StringUtils.isNotBlank(qingyangId)) {
            String permUpdate = "write"; // 修改权限
            String templateMeishouId = null; // 眉首id
            String templateBanJiId = null; // 版式id
            TypeSettingInfoModel typeSettingInfoModel =
                typeSettingInfoApi.getTypeSetting(Y9LoginUserHolder.getTenantId(), qingyangId).getData();
            String qymb = typeSettingInfoModel.getTemplate();

            String yfdate = "";
            String yfdateStr = formData.get("fwd_fwdate") + "";
            if (StringUtils.isNotBlank(yfdateStr) && !"".equals(yfdateStr.trim())) {
                LocalDate date = LocalDate.parse(yfdateStr);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
                yfdate = date.format(formatter) + "印发"; // 印发日期
            } else {
                yfdate = "印发"; // 印发日期
            }

            String wwcsDept = formData.get("wwcsdept") + "";
            String wncsDept = formData.get("wncsdept") + "";
            if ((wwcsDept != null) && (!wwcsDept.equals(""))) {
                wwcsDept = wwcsDept.replaceAll("\\s*|\r|\n", "");
                formData.put("wwcsdept", wwcsDept);
            }
            if ((wncsDept != null) && (!wncsDept.equals(""))) {
                wncsDept = wncsDept.replaceAll("\\s*|\r|\n", "");
                formData.put("wncsdept", wncsDept);
            }
            String month = "";
            String year = "";
            String day = "";

            String fwwh = (String)formData.get("fwwh");
            String nianfen = "";
            try {
                if (fwwh.contains("[")) {
                    fwwh = fwwh.replace("[", "〔");
                    fwwh = fwwh.replace("]", "〕");
                    String num = fwwh.substring(fwwh.lastIndexOf("〕") + 1);
                    fwwh = fwwh.substring(0, fwwh.indexOf("〕") + 1) + num.replaceAll("^[0]+", "");
                    formData.put("fwwh", fwwh);
                } else if (fwwh.contains("年")) {
                    nianfen = fwwh.substring(0, fwwh.indexOf("年"));
                    String num = fwwh.substring(fwwh.lastIndexOf("第") + 1);
                    num = num.substring(0, num.indexOf("号"));
                    fwwh = num;
                    formData.put("nianfen", nianfen);
                    formData.put("fwwh", fwwh);
                } else if ((!fwwh.contains("年")) && (fwwh.contains("第")) && (fwwh.contains("号"))) {
                    String num = fwwh.substring(fwwh.lastIndexOf("第") + 1);
                    num = num.substring(0, num.indexOf("号"));
                    formData.put("fwwh", num);
                }
            } catch (Exception e) {
                fwwh = (String)formData.get("fwwh");
                LOGGER.error("拼接fwwh出错，放弃拼接：" + fwwh + e);
            }
            String cwDateStr = formData.get("cwdate") + "";
            Date cwDate = null;
            if (StringUtils.isNotBlank(cwDateStr) && !"".equals(cwDateStr.trim())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    cwDate = sdf.parse(cwDateStr);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            if (cwDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(cwDate);
                year = Integer.toString(calendar.get(1));
                month = Integer.toString(calendar.get(2) + 1);
                day = " ";
            }
            formData.put("year", year);
            formData.put("month", month);
            formData.put("day", day);
            // 获取联合发文 1:委外、2:联合
            List<SignDeptModel> lhfwdept =
                signDeptInfoApi.getSignDeptList(Y9LoginUserHolder.getTenantId(), "2", processSerialNumber).getData();
            String lhfwdeptStr = lhfwdept.stream().map(SignDeptModel::getDeptName).collect(Collectors.joining(","));
            String lhfwdeptStrFull = "";
            String[] lhfwdeptList = new String[0];
            if (StringUtils.isBlank(lhfwdeptStr)) {
                lhfwdeptStr = "国家发展和改革委员会";
            }
            if (!lhfwdeptStr.contains("国家发展和改革委员会")) {
                lhfwdeptStr = "国家发展和改革委员会," + lhfwdeptStr;
            }
            if (lhfwdeptStr != null) {
                lhfwdeptList = lhfwdeptStr.split("\\,");
                if ((qymb.equals("令")) || (qymb.equals("公告"))) {
                    try {
                        lhfwdeptStrFull = getLhfwdeptfull(tenantId, lhfwdeptList);
                    } catch (Exception e) {
                        lhfwdeptStrFull = lhfwdeptStr;
                        LOGGER.error("生成令与公告的联合发文单位出错（fulldeptname）,使用lhfwdeptStr，lhfwdeptStrFull:" + lhfwdeptStrFull,
                            e);
                    }
                }
            }

            String lhfwdeptZwList = "";
            String zwMaxLength = "7";
            Map mapList = null;
            if (qymb.equals("令")) {
                mapList = getLhfwdeptZwList(tenantId, lhfwdeptList);
                lhfwdeptZwList = (String)mapList.get("lhfwdeptZwList");
                zwMaxLength = (String)mapList.get("maxLength");
            }

            boolean isTing = (qymb.equals("厅便函")) || (qymb.equals("厅下发"));
            if (qymb.equals("厅下发")) {
                lhfwdeptStr = getLhfwdeptStrTing(lhfwdeptStr);
            }
            int columns = 0;
            String lhfwdeptListWithOrder = "";
            Map mapListWithOrder = null;
            int maxLength = 7;
            if (lhfwdeptList.length > 1) {
                if (qymb.equals("委下发")) {
                    columns = 3;
                }
                mapListWithOrder = getLhfwdeptList(tenantId, lhfwdeptList, columns, isTing);
                lhfwdeptListWithOrder = (String)mapListWithOrder.get("lhfwdeptListWithOrder");
                columns = ((Integer)mapListWithOrder.get("columns")).intValue();
                maxLength = ((Integer)mapListWithOrder.get("maxLength")).intValue();
            }

            String cellNum = "11";
            String sheetNum = "0";
            String rowNum = "";
            if (qymb.equals("公告")) {
                cellNum = "6";
                sheetNum = "1";
            }
            if (qymb.equals("厅下发")) {
                cellNum = "10";
                sheetNum = "2";
            }
            if (qymb.equals("令")) {
                cellNum = "10";
                sheetNum = "3";
            }
            if ((qymb.equals("委下发")) || (qymb.equals("厅下发")) || (qymb.equals("公告")) || (qymb.equals("厅便函"))
                || (qymb.equals("委便函")) || (qymb.equals("专报便函"))) {
                if ((StringUtils.isNotBlank(lhfwdeptStr)) && (1 < lhfwdeptList.length)) {
                    templateMeishouId = qymb + "-眉首-多.doc";
                    if (columns == 3)
                        templateBanJiId = qymb + "-版记-多-3列.doc";
                    else {
                        templateBanJiId = qymb + "-版记-多-2列.doc";
                    }
                    rowNum = Integer.toString(lhfwdeptList.length);
                } else {
                    templateMeishouId = qymb + "-眉首.doc";
                    templateBanJiId = qymb + "-版记.doc";
                    rowNum = "1";
                }
            } else {
                templateBanJiId = qymb + "-版记.doc";
                if ((StringUtils.isNotBlank(lhfwdeptStr)) && (1 < lhfwdeptList.length)) {
                    templateMeishouId = qymb + "-眉首-多.doc";
                    rowNum = Integer.toString(lhfwdeptList.length);
                } else {
                    templateMeishouId = qymb + "-眉首.doc";
                    rowNum = "1";
                }
            }
            if (qymb.equals("会议纪要")) {
                templateMeishouId = qymb + "-眉首.doc";
                templateBanJiId = qymb + "-版记.doc";
                rowNum = "1";
            }

            String zsDept = "";
            zsDept = (String)formData.get("zsdept");
            String banjifenjie = "1";
            if ((StringUtils.isNotBlank(zsDept))
                && ((zsDept.contains("国务院")) || (zsDept.contains("中共中央")) || (zsDept.contains("中共中央办公厅")))
                && (!zsDept.contains("国务院办公厅"))) {
                banjifenjie = "1";
            }
            formData.put("yfdate", yfdate);

            Map map = new HashMap();
            map.put("NGROPINION", typeSettingInfoModel.getCheckOpinion());
            Gson gson = new Gson();
            String jdyj = gson.toJson(map);

            String docZwInstanceId = null;
            // 获取发文稿纸fileStoreId
            List<DocumentWordModel> list =
                documentWordApi.findByProcessSerialNumberAndWordType(tenantId, processSerialNumber, "发文稿纸").getData();
            if (list.size() > 0) {
                docZwInstanceId = list.get(0).getFileStoreId();
            }
            formData.put("docZwInstanceId", docZwInstanceId);

            String isHave = "0";
            if ("1".equals(typeSettingInfoModel.getIsHave())) {
                isHave = "1";
            }
            formData.put("isHave", isHave);
            formData.put("cellNum", cellNum);
            formData.put("sheetNum", sheetNum);
            formData.put("rowNum", rowNum);
            formData.put("permUpdate", permUpdate);
            formData.put("OPINION", jdyj);
            formData.put("ifHaveYj", typeSettingInfoModel.getIfHaveYj());
            formData.put("NGROPINION", typeSettingInfoModel.getCheckOpinion());

            formData.put("lhfwdeptStr", lhfwdeptStr);

            formData.put("templateMeishouId", templateMeishouId);
            formData.put("templateBanJiId", templateBanJiId);
            formData.put("configData", getQyConfig(rowNum, cellNum, sheetNum));
            formData.put("columns", Integer.valueOf(columns));
            formData.put("lhfwdeptListWithOrder", lhfwdeptListWithOrder);
            formData.put("lhfwdeptZwList", lhfwdeptZwList);
            formData.put("zwMaxLength", zwMaxLength);
            formData.put("lhfwdeptStrFull", lhfwdeptStrFull);
            formData.put("maxLength", Integer.valueOf(maxLength));
            formData.put("banjifenjie", banjifenjie);
        }
        formData.put("tingLeaderComment", tldContent);
        formData.put("reviewerComment", hgrContent);
        formData.put("leaderComment", ldContent);
        formData.put("directorComment", szContent);
        formData.put("deptLeaderComment", czContent);
        formData.put("secretLevelRecord", secretLevelRecord);
        formData.put("DT_zzfj", paperAttList);
        return Y9Result.success(formData);
    }

    // 获取联合发文部门集合
    private Map<String, Object> getLhfwdeptList(String tenantId, String[] lhfwdeptList, int columns, boolean isTing) {
        String[] lhfwdeptListTemp = new String[lhfwdeptList.length];
        System.arraycopy(lhfwdeptList, 0, lhfwdeptListTemp, 0, lhfwdeptList.length);
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            Map<String, String> map = signDeptInfoApi.findByDeptNameMax(tenantId, lhfwdeptListTemp[i]).getData();
            String deptname = map.get("DEPTNAME");
            String deptSuffix = map.get("DEPTSUFFIX");
            if (isTing) {
                if (StringUtils.isNotBlank(deptSuffix))
                    deptname = deptname + deptSuffix;
                else if ((!deptname.contains("办公厅")) && (!deptname.contains("办公室"))) {
                    deptname = deptname + "办公厅";
                }
            }
            if (StringUtils.isNotBlank(deptname)) {
                lhfwdeptListTemp[i] = deptname;
            }
        }

        int maxLength = 0;
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            if (maxLength < lhfwdeptListTemp[i].length()) {
                maxLength = lhfwdeptListTemp[i].length();
            }
        }
        if (maxLength == 9) {
            maxLength = 8;
        }
        if (columns == 3) {
            if (maxLength > 7)
                maxLength = 7;
        } else {
            if (maxLength > 9)
                columns = 2;
            else if ((0 < maxLength) && (maxLength <= 9)) {
                columns = 3;
            }
            if ((lhfwdeptListTemp.length == 2) || (lhfwdeptListTemp.length == 4)) {
                columns = 2;
            }
        }

        int remainder = lhfwdeptListTemp.length % columns;
        for (int i = 0; i < lhfwdeptListTemp.length - remainder; i = i) {
            String tempStr = lhfwdeptListTemp[i];
            lhfwdeptListTemp[i] = lhfwdeptListTemp[(i + columns - 1)];
            lhfwdeptListTemp[(i + columns - 1)] = tempStr;
            i += columns;
        }
        if (remainder > 1) {
            String tempStr = lhfwdeptListTemp[(lhfwdeptListTemp.length - remainder)];
            lhfwdeptListTemp[(lhfwdeptListTemp.length - remainder)] = lhfwdeptListTemp[(lhfwdeptListTemp.length - 1)];
            lhfwdeptListTemp[(lhfwdeptListTemp.length - 1)] = tempStr;
        }

        String lhfwdeptListStr = "";
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            if (i != lhfwdeptListTemp.length - 1)
                lhfwdeptListStr = lhfwdeptListStr + lhfwdeptListTemp[i] + ",";
            else {
                lhfwdeptListStr = lhfwdeptListStr + lhfwdeptListTemp[i];
            }
        }

        Map mapList = new HashMap();
        mapList.put("lhfwdeptListWithOrder", lhfwdeptListStr);
        mapList.put("lhfwdeptList", lhfwdeptListTemp);
        mapList.put("columns", Integer.valueOf(columns));
        mapList.put("maxLength", Integer.valueOf(maxLength));
        return mapList;
    }

    // 获取联合发文部门字符集-厅
    private String getLhfwdeptStrTing(String lhfwdeptStr) {
        String[] lhfwdeptListTemp = new String[0];

        if (lhfwdeptStr != null) {
            lhfwdeptListTemp = lhfwdeptStr.split("\\,");
        }
        String lhfwdeptStrTemp = "";
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            if (i != lhfwdeptListTemp.length - 1)
                lhfwdeptStrTemp = lhfwdeptStrTemp + lhfwdeptListTemp[i] + ",";
            else {
                lhfwdeptStrTemp = lhfwdeptStrTemp + lhfwdeptListTemp[i];
            }
        }
        return lhfwdeptStrTemp;
    }

    // 获取联合发文正文列表
    private Map<String, Object> getLhfwdeptZwList(String tenantId, String[] lhfwdeptList) {
        String lhfwdeptZwList = "";
        String[] lhfwdeptListTemp = new String[lhfwdeptList.length];
        System.arraycopy(lhfwdeptList, 0, lhfwdeptListTemp, 0, lhfwdeptList.length);
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            Map<String, String> map = signDeptInfoApi.findByDeptNameMax(tenantId, lhfwdeptListTemp[i]).getData();
            String ldcw = map.get("ldcw");
            if (StringUtils.isNotBlank(ldcw)) {
                lhfwdeptListTemp[i] = (lhfwdeptListTemp[i] + ldcw);
            }
        }

        int maxLength = 0;
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            if (maxLength < lhfwdeptListTemp[i].length()) {
                maxLength = lhfwdeptListTemp[i].length();
            }
        }
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            if (i != lhfwdeptListTemp.length - 1)
                lhfwdeptZwList = lhfwdeptZwList + lhfwdeptListTemp[i] + ",";
            else {
                lhfwdeptZwList = lhfwdeptZwList + lhfwdeptListTemp[i];
            }
        }

        Map mapList = new HashMap();
        mapList.put("lhfwdeptZwList", lhfwdeptZwList);
        mapList.put("maxLength", Integer.toString(maxLength));
        return mapList;
    }

    // 获取联合发文部门总称
    private String getLhfwdeptfull(String tenantId, String[] lhfwdeptList) throws Exception {
        String lhfwdeptStrFull = "";
        String[] lhfwdeptListTemp = new String[lhfwdeptList.length];
        System.arraycopy(lhfwdeptList, 0, lhfwdeptListTemp, 0, lhfwdeptList.length);
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            Map<String, String> map = signDeptInfoApi.findByDeptNameMax(tenantId, lhfwdeptListTemp[i]).getData();
            String fullDeptName = map.get("fullDeptName");
            if (StringUtils.isNotBlank(fullDeptName)) {
                lhfwdeptListTemp[i] = fullDeptName;
            }
        }
        for (int i = 0; i < lhfwdeptListTemp.length; i++) {
            if (i != lhfwdeptListTemp.length - 1)
                lhfwdeptStrFull = lhfwdeptStrFull + lhfwdeptListTemp[i] + ",";
            else {
                lhfwdeptStrFull = lhfwdeptStrFull + lhfwdeptListTemp[i];
            }
        }

        return lhfwdeptStrFull;
    }

    /**
     * 清样生成二维码(每次调用生成新的二维码)
     *
     * @param processSerialNumber
     * @param tenantId
     * @param request
     * @param response
     */
    @RequestMapping("/getQYTmhPicture")
    public void getQYTmhPicture(@RequestParam String processSerialNumber, @RequestParam String tenantId,
        HttpServletRequest request, HttpServletResponse response) {
        String tmh = "";
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "select * from y9_form_fw where guid = '" + processSerialNumber + "'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list != null && list.size() > 0) {
                byte[] bytes = htkyService.getQYTmhPicture(list.get(0));
                tmh = list.get(0).get("tmh") == null ? "" : list.get(0).get("tmh").toString();
                if (StringUtils.isNotBlank(tmh)) {
                    TmhPictureModel tmhPictureModel = new TmhPictureModel();
                    tmhPictureModel.setProcessSerialNumber(processSerialNumber);
                    tmhPictureModel.setTmh(tmh);
                    tmhPictureModel.setTmhType("QY");
                    tmhPictureModel.setPicture(bytes);
                    tmhPictureModel.setUpdateTime(sdf1.format(new Date()));
                    tmhPictureModel.setFileStoreId("");
                    tmhPictureModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    tmhPictureApi.saveOrUpdate(tenantId, tmhPictureModel);
                    LOGGER.info("清样生成二维码的条码号" + tmh);
                    String filename = tmh + ".jpg";
                    if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                        filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");// 火狐浏览器
                    } else {
                        filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
                    }
                    OutputStream out = response.getOutputStream();
                    response.reset();
                    response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
                    response.setHeader("Content-type", "text/html;charset=UTF-8");
                    response.setContentType("application/octet-stream");
                    out.write(bytes);
                    out.flush();
                    out.close();
                }
            }
        } catch (Exception e) {
            LOGGER.info("清样生成二维码错误的条码号" + tmh);
            e.printStackTrace();
        }
    }

    // 获取excel配置
    private Map<String, String> getQyConfig(String rowNum, String cellNum, String sheetNum) {
        Map configMap = getConfigByExcel(rowNum, cellNum, sheetNum);
        return configMap;
    }

    /**
     * 获取条形码图片
     *
     * @param processSerialNumber 流程编号id
     * @param tenantId 租户id
     * @param request
     * @param response
     */
    @RequestMapping("/getTmhPicture")
    public void getTmhPicture(@RequestParam String processSerialNumber, @RequestParam String tenantId,
        HttpServletRequest request, HttpServletResponse response) {
        String tmh = "";
        String filename = "";
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "select * from y9_form_fw where guid = '" + processSerialNumber + "'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list != null && list.size() > 0) {
                tmh = list.get(0).get("tmh") == null ? "" : list.get(0).get("tmh").toString();
                if (StringUtils.isNotBlank(tmh)) {
                    byte[] bytes = htkyService.getTmhPicture(tmh);
                    TmhPictureModel tmhPictureModel = new TmhPictureModel();
                    tmhPictureModel.setProcessSerialNumber(processSerialNumber);
                    tmhPictureModel.setTmh(tmh);
                    tmhPictureModel.setTmhType("TMH");
                    tmhPictureModel.setPicture(bytes);
                    tmhPictureModel.setUpdateTime(sdf.format(new Date()));
                    tmhPictureModel.setFileStoreId("");
                    tmhPictureModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    tmhPictureApi.saveOrUpdate(tenantId, tmhPictureModel);
                    LOGGER.info("需要生成图片的条码号：" + tmh);
                    filename = tmh + ".jpg";
                    if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                        filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");// 火狐浏览器
                    } else {
                        filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
                    }
                    OutputStream out = response.getOutputStream();
                    response.reset();
                    response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
                    response.setHeader("Content-type", "text/html;charset=UTF-8");
                    response.setContentType("application/octet-stream");
                    out.write(bytes);
                    out.flush();
                    out.close();
                }
            }
        } catch (Exception e) {
            LOGGER.info("生成或下载条码有问题的tmh:" + tmh);
            e.printStackTrace();
        }
    }

    /**
     * 打开正文
     *
     * @param fileStoreId 文件存储id
     * @param response 响应
     * @param request 请求
     */
    @RequestMapping(value = "/openDoc")
    public void openDoc(@RequestParam String fileStoreId, HttpServletResponse response, HttpServletRequest request) {
        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            String fileName = y9FileStore.getFileName();
            if (agent.contains("Firefox")) {
                Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8));
            } else {
                fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            out = response.getOutputStream();
            byte[] buf = y9FileStoreService.downloadFileToBytes(fileStoreId);
            IOUtils.write(buf, out);
        } catch (Exception e) {
            LOGGER.error("打开正文失败", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭流失败", e);
            }
        }
    }

    /**
     * 保存合并文件
     *
     * @param wordType 文档类型,1为合并文件,2为合并版式文件
     * @param fileType 文件类型,.doc,.docx,.wps,.ofd等
     * @param listType 列表类型,1为附件合并,2为文件合并
     * @param processSerialNumber 流程编号，listType为2时为空
     * @param sourceFileId 版式文件源id，wordType为2,版式文件时不为空，传源合并文件id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param file 文件
     * @param request 请求
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveMergeFile")
    public Y9Result<Object> saveMergeFile(@RequestParam String wordType, @RequestParam String fileType,
        @RequestParam String listType, @RequestParam(required = false) String sourceFileId,
        @RequestParam(required = false) String processSerialNumber, @RequestParam String tenantId,
        @RequestParam String userId, @RequestParam MultipartFile file, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        MultipartFile multipartFile = null;
        if (file != null) {
            multipartFile = file;
        }
        if (multipartFile == null) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            multipartFile = multipartRequest.getFile("currentDoc");
        }
        try {
            String newprocessSerialNumber = processSerialNumber;
            if (StringUtils.isBlank(processSerialNumber)) {
                newprocessSerialNumber = userId;
            }
            String fileName = "合并文件" + fileType;
            String id = Y9IdGenerator.genId(IdType.SNOWFLAKE);

            // *****************文件名处理
            if (StringUtils.isNotBlank(sourceFileId)) {// 合并文件转ofd文件
                List<MergeFileModel> mlist = mergeFileApi.getOfdFile(tenantId, sourceFileId).getData();
                if (mlist.size() > 0) {// 判断源合并文件是否已经转过ofd文件，如已经转过，则更新ofd文件
                    id = mlist.get(0).getId();
                }
                // 使用源合并文件名拼接文件类型
                MergeFileModel mergeFileModel = mergeFileApi.getMergeFile(tenantId, sourceFileId).getData();
                fileName = mergeFileModel.getFileName().split(".")[0] + fileType;
            } else {// 合并文件
                List<MergeFileModel> mergeFileList = mergeFileApi
                    .getMergeFileList(tenantId, person.getId(), processSerialNumber, listType, wordType).getData();
                if (mergeFileList.size() > 0) {
                    List<MergeFileModel> list1 =
                        mergeFileList.stream().filter(mergeFileModel -> mergeFileModel.getFileName().contains("合并文件"))
                            .collect(Collectors.toList());
                    String oldfileName = "";
                    if (list1.size() > 0) {
                        oldfileName = list1.get(list1.size() - 1).getFileName();
                    }
                    if (oldfileName.contains("(")) {// 获取最后的文件名 +1
                        oldfileName.replace("(", "&").replace(")", "&");
                        String index = oldfileName.split("&")[1];
                        fileName = "合并文件(" + (Integer.parseInt(index) + 1) + ")" + fileType;
                    } else {
                        fileName = "合并文件(1)" + fileType;
                    }
                }
            }
            // *****************文件名处理

            String fullPath =
                Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "mergeFile", newprocessSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, fileName);
            LOGGER.info("=======fileType=======:" + fileType + "=========y9FileStoreId========:" + y9FileStore.getId());
            MergeFileModel mergeFileModel = new MergeFileModel();
            mergeFileModel.setId(id);
            mergeFileModel.setFileName(fileName);
            mergeFileModel.setListType(listType);
            mergeFileModel.setFileStoreId(y9FileStore.getId());
            mergeFileModel.setProcessSerialNumber(StringUtils.isBlank(processSerialNumber) ? "" : processSerialNumber);
            mergeFileModel.setPersonName(person.getName());
            mergeFileModel.setPersonId(person.getId());
            mergeFileModel.setFileType(wordType);// 存wordType
            mergeFileModel.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            mergeFileModel.setSourceFileId(sourceFileId);
            return mergeFileApi.saveMergeFile(tenantId, mergeFileModel);
        } catch (Exception e) {
            LOGGER.error("保存合并文件失败", e);
        }
        return Y9Result.failure("保存合并文件失败");
    }

    /**
     * 保存打印日志
     *
     * @param processSerialNumber 流程编号id
     * @param actionContent 动作内容
     * @param actionType 动作类型
     * @param tenantId 租户id
     * @param userId 用户id
     * @param request 请求
     * @return {@link Y9Result<Object>}
     */
    @RequestMapping(value = "/savePrintLog")
    public Y9Result<Object> savePrintLog(@RequestParam String processSerialNumber, @RequestParam String actionContent,
        @RequestParam String actionType, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PrintLogModel printLog = new PrintLogModel();
        printLog.setProcessSerialNumber(processSerialNumber);
        printLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        printLog.setPrintTime(sdf.format(new Date()));
        printLog.setUserId(userId);
        printLog.setUserName(person.getName());
        printLog.setActionContent(actionContent);
        printLog.setActionType(actionType);
        printLog.setIp(Y9Context.getIpAddr(request));
        printLog.setDeptId(person.getParentId());
        return printLogApi.savePrintLog(Y9LoginUserHolder.getTenantId(), printLog);
    }

    /**
     * 获取办件正文信息
     *
     * @param processSerialNumber 流程编号
     * @param wordType 文档类型
     * @param positionId 岗位id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping("/showWord")
    public Y9Result<DocumentWordModel> showWord(@RequestParam String processSerialNumber, @RequestParam String wordType,
        @RequestParam String itemId, @RequestParam(required = false) String signId,
        @RequestParam(required = false) String positionId, @RequestParam String tenantId, @RequestParam String userId) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
            OrgUnit currentBureau = orgUnitApi.getBureau(tenantId, orgUnit.getParentId()).getData();
            DocumentWordModel wordInfo;
            if (StringUtils.isNotBlank(signId)) {// 意见签注
                SignDeptDetailModel sign = signDeptDetailApi.findById(tenantId, signId).getData();
                wordInfo = new DocumentWordModel();
                wordInfo.setId(sign.getId());
                wordInfo.setFileStoreId(sign.getFileStoreId());
                wordInfo.setCurrentBureauId(currentBureau != null ? currentBureau.getId() : "");
                wordInfo.setCurrentUserName(person.getName());
                return Y9Result.success(wordInfo, "获取信息成功");
            }
            List<DocumentWordModel> list =
                documentWordApi.findByProcessSerialNumberAndWordType(tenantId, processSerialNumber, wordType).getData();
            if (list != null && list.size() > 0) {
                wordInfo = list.get(0);
            } else {
                // 新文档，获取正文绑定模板
                String fileStoreId = wordTemplateApi.getWordTemplateBind(tenantId, itemId, wordType).getData();
                wordInfo = new DocumentWordModel();
                wordInfo.setFileStoreId(fileStoreId);
            }
            wordInfo.setCurrentBureauId(currentBureau != null ? currentBureau.getId() : "");
            wordInfo.setCurrentUserName(person.getName());
            return Y9Result.success(wordInfo, "获取信息成功");
        } catch (Exception e) {
            LOGGER.error("获取信息失败", e);
        }
        return Y9Result.failure("获取信息失败");
    }

    /**
     * 获取套红模板列表
     *
     * @param currentBureauGuid 委办局id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @return List<Map < String, Object>>
     */
    @RequestMapping(value = "/taoHongTemplateList")
    public List<TaoHongTemplateModel> taoHongTemplateList(@RequestParam(required = false) String currentBureauGuid,
        @RequestParam String tenantId, @RequestParam(required = false) String userId,
        @RequestParam(required = false) String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (StringUtils.isBlank(currentBureauGuid) && StringUtils.isNotBlank(positionId)) {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
            currentBureauGuid = orgUnit.getParentId();
        }
        return transactionWordApi.taoHongTemplateList(tenantId, userId, currentBureauGuid).getData();
    }

    /**
     * 保存清样文件
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param id 清样文件id
     * @param request 请求
     * @return Y9Result<DocumentWordModel>
     */
    @PostMapping(value = "/uploadQingyang")
    public Y9Result<String> uploadQingyang(@RequestParam String fileType, @RequestParam String processSerialNumber,
        @RequestParam String id, @RequestParam String tenantId, @RequestParam String userId,
        @RequestParam MultipartFile file, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        if (multipartFile == null) {
            multipartFile = file;
        }
        try {
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, "清样文件" + fileType);
            LOGGER.info("==========fileType==========:" + fileType + "=============y9FileStoreId=============:"
                + y9FileStore.getId());
            Boolean flag = typeSettingInfoApi.updateFile(tenantId, id, y9FileStore.getId(), fileType).isSuccess();
            if (flag) {
                return Y9Result.success(y9FileStore.getId());
            }
        } catch (Exception e) {
            LOGGER.error("保存正文失败", e);
        }
        return Y9Result.failure("保存正文失败");
    }

    /**
     * 办件保存正文
     *
     * @param fileType 文件类型
     * @param type 数据类型
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param wordType 文档类别
     * @param request 请求
     * @return Y9Result<DocumentWordModel>
     */
    @PostMapping(value = "/uploadWord")
    public Y9Result<DocumentWordModel> uploadWord(@RequestParam String fileType, @RequestParam Integer type,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam String wordType, @RequestParam String tenantId,
        @RequestParam String userId, @RequestParam(required = false) String signId,
        @RequestParam(required = false) MultipartFile file, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        MultipartFile multipartFile = null;
        if (file != null) {
            multipartFile = file;
        }
        if (multipartFile == null) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            multipartFile = multipartRequest.getFile("currentDoc");
        }
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, wordType + fileType);
            if (StringUtils.isNotBlank(signId)) {// 会签意见保存
                Y9Result res =
                    signDeptDetailApi.updateFileStoreId(Y9LoginUserHolder.getTenantId(), signId, y9FileStore.getId());
                if (!res.isSuccess()) {
                    return Y9Result.failure("保存失败");
                }
                DocumentWordModel model = new DocumentWordModel();
                model.setId(Y9IdGenerator.genId());
                model.setFileType(fileType);
                model.setFileName(wordType + fileType);
                model.setFileSize(y9FileStore.getDisplayFileSize());
                model.setUserId(userId);
                model.setUserName(person.getName());
                model.setType(type);
                model.setSaveDate(sdf.format(new Date()));
                model.setProcessSerialNumber(processSerialNumber);
                model.setProcessInstanceId(processInstanceId);
                model.setWordType(wordType);
                model.setTaskId(taskId);
                model.setUpdateDate(sdf.format(new Date()));
                model.setFileStoreId(y9FileStore.getId());
                return Y9Result.success(model);
            }
            DocumentWordModel model = new DocumentWordModel();
            model.setId(Y9IdGenerator.genId());
            model.setFileType(fileType);
            model.setFileName(wordType + fileType);
            model.setFileSize(y9FileStore.getDisplayFileSize());
            model.setUserId(userId);
            model.setUserName(person.getName());
            model.setType(type);
            model.setSaveDate(sdf.format(new Date()));
            model.setProcessSerialNumber(processSerialNumber);
            model.setProcessInstanceId(processInstanceId);
            model.setWordType(wordType);
            model.setTaskId(taskId);
            model.setUpdateDate(sdf.format(new Date()));
            model.setFileStoreId(y9FileStore.getId());
            return documentWordApi.saveWord(tenantId, model);
        } catch (Exception e) {
            LOGGER.error("上传正文失败", e);
        }
        return Y9Result.failure("上传正文失败");
    }

}
