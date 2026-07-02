package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.Y9WordHistoryModel;
import net.risesoft.model.itemadmin.Y9WordInfo;
import net.risesoft.model.itemadmin.Y9WordModel;
import net.risesoft.pojo.Y9Result;

/**
 * 正文接口管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Y9WordApi {

    /**
     * 根据流程编号删除正文，同时删除文件系统的文件
     *
     * @param processSerialNumbers 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/delBatchByProcessSerialNumbers")
    Y9Result<Object> delBatchByProcessSerialNumbers(@RequestBody List<String> processSerialNumbers);

    /**
     * 删除撤销PDF文件
     *
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/deleteByIsTaoHong")
    Y9Result<Object> deleteByIsTaoHong(@RequestParam String processSerialNumber, @RequestParam String isTaoHong);

    /**
     * 获取正文文件信息（数据传输）
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Y9WordModel>} 通用请求返回对象 - data 是正文文件信息
     * @since 9.6.6
     */
    @GetMapping(value = "/exchangeFindWordByProcessSerialNumber")
    Y9Result<Y9WordModel> exchangeFindWordByProcessSerialNumber(@RequestParam String processSerialNumber);

    /**
     * 打开历史文件
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Y9WordHistoryModel>} 通用请求返回对象 - data 是历史正文文件信息对象
     * @since 9.6.6
     */
    @GetMapping(value = "/findHistoryVersionDoc")
    Y9Result<Y9WordHistoryModel> findHistoryVersionDoc(@RequestParam String taskId);

    /**
     * 获取正文文件信息
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Y9WordModel>} 通用请求返回对象 - data 是正文文件信息
     * @since 9.6.6
     */
    @GetMapping(value = "/findWordByProcessSerialNumber")
    Y9Result<Y9WordModel> findWordByProcessSerialNumber(@RequestParam String processSerialNumber);

    /**
     * 获取正文列表
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<Y9WordModel>>} 通用请求返回对象 - data 是正文文件信息列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getWordList")
    Y9Result<List<Y9WordModel>> getWordList(@RequestParam String processSerialNumber);

    /**
     * 保存公文传输转入工作流的正文信息
     *
     * @param docJson 正文json信息
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是保存是否成功的信息
     * @since 9.6.6
     */
    @PostMapping(value = "/importY9Word")
    Y9Result<Boolean> importY9Word(@RequestParam String docJson, @RequestParam String processSerialNumber);

    /**
     * 打开正文
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param bindValue 绑定值
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是正文文件地址
     * @since 9.6.6
     */
    @GetMapping(value = "/openDocument")
    Y9Result<String> openDocument(@RequestParam String processSerialNumber, @RequestParam String itemId,
        @RequestParam String bindValue);

    /**
     * 根据流程编号打开正文
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是正文文件地址
     * @since 9.6.6
     */
    @GetMapping(value = "/openDocumentByProcessSerialNumber")
    Y9Result<String> openDocumentByProcessSerialNumber(@RequestParam String processSerialNumber);

    /**
     * 打开套红模板
     *
     * @param templateGuid 模板id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是套红文件地址
     * @since 9.6.6
     */
    @GetMapping(value = "/openDocumentTemplate")
    Y9Result<String> openDocumentTemplate(@RequestParam String templateGuid);

    /**
     * 打开PDF
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是PDF文件地址
     * @since 9.6.6
     */
    @GetMapping(value = "/openPdf")
    Y9Result<String> openPdf(@RequestParam String processSerialNumber);

    /**
     * 打开撤销PDF后的正文
     *
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是PDF文件地址
     * @since 9.6.6
     */
    @GetMapping(value = "/openRevokePDFAfterDocument")
    Y9Result<String> openRevokePdfAfterDocument(@RequestParam String processSerialNumber,
        @RequestParam String isTaoHong);

    /**
     * 选择套红
     *
     * @param activitiUser activitiUser
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是当前人员的委办局GUID
     * @since 9.6.6
     */
    @GetMapping(value = "/openTaoHong")
    Y9Result<String> openTaoHong(@RequestParam String activitiUser);

    /**
     * 获取正文
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param itembox 办件状态，{@link net.risesoft.enums.ItemBoxTypeEnum}
     * @param taskId 任务id
     * @param bindValue 绑定值
     * @return {@code Y9Result<WordInfo>} 通用请求返回对象 - data 是正文详情
     * @since 9.6.6
     */
    @GetMapping(value = "/showWord")
    Y9Result<Y9WordInfo> showWord(@RequestParam String processSerialNumber, @RequestParam String itemId,
        @RequestParam(required = false) String itembox, @RequestParam(required = false) String taskId,
        @RequestParam String bindValue);

    /**
     * 获取套红模板列表
     *
     * @param currentBureauGuid 委办局id
     * @return {@code Y9Result<List<TaoHongTemplateModel>>} 通用请求返回对象 - data 是套红模板列表
     * @since 9.6.6
     */
    @GetMapping(value = "/taoHongTemplateList")
    Y9Result<List<TaoHongTemplateModel>> taoHongTemplateList(@RequestParam String currentBureauGuid);

    /**
     * 草稿箱保存正文
     *
     * @param documentTitle 文档标题
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param isTaoHong 是否套红
     * @param docCategory 文档类别
     * @param taskId 任务id
     * @param fileSizeString 文件大小
     * @param fileStoreId 文件id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是保存是否成功的信息
     * @since 9.6.6
     */
    @PostMapping(value = "/uploadWord")
    Y9Result<Boolean> uploadWord(@RequestParam String documentTitle, @RequestParam String fileType,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String isTaoHong,
        @RequestParam(required = false) String docCategory, @RequestParam(required = false) String taskId,
        @RequestParam String fileSizeString, @RequestParam String fileStoreId);

    /**
     * 下载正文
     *
     * @param id 正文id
     * @return {@code Y9Result<WordInfo>} 通用请求返回对象 - data 是正文详情
     * @since 9.6.6
     */
    @GetMapping(value = "/wordDownload")
    Y9Result<Y9WordModel> wordDownload(@RequestParam String id);
}
