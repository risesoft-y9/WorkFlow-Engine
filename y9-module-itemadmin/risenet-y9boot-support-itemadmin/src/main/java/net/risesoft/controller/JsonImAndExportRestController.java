package net.risesoft.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.button.CommonButton;
import net.risesoft.entity.button.SendButton;
import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;
import net.risesoft.entity.interfaceinfo.InterfaceInfo;
import net.risesoft.entity.interfaceinfo.InterfaceRequestParams;
import net.risesoft.entity.interfaceinfo.InterfaceResponseParams;
import net.risesoft.entity.interfaceinfo.ItemInterfaceParamsBind;
import net.risesoft.entity.opinion.OpinionFrame;
import net.risesoft.entity.view.ItemViewConf;
import net.risesoft.model.InterfaceJsonModel;
import net.risesoft.model.OptionClassJsonModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CommonButtonService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.InterfaceService;
import net.risesoft.service.OpinionFrameService;
import net.risesoft.service.SendButtonService;
import net.risesoft.service.config.ItemInterfaceParamsBindService;
import net.risesoft.service.config.ItemViewConfService;
import net.risesoft.service.form.Y9FormOptionClassService;

/**
 * 接口信息
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/json", produces = MediaType.APPLICATION_JSON_VALUE)
public class JsonImAndExportRestController {

    private final InterfaceService interfaceService;
    private final ItemInterfaceParamsBindService itemInterfaceParamsBindService;
    private final ItemViewConfService itemViewConfService;
    private final DynamicRoleService dynamicRoleService;
    private final OpinionFrameService opinionFrameService;
    private final CommonButtonService commonButtonService;
    private final SendButtonService sendButtonService;
    private final Y9FormOptionClassService y9FormOptionClassService;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    /**
     * 导出 JSON 数据
     *
     * @param id 接口id(注意事项：id为空时，导出所有接口信息，事项配置导出接口参数：id(接口id:事项id))
     * @return JSON 数据
     */
    @GetMapping(value = "/exportJson")
    public ResponseEntity<byte[]> exportJson(@RequestParam(required = false) String id, @RequestParam String type) {

        Object dataToExport;
        String filename = "export";
        switch (type) {
            case "interface": // 接口信息
                dataToExport = buildInterfaceExportData(id);
                filename = ((List<InterfaceJsonModel>)dataToExport).get(0).getInterfaceInfo().getInterfaceName();
                break;
            case "interfaceAll": // 全部接口信息
                dataToExport = buildInterfaceExportData("");
                filename = "所有接口数据" + sdf.format(new Date());
                break;
            case "interfaceParam": // 接口信息
                dataToExport = buildInterfaceParamBindExportData(id);
                filename = "接口参数" + sdf.format(new Date());
                break;
            case "itemViewConfig": // 事项视图配置
                dataToExport = buildItemViewConfigExportData(id);
                filename = "视图配置" + sdf.format(new Date());
                break;
            case "dynamicRoleConfig": // 动态角色配置
                dataToExport = buildDynamicRoleConfigExportData();
                filename = "动态角色配置" + sdf.format(new Date());
                break;
            case "opinionFrame":
                dataToExport = buildOpinionFrameExportData();
                filename = "意见框配置" + sdf.format(new Date());
                break;
            case "commonButton":
                dataToExport = buildCommonButtonExportData();
                filename = "普通按钮配置" + sdf.format(new Date());
                break;
            case "sendButton":
                dataToExport = buildSendButtonExportData();
                filename = "发送按钮配置" + sdf.format(new Date());
                break;
            case "optionClassAll":
                dataToExport = buildOptionClassExportData("");
                filename = "数字字典所有配置" + sdf.format(new Date());
                break;
            case "optionClass":
                dataToExport = buildOptionClassExportData(id);
                filename = "数字字典【" + id + "】配置" + sdf.format(new Date());
                break;
            // 可扩展其他类型
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("不支持的导出类型".getBytes());
        }
        return buildJsonDownloadResponse(dataToExport, filename);
    }

    /**
     * 获取数字字典数据
     *
     * @param id
     * @return
     */
    private List<OptionClassJsonModel> buildOptionClassExportData(String id) {
        List<Y9FormOptionClass> optionClassList = new ArrayList<>();
        if (StringUtils.isNotBlank(id)) {
            optionClassList.add(y9FormOptionClassService.findByType(id));
        } else {
            optionClassList = y9FormOptionClassService.listAllOptionClass(); // 查询所有接口
        }
        return optionClassList.stream().map(y9FormOptionClass -> {
            List<Y9FormOptionValue> optionValueList =
                y9FormOptionClassService.listByTypeOrderByTabIndexAsc(y9FormOptionClass.getType());
            OptionClassJsonModel exportData = new OptionClassJsonModel();
            exportData.setOptionClass(y9FormOptionClass);
            exportData.setFormOptionValueList(optionValueList);
            return exportData;
        }).collect(Collectors.toList());
    }

    /**
     * 获取普通按钮配置数据
     * 
     * @return
     */
    private List<CommonButton> buildCommonButtonExportData() {
        return commonButtonService.listAll();
    }

    /**
     * 获取发送按钮配置数据
     *
     * @return
     */
    private List<SendButton> buildSendButtonExportData() {
        return sendButtonService.listAll();
    }

    /**
     * 获取意见框数据
     *
     * @return
     */
    private List<OpinionFrame> buildOpinionFrameExportData() {
        return opinionFrameService.listAll();
    }

    /**
     * 获取动态角色数据
     * 
     * @return
     */
    private List<DynamicRole> buildDynamicRoleConfigExportData() {
        return dynamicRoleService.listAll();
    }

    /**
     * 获取视图配置数据
     * 
     * @param id
     * @return
     */
    private List<ItemViewConf> buildItemViewConfigExportData(String id) {
        return itemViewConfService.listByItemId(id);
    }

    /**
     * 获取接口以及接口参数数据
     * 
     * @param id
     * @return
     */
    private List<InterfaceJsonModel> buildInterfaceExportData(String id) {
        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();
        if (StringUtils.isNotBlank(id)) {
            interfaceInfoList.add(interfaceService.findById(id));
        } else {
            interfaceInfoList = interfaceService.findAll(); // 查询所有接口
        }
        return interfaceInfoList.stream().map(info -> {
            List<InterfaceRequestParams> requestParamsList =
                interfaceService.listRequestParams(null, null, info.getId());
            List<InterfaceResponseParams> responseParamsList =
                interfaceService.listResponseParamsByNameAndId(null, info.getId());

            InterfaceJsonModel exportData = new InterfaceJsonModel();
            exportData.setInterfaceInfo(info);
            exportData.setRequestParamsList(requestParamsList);
            exportData.setResponseParamsList(responseParamsList);
            return exportData;
        }).collect(Collectors.toList());
    }

    /**
     * 导出当前事项绑定的接口信息下参数
     *
     * @param id 接口id
     * @return
     */
    private List<ItemInterfaceParamsBind> buildInterfaceParamBindExportData(String id) {
        String itemId = id.split(":")[0];
        String interfaceId = id.split(":")[1];
        return itemInterfaceParamsBindService.listByItemIdAndInterfaceId(itemId, interfaceId);
    }

    /**
     * 导出json
     * 
     * @param data
     * @param baseFilename
     * @return
     */
    protected ResponseEntity<byte[]> buildJsonDownloadResponse(Object data, String baseFilename) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String jsonStr = objectMapper.writeValueAsString(data);
            byte[] jsonData = jsonStr.getBytes(StandardCharsets.UTF_8);

            String filename = URLEncoder.encode(baseFilename, StandardCharsets.UTF_8).replace("+", "%20");

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + filename + ".json; filename*=UTF-8''" + filename + ".json")
                .body(jsonData);
        } catch (Exception e) {
            LOGGER.error("导出 JSON 失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导出失败".getBytes());
        }
    }

    /**
     * 导入接口信息及参数
     *
     * @param file 导入文件
     * @param id (type:interfaceParam,事项配置导入接口参数：id(接口id:事项id)
     * @return
     */
    @PostMapping(value = "/importJson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Y9Result<String> importJson(MultipartFile file, @RequestParam(required = false) String id,
        @RequestParam String type) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            switch (type) {
                case "interfaceAll": // 接口信息以及接口参数导入
                    importInterfaceFromJson(file, objectMapper);
                    break;
                case "interfaceParam": // 接口参数导入
                    importInterfaceParamFromJson(file, id, objectMapper);
                    break;
                case "itemViewConfig": // 事项视图配置
                    importItemViewFormJson(file, id, objectMapper);
                    break;
                case "dynamicRoleConfig": // 动态角色配置
                    importDynamicRoleFormJson(file, objectMapper);
                    break;
                case "opinionFrame": // 意见框配置
                    importOpinionFrameFormJson(file, objectMapper);
                    break;
                case "commonButton": // 普通按钮配置
                    importCommonButtonFormJson(file, objectMapper);
                    break;
                case "sendButton": // 发送按钮配置
                    importSendButtonFormJson(file, objectMapper);
                    break;
                case "optionClassAll":
                    importOptionClassFormJson(file, objectMapper);
                    break;
                // 可扩展其他类型
                default:
                    return Y9Result.failure("不支持的导出类型");
            }
            return Y9Result.successMsg("导入成功");
        } catch (Exception e) {
            return Y9Result.failure("导入失败：" + e.getMessage());
        }
    }

    private void importOptionClassFormJson(MultipartFile file, ObjectMapper objectMapper) {
        try {
            // 反序列化为 List<OptionClassExportModel>
            List<OptionClassJsonModel> exportDataList = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, OptionClassJsonModel.class));

            for (OptionClassJsonModel exportData : exportDataList) {
                Y9FormOptionClass optionClass = exportData.getOptionClass();
                List<Y9FormOptionValue> optionValueList = exportData.getFormOptionValueList();

                Y9FormOptionClass Y9FormOptionClass = y9FormOptionClassService.saveOptionClass(optionClass).getData();
                if (null != Y9FormOptionClass) {
                    for (Y9FormOptionValue Y9FormOptionValue : optionValueList) {
                        Y9FormOptionValue.setId(null);
                        Y9FormOptionValue.setType(Y9FormOptionClass.getType());
                        y9FormOptionClassService.saveOptionValue(Y9FormOptionValue);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("导入数据异常：" + e.getMessage());
        }
    }

    /**
     * 导入普通按钮数据
     *
     * @param file
     * @param objectMapper
     */
    private void importCommonButtonFormJson(MultipartFile file, ObjectMapper objectMapper) {
        try {
            List<CommonButton> exportDataList = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CommonButton.class));
            for (CommonButton commonButton : exportDataList) {
                commonButton.setId(null);
                commonButton.setCustomId(commonButton.getCustomId().replace("common_", ""));
                commonButtonService.saveOrUpdate(commonButton);
            }
        } catch (Exception e) {
            LOGGER.error("导入数据异常：" + e.getMessage());
        }
    }

    /**
     * 导入发送按钮数据
     *
     * @param file
     * @param objectMapper
     */
    private void importSendButtonFormJson(MultipartFile file, ObjectMapper objectMapper) {
        try {
            List<SendButton> exportDataList = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, SendButton.class));
            for (SendButton sendButton : exportDataList) {
                sendButton.setId(null);
                sendButton.setCustomId(sendButton.getCustomId().replace("send_", ""));
                sendButtonService.saveOrUpdate(sendButton);
            }
        } catch (Exception e) {
            LOGGER.error("导入数据异常：" + e.getMessage());
        }
    }

    /**
     * 导入意见框数据
     * 
     * @param file
     * @param objectMapper
     */
    private void importOpinionFrameFormJson(MultipartFile file, ObjectMapper objectMapper) {
        try {
            List<OpinionFrame> exportDataList = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, OpinionFrame.class));
            for (OpinionFrame opinionFrame : exportDataList) {
                OpinionFrame existOpinionFrame = opinionFrameService.getByMark(opinionFrame.getMark());
                if (null == existOpinionFrame) {
                    opinionFrame.setId(null);
                    opinionFrameService.saveOrUpdate(opinionFrame);
                }
            }
        } catch (Exception e) {
            LOGGER.error("导入数据异常：" + e.getMessage());
        }
    }

    /**
     * 导入动态角色配置
     *
     * @param file
     * @param objectMapper
     */
    private void importDynamicRoleFormJson(MultipartFile file, ObjectMapper objectMapper) {
        try {
            // 反序列化为 List<DynamicRole>
            List<DynamicRole> exportDataList = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, DynamicRole.class));
            for (DynamicRole dynamicRole : exportDataList) {
                DynamicRole role =
                    dynamicRoleService.findByNameAndClassPath(dynamicRole.getName(), dynamicRole.getClassPath());
                if (null == role) {
                    dynamicRole.setId(null);
                    dynamicRoleService.saveOrUpdate(dynamicRole);
                }
            }
        } catch (Exception e) {
            LOGGER.error("导入数据异常：" + e.getMessage());
        }
    }

    /**
     * 解析视图配置json文件
     *
     * @param file
     * @param id
     * @param objectMapper
     */
    private void importItemViewFormJson(MultipartFile file, String id, ObjectMapper objectMapper) {
        try {
            // 反序列化为 List<ItemViewConf>
            List<ItemViewConf> exportDataList = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ItemViewConf.class));

            for (ItemViewConf conf : exportDataList) {
                // 如果当前事项的视图分类已经绑定了这个字段，将不保存。
                ItemViewConf viewConf = itemViewConfService.findByItemIdAndViewTypeAndColumnName(id, conf.getViewType(),
                    conf.getColumnName());
                if (null == viewConf) {
                    conf.setId(null);
                    conf.setItemId(id);
                    itemViewConfService.saveOrUpdate(conf);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("导入失败：" + e.getMessage(), e);
        }
    }

    /**
     * 解析导入的接口参数方法
     * 
     * @param file
     * @param id
     * @param objectMapper
     */
    private void importInterfaceParamFromJson(MultipartFile file, String id, ObjectMapper objectMapper) {
        try {
            String itemId = id.split(":")[0];
            String interfaceId = id.split(":")[1];
            // 反序列化为 List<InterfaceExportData>
            List<ItemInterfaceParamsBind> exportDataList = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ItemInterfaceParamsBind.class));

            for (ItemInterfaceParamsBind exportData : exportDataList) {
                exportData.setId(null);
                exportData.setInterfaceId(interfaceId);
                exportData.setItemId(itemId);
                itemInterfaceParamsBindService.saveBind(exportData);
            }

        } catch (IOException e) {
            throw new RuntimeException("导入失败：" + e.getMessage(), e);
        }
    }

    /**
     * 解析接口以及接口参数方法
     * 
     * @param file
     * @param objectMapper
     */
    private void importInterfaceFromJson(MultipartFile file, ObjectMapper objectMapper) {
        try {
            // 反序列化为 List<InterfaceExportData>
            List<InterfaceJsonModel> exportDataList = objectMapper.readValue(file.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, InterfaceJsonModel.class));

            for (InterfaceJsonModel exportData : exportDataList) {
                InterfaceInfo interfaceInfo = exportData.getInterfaceInfo();
                List<InterfaceRequestParams> requestParamsList = exportData.getRequestParamsList();
                List<InterfaceResponseParams> responseParamsList = exportData.getResponseParamsList();

                // 设置新ID，避免冲突
                interfaceInfo.setId(null);

                // 检查是否已存在同名接口
                List<InterfaceInfo> infoList = interfaceService.findByInterfaceName(interfaceInfo.getInterfaceName());
                if (infoList.size() > 0) {
                    interfaceInfo.setInterfaceName(interfaceInfo.getInterfaceName() + "-" + infoList.size());
                }

                InterfaceInfo savedInfo = interfaceService.saveInterfaceInfo(interfaceInfo);

                // 保存请求参数
                for (InterfaceRequestParams param : requestParamsList) {
                    param.setId(null);
                    param.setInterfaceId(savedInfo.getId());
                    interfaceService.saveRequestParams(param);
                }

                // 保存响应参数
                for (InterfaceResponseParams param : responseParamsList) {
                    param.setId(null);
                    param.setInterfaceId(savedInfo.getId());
                    interfaceService.saveResponseParams(param);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("导入失败：" + e.getMessage(), e);
        }
    }

}
