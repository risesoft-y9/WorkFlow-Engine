package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.core.DocumentDetailModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2025/08/08
 */
public interface ButtonService {

    Map<String, Object> showButton(String itemId, String taskId, String itemBox);

    List<ItemButtonModel> showButton4Add(String itemId);

    List<ItemButtonModel> showButton4Draft(String itemId);

    List<ItemButtonModel> showButton4Copy();

    List<ItemButtonModel> showButton4Doing(String itemId, String taskId);

    List<ItemButtonModel> showButton4Done(DocumentDetailModel model);

    List<ItemButtonModel> showButton4Recycle();

    List<ItemButtonModel> showButton4Todo(String itemId, String taskId, DocumentDetailModel model);
}