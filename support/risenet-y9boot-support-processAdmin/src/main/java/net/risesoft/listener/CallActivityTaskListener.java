package net.risesoft.listener;

import java.util.Map;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class CallActivityTaskListener implements TaskListener {

    private static final long serialVersionUID = 7068311265120131339L;

    /**
     * 需要复制的字段间的对应关系，多个对应字段间以逗号分隔，源字段和目标字段间以冒号分隔（前面为源字段，后面为目标字段）
     */
    @SuppressWarnings("unused")
    private Expression fieldMapping;

    @Override
    @SuppressWarnings("unchecked")
    public void notify(DelegateTask delegateTask) {
        Map<String, Object> initDataMap = (Map<String, Object>)(delegateTask.getVariable(SysVariables.INITDATAMAP));
        String parentProcessSerialNumber = (String)initDataMap.get(SysVariables.PARENTPROCESSSERIALNUMBER);

        delegateTask.setVariable(SysVariables.PROCESSSERIALNUMBER, parentProcessSerialNumber);

        String processDefinitionId = delegateTask.getProcessDefinitionId();
        ItemModel itemModel = Y9Context.getBean(ItemApi.class).findByProcessDefinitionKey(Y9LoginUserHolder.getTenantId(), processDefinitionId.split(":")[0]);
        if (null != itemModel) {
        }
        /**
         * 调接口把父流程表单的数据拷贝到子流程中去
         */
    }
}