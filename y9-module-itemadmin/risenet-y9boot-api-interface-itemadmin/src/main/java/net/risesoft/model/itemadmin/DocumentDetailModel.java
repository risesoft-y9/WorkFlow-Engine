package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 流程详情数据
 *
 * @author qinman
 * @date 2024/11/01
 */
@Data
public class DocumentDetailModel implements Serializable {

    private static final long serialVersionUID = 4115564591151087066L;
    /**
     * 事项Id
     */
    private String itemId;

    /**
     * 流程编号
     */
    private String processSerialNumber;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 所属事项绑定的流程定义
     */
    private String processDefinitionKey;

    /**
     * 流程实例
     */
    private String processInstanceId;

    /**
     * 任务key
     */
    private String taskDefKey;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 当前岗位名称
     */
    private String currentUser;

    /**
     * 当前人员id
     */
    private String activitiUser;

    /**
     * 状态
     */
    private String itembox;

    /**
     * 表单列表
     */
    private List<ItemFormModel> formList;

    /**
     * 附件，正文，沟通交流页签显示
     */
    private String showOtherFlag;

    /**
     * 重定位选项
     */
    private List<Map<String, Object>> repositionMap;

    /**
     * 重定位选项json数据
     */
    private String taskDefNameJson;

    /**
     * 菜单按钮选项
     */
    private List<ItemButtonModel> buttonList;

    /**
     * 是否主办办理
     */
    private String sponsorHandle;

    /**
     * 拒签时是否是最后一个人员
     */
    private boolean isLastPerson4RefuseClaim;

    /**
     * 任务节点类型
     */
    private String multiInstance;

    /**
     * 是否需要发送下一个节点
     */
    private boolean nextNode;

    /**
     * 是否上会
     */
    private boolean meeting;

    /**
     * 启动节点key
     */
    private String startTaskDefKey;

    /**
     * 标题
     */
    private String title;

    /**
     * 启动人id
     */
    private String startor;

    /**
     * 抄送件id
     */
    private String id;

    /**
     * 抄送件状态
     */
    private Integer status;

    /**
     * 是否自定义事项
     */
    private boolean customItem;
}
