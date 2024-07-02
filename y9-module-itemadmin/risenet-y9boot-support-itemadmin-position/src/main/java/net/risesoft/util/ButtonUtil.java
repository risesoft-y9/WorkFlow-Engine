package net.risesoft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.DelegationState;

import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.entity.CustomProcessInfo;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.CustomProcessInfoService;
import net.risesoft.service.ItemTaskConfService;
import net.risesoft.service.ProcInstanceRelationshipService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class ButtonUtil {

    private final ProcInstanceRelationshipService procInstanceRelationshipService;
    private final TaskApi taskManager;
    private final VariableApi variableManager;
    private final RuntimeApi runtimeManager;
    private final ProcessDefinitionApi processDefinitionManager;
    private final IdentityApi identityManager;
    private final HistoricTaskApi historicTaskManager;
    private final CustomProcessInfoService customProcessInfoService;
    private final SpmApproveItemService itemService;
    private final ItemTaskConfService itemTaskConfService;

    public ButtonUtil() {
        this.procInstanceRelationshipService = Y9Context.getBean(ProcInstanceRelationshipService.class);
        this.taskManager = Y9Context.getBean(TaskApi.class);
        this.variableManager = Y9Context.getBean(VariableApi.class);
        this.runtimeManager = Y9Context.getBean(RuntimeApi.class);
        this.processDefinitionManager = Y9Context.getBean(ProcessDefinitionApi.class);
        this.identityManager = Y9Context.getBean(IdentityApi.class);
        this.historicTaskManager = Y9Context.getBean(HistoricTaskApi.class);
        this.customProcessInfoService = Y9Context.getBean(CustomProcessInfoService.class);
        this.itemService = Y9Context.getBean(SpmApproveItemService.class);
        this.itemTaskConfService = Y9Context.getBean(ItemTaskConfService.class);
    }

    @SuppressWarnings({"unused", "unchecked"})
    public Map<String, Object> showButton(String itemId, String taskId, String itembox) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = new HashMap<>(16);
        String[] buttonIds = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21"};
        String[] buttonNames = {"保存", "发送", "返回", "退回", "委托", "协商", "完成", "送下一人", "办理完成", "签收", "撤销签收", "办结", "收回",
            "拒签", "特殊办结", "重定位", "打印", "抄送", "加减签", "恢复待办", "提交"};
        int[] buttonOrders = {3, 15, 10, 11, 1, 2, 21, 4, 5, 6, 7, 8, 9, 12, 19, 13, 14, 16, 18, 20, 17};
        boolean[] isButtonShow = new boolean[buttonIds.length];
        for (int i = 0; i < buttonIds.length; i++) {
            isButtonShow[i] = false;
        }
        TaskModel task = null;
        if (ItemBoxTypeEnum.TODO.getValue().equals(itembox) || ItemBoxTypeEnum.DOING.getValue().equals(itembox)) {
            task = taskManager.findById(tenantId, taskId).getData();
        }
        Map<String, Object> vars = new HashMap<>(16);
        String varsUser = "";
        String taskSenderId = "";
        List<String> varsUsers = new ArrayList<>();
        String multiInstance = "", varsSponsorGuid = "";
        SpmApproveItem item = itemService.findById(itemId);
        boolean customItem = false, showSubmitButton = false;
        if (null != item.getCustomItem()) {
            customItem = item.getCustomItem();
        }
        showSubmitButton = item.isShowSubmitButton();
        if (task != null) {
            vars = variableManager.getVariables(tenantId, taskId).getData();
            varsUsers = (List<String>)vars.get(SysVariables.USERS);
            varsUser = String.valueOf(vars.get(SysVariables.USER));
            varsSponsorGuid = String.valueOf(vars.get(SysVariables.PARALLELSPONSOR));
            taskSenderId = String.valueOf(vars.get(SysVariables.TASKSENDERID));
            multiInstance = processDefinitionManager.getNodeType(tenantId, task.getProcessDefinitionId(),
                task.getTaskDefinitionKey());
        }

        //// 在待办件列表中打开公文显示按钮(itembox==todo表示在待办件列表)，在在办件列表和办结件列表里打开公文不显示任何按钮
        if (ItemBoxTypeEnum.TODO.getValue().equals(itembox)) {
            // 并行任务的总个数
            int nrOfInstances = -1;
            // 当前还没有完成的并行任务个数，对应vars中的nrOfActiveInstances变量
            int nrOfActiveInstances = -1;
            // 已经完成的并行任务个数
            long nrOfCompletedInstances = -1;
            // 已经循环的次数
            long loopCounter = -1;
            // 用来表示当前任务节点在串行状态下，当前用户是否显示发送按钮和送下一人按钮，当为true时，不显示发送按钮，显示的是送下一人按钮
            boolean isSequential = false;
            // 串行时是否是最后一个人员
            boolean isLastSequential = false;
            // 拒签时是否是最后一个人员
            boolean isLastPerson4RefuseClaim = false;
            // 在串行状态下且不是users里面的最后一个用户，isSequential为true
            if (multiInstance.equals(SysVariables.SEQUENTIAL)) {
                isSequential = true;
                nrOfInstances = (Integer)vars.get(SysVariables.NROFINSTANCES);
                nrOfActiveInstances = (Integer)vars.get(SysVariables.NROFACTIVEINSTANCES);
                nrOfCompletedInstances = (Integer)vars.get(SysVariables.NROFCOMPLETEDINSTANCES);
                loopCounter = (Integer)vars.get(SysVariables.LOOPCOUNTER);
                int usersSize = varsUsers.size();
                // users可能没有用户，或者只有一个用户，因此需要进行判断，对于没有用户，或者只有一个用户，都应显示发送按钮，送下一人按钮不显示
                if (usersSize > 1) {
                    // 串行处理时，当存在多个用户时，如果当前用户是users里面的最后一个，此时isLastSequential应为true
                    /**
                     * 判断 1、实际的用户数和nrOfInstances是否一致 2、nrOfCompletedInstances和loopCounter 3、nrOfActiveInstances等于1
                     * 有一个不成立说明加减签有问题，调整变量，且以users中的用户为准
                     */
                    long finishedCount = 0;
                    if (usersSize != nrOfInstances || nrOfCompletedInstances != loopCounter
                        || 1 != nrOfActiveInstances) {
                        finishedCount = historicTaskManager
                            .getFinishedCountByExecutionId(tenantId, task.getExecutionId()).getData();
                        nrOfCompletedInstances = finishedCount;
                        loopCounter = finishedCount;
                        Map<String, Object> varMapTemp = new HashMap<>(16);
                        varMapTemp.put(SysVariables.NROFINSTANCES, usersSize);
                        varMapTemp.put(SysVariables.NROFCOMPLETEDINSTANCES, finishedCount);
                        varMapTemp.put(SysVariables.LOOPCOUNTER, finishedCount);
                        varMapTemp.put(SysVariables.NROFACTIVEINSTANCES, 1);
                        runtimeManager.setVariables(tenantId, task.getExecutionId(), varMapTemp);
                    }
                    if (nrOfInstances == (nrOfCompletedInstances + 1)
                        && positionId.equals(varsUsers.get(varsUsers.size() - 1))) {
                        isLastSequential = true;
                    }
                } else {
                    isLastSequential = true;
                }
            }
            // 用来表示当前任务节点在并行状态下，true表示是并行状态，false表示不是并行状态
            boolean isParallel = false;
            // 用来表示并行状态下当前节点是否存在主协办状态
            // boolean sponsorStatus = false;
            // 用来表示当前任务节点在并行状态下且是主办，true表示是并行状态下主办人员，false表示不是并行状态下的主办人员
            boolean isParallelSponsor = false;
            // 是否是并行状态下的最后一个人
            boolean isLastParallel = false;
            // boolean isHandledParallel = true;//
            // 并行办理的任务节点中是否有任务被办理过，默认值为true，即如果出现错误则认为有人办理过，防止显示按钮引起其它不可预知的问题
            // 在并行状态下且还有用户未办理完成，isParallel为true
            if (multiInstance.equals(SysVariables.PARALLEL)) {
                isParallel = true;
                if (StringUtils.isNotBlank(varsSponsorGuid) && positionId.equals(varsSponsorGuid)) {
                    isParallelSponsor = true;
                }
                nrOfInstances = (Integer)vars.get(SysVariables.NROFINSTANCES);
                nrOfActiveInstances = (Integer)vars.get(SysVariables.NROFACTIVEINSTANCES);
                nrOfCompletedInstances = (Integer)vars.get(SysVariables.NROFCOMPLETEDINSTANCES);
                if (nrOfInstances > 0) {
                    // 如果只有一个人办理，那么他就是最后一个人
                    if (nrOfInstances == 1) {
                        isLastParallel = true;
                    } else {
                        // 设置并行办理状态下是否是最后一个办理人员,最后一人为主办
                        if (nrOfInstances == (nrOfCompletedInstances + 1)) {
                            isLastParallel = true;
                            isParallelSponsor = true;
                        }
                    }
                }
            }
            // 用于判定是否需要发送下一个节点
            map.put("nextNode", false);
            // 节点类型，用于判定按钮调用的方法
            map.put("multiInstance", multiInstance);
            if (task != null) {
                String assignee = task.getAssignee();
                String currentProcInstanceId = task.getProcessInstanceId();
                /**
                 * 是否签收，true表示签收了，false表示没有签收 如果未签收了，除了签收、拒签、返回按钮都不显示 因此下面每个按钮都需要判断isAssignee为true还是false
                 */
                boolean isAssignee = StringUtils.isNotBlank(assignee);
                Boolean isContainEndEvent =
                    processDefinitionManager.isContainNodeType(tenantId, taskId, SysVariables.ENDEVENT);
                // 获取某个节点除去end节点的所有的输出线路的个数
                int outPutNodeCount = processDefinitionManager.getOutPutNodeCount(tenantId, taskId);
                String processDefinitionId = task.getProcessDefinitionId();
                String taskDefKey = task.getTaskDefinitionKey();
                /*----- 下面是保存按钮的设置 -----*/
                // 如果未签收了，除了签收、拒签、返回按钮都不显示
                if (isAssignee) {
                    isButtonShow[0] = true;
                }

                /*----- 上面是保存按钮的设置 -----*/

                /*----- 下面是可以打开选人界面的发送按钮的设置 -----*/
                // DelegationState.PENDING ==
                // task.getDelegationState()：表示当前用户是在协办状态，发送按钮不再显示，完成按钮显示
                // isSequential：在串行状态下且不是users里面的最后一个用户，isSequential为true
                // outPutNodeCount>0表示存在发送节点
                // 当outPutNodeCount==1 &&
                // isContainEndEvent时，表示只有一个发送节点且是办结节点，此时不再显示发送按钮
                if (DelegationState.PENDING != task.getDelegationState() && isAssignee && outPutNodeCount > 0) {
                    // 如果是在并行状态下，那么就要看是不是并行状态主办人，如果是则显示发送按钮，否则不显示
                    if (isParallel) {
                        if (isParallelSponsor) {
                            // isParallelSponsor：表示当前用户是并行状态下主办人员
                            if (showSubmitButton) {
                                isButtonShow[20] = true;
                            } else {
                                isButtonShow[1] = true;
                            }
                            // 主办办理
                            map.put("sponsorHandle", "true");
                        } else {
                            ItemTaskConf itemTaskConf =
                                itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId,
                                    processDefinitionId, taskDefKey);
                            if (null != itemTaskConf && itemTaskConf.getSignTask()) {
                                if (showSubmitButton) {
                                    isButtonShow[20] = true;
                                } else {
                                    isButtonShow[1] = true;
                                }
                            }
                        }
                        if (isLastParallel) {
                            if (showSubmitButton) {
                                isButtonShow[20] = true;
                            } else {
                                isButtonShow[1] = true;
                            }
                        }
                    } else if (isSequential) {
                        // 如果在串行状态下，那么就要看是不是最后一个用户，如果是则显示发送按钮，否则不显示
                        if (isLastSequential) {
                            if (showSubmitButton) {
                                isButtonShow[20] = true;
                            } else {
                                isButtonShow[1] = true;
                            }
                        }
                    } else {
                        // 如果既不是并行也不是串行
                        if (showSubmitButton) {
                            isButtonShow[20] = true;
                        } else {
                            isButtonShow[1] = true;
                        }
                    }
                    // 定制流程处理,有发送按钮说明需要进入下一个定制任务**************************************************
                    if (customItem) {
                        // 定制流程的任务都以办理完成往下走
                        isButtonShow[8] = true;
                        if (isButtonShow[1]) {
                            // 不显示发送
                            isButtonShow[1] = false;
                            // 用于判定是否需要发送下一个节点
                            map.put("nextNode", true);
                        }
                    }

                    // 没有发送按钮的时候，串并行显示加减签按钮
                    boolean b =
                        (multiInstance.equals(SysVariables.PARALLEL) || multiInstance.equals(SysVariables.SEQUENTIAL))
                            && !isButtonShow[1];
                    if (b) {
                        isButtonShow[18] = true;
                    }
                }
                /*----- 上面是可以打开选人界面的发送按钮的设置 -----*/

                /*----- 下面是返回按钮的设置 -----*/
                isButtonShow[2] = true;
                /*----- 上面是返回按钮的设置 -----*/

                /*----- 下面是退回按钮的设置 -----*/
                if (isAssignee && !customItem) {
                    /*----- 下面是退出按钮的判断 -----*/
                    String returnDoc =
                        variableManager.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
                    String takeBackDoc =
                        variableManager.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
                    String repositionDoc =
                        variableManager.getVariableLocal(tenantId, taskId, SysVariables.REPOSITION).getData();
                    // 当前任务为退回件,或者收回件都不能再退回，不显示退回按钮
                    if (returnDoc != null || takeBackDoc != null || repositionDoc != null) {
                        isButtonShow[3] = false;
                    } else {
                        if (isParallel) {
                            // if (sponsorStatus) {// 在基本配置中是否配置了主协办，没有配置为false，配置了使用主协办为true，不使用主协办为false
                            // if (isParallelSponsor) {// 主协办状态下主办人在其他人没有办理的情况下可以退回
                            // isButtonShow[3] = true;
                            // }
                            // } else {
                            // isButtonShow[3] = true;
                            // }
                            // 并行状态下都可以退回，退回只退回自己的任务，不影响其他人
                            isButtonShow[3] = true;
                        } else if (isSequential) {
                            isButtonShow[3] = true;
                        } else {
                            // 当前任务不是退出任务且发送人不等于当前人的时候才可以显示退回
                            if (!taskSenderId.equals(positionId)) {
                                isButtonShow[3] = true;
                            }
                        }
                    }
                    /*----- 上面是退出按钮的判断 -----*/
                }
                /*----- 上面是退回按钮的设置 -----*/

                /*----- 下面是协办状态下的完成按钮的设置 -----*/
                if (isAssignee) {
                    // 此时说明用户在协办状态，发送按钮不再显示，完成按钮显示
                    if (DelegationState.PENDING == task.getDelegationState()) {
                        // 协办状态下，不再允许转办
                        isButtonShow[4] = false;
                        // 协办状态下，不再允许协办
                        isButtonShow[5] = false;
                        isButtonShow[6] = true;
                    }
                }

                /*----- 上面是完成按钮的设置 -----*/

                /*----- 下面是送下一人状态下的完成按钮的设置 -----*/
                if (isSequential && !isLastSequential && isAssignee
                    && DelegationState.PENDING != task.getDelegationState()) {
                    isButtonShow[7] = true;
                    isButtonShow[12] = false;
                    // 定制流程处理**************************************************
                    if (customItem) {
                        // 不显示送下一人
                        isButtonShow[7] = false;
                        // 定制流程的任务都以办理完成往下走
                        isButtonShow[8] = true;
                    }
                }
                /*----- 上面是送下一人按钮的设置 -----*/

                /*----- 下面是并行处理时办理完成按钮的设置 -----*/
                // 办理完成，并行状态下存在
                // 人员已经指定，在并行状态下并且不是并行状态下主办，同时用户设置了存在主协办
                if (isAssignee && isParallel && DelegationState.PENDING != task.getDelegationState()) {
                    // if (sponsorStatus) {// 在基本是否配置了主协办，没有配置为false，配置了使用主协办为true，不使用主协办为false
                    // 如果不是主办人，并且不是最后一个处理人，显示办理完成按钮
                    if (!isParallelSponsor && !isLastParallel) {
                        ItemTaskConf itemTaskConf =
                            itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId,
                                processDefinitionId, taskDefKey);
                        if (null != itemTaskConf && itemTaskConf.getSignTask()) {
                            if (outPutNodeCount > 0) {
                                if (showSubmitButton) {
                                    isButtonShow[20] = true;
                                } else {
                                    isButtonShow[1] = true;
                                }
                            } else {
                                isButtonShow[8] = false;
                            }
                        } else {
                            isButtonShow[8] = true;
                        }
                    }
                    // } else {// sponsorStatus，此时如果是最后一个处理人，上面的发送按钮要显示，如果不是，要显示办理完成按钮
                    // if (!isLastParallel) {
                    // isButtonShow[8] = true;
                    // isButtonShow[12] = false;
                    // }
                    // }
                }

                /*----- 上面是办理完成按钮的设置 -----*/

                /*----- 下面是签收按钮的设置 -----*/
                // 签收、拒签按钮
                // 当user变量没有指定人员时，显示签收
                if (!isAssignee) {
                    isButtonShow[9] = true;
                    isButtonShow[12] = false;
                    isButtonShow[13] = true;
                    // 是否是最后一个拒签人员，如果是则提示是否拒签，如果拒签，则退回任务给发送人，发送人重新选择人员进行签收办理
                    List<IdentityLinkModel> identityLinkList =
                        identityManager.getIdentityLinksForTask(tenantId, taskId).getData();
                    if (identityLinkList.size() <= 1) {
                        map.put("isLastPerson4RefuseClaim", true);
                    }
                }
                /*----- 上面是签收按钮的设置 -----*/

                /*----- 下面是撤销签收按钮的设置 -----*/
                // 撤销签收
                if (isAssignee) {
                    // 判断当前流程实例经过的任务节点数和当前流程实例是否存在父流程实例
                    // 如果任务节点数为1且存在父流程实例，则是流程调用，此时显示拒签按钮
                    // 否则是流程中发给多人等情况，不显示拒签按钮
                    Integer count = historicTaskManager.getByProcessInstanceId(tenantId, currentProcInstanceId, "")
                        .getData().size();
                    if (count == 1) {
                        String superProcessInstanceId =
                            procInstanceRelationshipService.getParentProcInstanceId(currentProcInstanceId);
                        // 是父子流程
                        if (StringUtils.isNotBlank(superProcessInstanceId)) {
                            isButtonShow[10] = true;
                        }
                    } else {
                        List<IdentityLinkModel> identityLinkList =
                            identityManager.getIdentityLinksForTask(tenantId, taskId).getData();
                        int size = 2;
                        if (identityLinkList.size() > size) {
                            for (IdentityLinkModel i : identityLinkList) {
                                if (i.getUserId().contains(positionId) && "candidate".equals(i.getType())) {
                                    isButtonShow[10] = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                /*----- 上面是撤销签收按钮的设置 -----*/

                /*----- 下面是办结按钮的设置 -----*/
                // 办结
                // 当前节点的目标节点存在ENDEVENT类型节点时，显示办结按钮
                if (isAssignee && isContainEndEvent) {
                    // 如果是在并行状态下，那么就要看是不是并行状态主办人，如果是则显示办结按钮，否则不显示
                    if (isParallel) {
                        ItemTaskConf itemTaskConf =
                            itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId,
                                processDefinitionId, taskDefKey);
                        if (null != itemTaskConf && itemTaskConf.getSignTask()) {
                            isButtonShow[11] = true;
                        } else {
                            if (isParallelSponsor || isLastParallel) {
                                isButtonShow[11] = true;
                            }
                        }

                    } else if (isSequential) {
                        // 如果在串行状态下，那么就要看是不是最后一个用户，如果是则显示办结按钮，否则不显示
                        if (isLastSequential) {
                            isButtonShow[11] = true;
                        }
                    } else {// 如果既不是并行也不是串行
                        isButtonShow[11] = true;
                    }
                    isButtonShow[12] = false;
                    if (customItem) {
                        // 定制流程处理,办结按钮处理**************************************************
                        CustomProcessInfo info = customProcessInfoService
                            .getCurrentTaskNextNode((String)vars.get(SysVariables.PROCESSSERIALNUMBER));
                        // 如果当前运行任务的下一个节点是办结,且显示办结按钮,则隐藏办理完成按钮
                        if (info.getTaskType().equals(SysVariables.ENDEVENT)) {
                            boolean isButtonShow11 = isButtonShow[11];
                            if (isButtonShow11) {
                                // 办理完成按钮隐藏
                                isButtonShow[8] = false;
                                // 用于判定是否需要发送下一个节点
                                map.put("nextNode", true);
                            }
                        } else {// 如果当前运行任务的下一个节点不是办结,隐藏办结按钮
                            isButtonShow[11] = false;
                        }
                    }
                }
                /*----- 上面是办结按钮的设置 -----*/

            } else {// task为null，此时是新增
                isButtonShow[0] = true;
                if (showSubmitButton) {
                    isButtonShow[20] = true;
                } else {
                    isButtonShow[1] = true;
                }
            }
            // 抄送
            isButtonShow[17] = true;

            // 下面是加减签按钮,待办件，自己发的件，可加减签，主办可加减签。
            boolean b = (multiInstance.equals(SysVariables.PARALLEL) || multiInstance.equals(SysVariables.SEQUENTIAL))
                && !customItem && StringUtils.isNotBlank(taskSenderId) && taskSenderId.contains(positionId)
                || (StringUtils.isNotBlank(varsSponsorGuid) && positionId.equals(varsSponsorGuid));
            if (b) {
                isButtonShow[18] = true;
            }
            // 上面是加减签按钮
        } else if (ItemBoxTypeEnum.DOING.getValue().equals(itembox)) {
            // 在办情况下，显示返回按钮
            isButtonShow[2] = true;
            // 在办情况下，收回按钮默认为不显示，当配置了收回按钮时，且当前节点的下一个节点满足回收的条件时才显示回收按钮
            isButtonShow[12] = false;
            String takeBackObj = variableManager.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
            String rollbackObj = variableManager.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
            String repositionObj =
                variableManager.getVariableLocal(tenantId, taskId, SysVariables.REPOSITION).getData();
            // 下面是收回按钮
            if (StringUtils.isNotBlank(taskSenderId) && taskSenderId.contains(positionId) && takeBackObj == null
                && rollbackObj == null && repositionObj == null) {
                isButtonShow[12] = true;
            }
            // 上面是收回按钮
            // 下面是加减签按钮
            boolean b = (multiInstance.equals(SysVariables.PARALLEL) || multiInstance.equals(SysVariables.SEQUENTIAL))
                && StringUtils.isNotBlank(taskSenderId) && taskSenderId.contains(positionId);
            if (b) {
                isButtonShow[18] = true;
            }
            // 抄送
            isButtonShow[17] = true;
            ProcessInstanceModel processInstanceModel =
                runtimeManager.getProcessInstance(tenantId, task.getProcessInstanceId()).getData();
            // 重定向按钮
            isButtonShow[15] = true;
            if (positionId.equals(processInstanceModel.getStartUserId())) {
                // 重定向
                // isButtonShow[15] = true;
                // 特殊办结
                isButtonShow[14] = true;
            }
        } else if (ItemBoxTypeEnum.DONE.getValue().equals(itembox)) {
            isButtonShow[2] = true;
            isButtonShow[19] = true;
        } else if (ItemBoxTypeEnum.ADD.getValue().equals(itembox)) {
            isButtonShow[0] = true;
            if (showSubmitButton) {
                isButtonShow[20] = true;
            } else {
                isButtonShow[1] = true;
            }
            // 新建可抄送
            isButtonShow[17] = true;
        } else if (ItemBoxTypeEnum.DRAFT.getValue().equals(itembox)) {
            isButtonShow[0] = true;
            if (showSubmitButton) {
                isButtonShow[20] = true;
            } else {
                isButtonShow[1] = true;
            }
            isButtonShow[2] = true;
            isButtonShow[17] = true;
        } else if (ItemBoxTypeEnum.MONITORDOING.getValue().equals(itembox)) {
            isButtonShow[15] = true;
            isButtonShow[14] = true;
            isButtonShow[2] = true;
        } else if (SysVariables.PAUSE.equals(itembox)) {
            isButtonShow[2] = true;
        }
        // 打印
        isButtonShow[16] = true;
        map.put("buttonIds", buttonIds);
        map.put("buttonNames", buttonNames);
        map.put("isButtonShow", isButtonShow);
        map.put("buttonOrders", buttonOrders);

        return map;
    }
}