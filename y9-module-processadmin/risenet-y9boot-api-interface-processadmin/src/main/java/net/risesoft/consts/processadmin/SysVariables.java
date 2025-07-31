package net.risesoft.consts.processadmin;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class SysVariables {
    /**
     * 自定义变量名称 流程图中用到的sys_user，用来保存assignee指定的人员的guid
     */
    public static final String USER = "user";
    /**
     * 自定义变量名称 流程图中用到的sys_user，用来保存候选人组
     */
    public static final String USERS = "users";
    /**
     * 变量名称：实例总数
     */
    public static final String NR_OF_INSTANCES = "nrOfInstances";
    /**
     * 变量名称：当前还没有完成的实例
     */
    public static final String NR_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
    /**
     * 变量名称：已经完成的实例个数
     */
    public static final String NR_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";
    /**
     * 多实例任务时的循环次数，假如选择三个人发送，并行则会有三个变量分别为loopCounter=0，loopCounter=1，loopCounter=2，串行的时候，只有一个变量，刚开始loopCounter=0，办理完成一个loopCounter就加1
     */
    public static final String LOOP_COUNTER = "loopCounter";
    public static final String ELEMENT_USER = "elementUser";
    /**
     * 自定义变量名称 用来记录当前流程的前发送人的中文姓名
     */
    public static final String TASK_SENDER = "taskSender";
    /**
     * 自定义变量名称 用来记录当前流程的前发送人的guid
     */
    public static final String TASK_SENDER_ID = "taskSenderId";
    /**
     * 自定义变量名称 用来记录当前流程的前发送人的中文姓名
     */
    public static final String MAIN_SENDER = "mainSender";
    /**
     * 自定义变量名称 用来记录当前流程的前发送人的guid
     */
    public static final String MAIN_SENDER_ID = "mainSenderId";
    /**
     * 自定义变量名称 用来记录当前流程的前发送人岗位的guid
     */
    public static final String TASK_SENDER_POSITION_ID = "taskSenderPositionId";
    /**
     * 自定义变量名称 流程唯一标识，当点击新建按钮的时候就生成（此时流程还未启动），当流程启动时放入流程变量中，直到流程结束
     */
    public static final String PROCESS_SERIAL_NUMBER = "processSerialNumber";
    /**
     * 自定义变量名称 文档标题
     */
    public static final String DOCUMENT_TITLE = "documentTitle";
    public static final String ROUTE_TO_TASK_ID = "routeToTaskId";
    /**
     * 并行的时候增加参数parallelSponsor，用以标识主协办，值为主办人员guid
     */
    public static final String PARALLEL_SPONSOR = "parallelSponsor";
    /**
     * 并行
     */
    public static final String PARALLEL = "parallel";
    /**
     * 串行
     */
    public static final String SEQUENTIAL = "sequential";
    /**
     * 普通的单实例人员节点
     */
    public static final String COMMON = "common";
    /**
     * 审批事项guid
     */
    public static final String ITEM_ID = "itemId";
    /**
     * 待办是否是最新的(新产生的待办，打开过就不是最新的)
     */
    public static final String IS_NEW_TODO = "isNewTodo";
    /**
     * 待办来源 ItemBoxTypeEnum
     */
    public static final String ITEM_BOX = "itemBox";
    /**
     * 用来记录任务的办理完成的动作
     */
    public static final String ACTION_NAME = "actionName";
    /**
     * 自由流程办结角色
     */
    public static final String FREE_FLOW_END_ROLE = "freeFlowEndRole";
    /**
     * 收回
     */
    public static final String TAKEBACK = "takeBack";
    /**
     * 回退
     */
    public static final String ROLLBACK = "rollBack";
    /**
     * 拒签
     */
    public static final String REFUSE_CLAIM_ROLLBACK = "refuseClaimRollback";
    /**
     * 重定位
     */
    public static final String REPOSITION = "reposition";
    public static final String IS_PARALLEL_GATEWAY_TASK = "isParallelGatewayTask";
    public static final String TYPE = "type";
    public static final String TENANT_ID = "tenantId";
    public static final String START_EVENT = "startEvent";
    public static final String END_EVENT = "endEvent";
    public static final String GATEWAY = "Gateway";
    public static final String USER_TASK = "userTask";
    public static final String CALL_ACTIVITY = "callActivity";
    public static final String SUBPROCESS = "subProcess";
    /**
     * 优先级
     */
    public static final String PRIORITY = "priority";
    /**
     * 逗号
     */
    public static final String COMMA = ",";
    /**
     * 冒号
     */
    public static final String COLON = ":";
    /**
     * 分号
     */
    public static final String SEMICOLON = ";";
    /**
     * 表示当前公文在暂停件列表(暂停的意思是流程挂起)
     */
    public static final String PAUSE = "pause";
    /**
     * 部门
     */
    public static final String DEPARTMENT = "2";
    public static final String VARCHAR = "varchar";
    public static final String NUMBER = "number";// 自定义变量名称 文档编号
    public static final String LEVEL = "level";// 级别
    public static final String IS_REMINDER = "isReminder";// 是否存在催办
    public static final String TASK_RELATED_LIST = "taskRelatedList";// 是否存在催办
}
