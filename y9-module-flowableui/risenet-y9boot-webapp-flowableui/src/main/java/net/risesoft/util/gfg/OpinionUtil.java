package net.risesoft.util.gfg;

import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.OpinionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpinionUtil {

    /**
     * 生成打印模板的意见内容
     * @param opinionList
     * @return
     */
    public static String generateOpinions(List<OpinionListModel> opinionList) {
        StringBuilder resultBuilder = new StringBuilder();
        for (OpinionListModel detail : opinionList) {
            OpinionModel opinion = detail.getOpinion();
            if (opinion != null) {
                String line = opinion.getContent() + "（" + opinion.getDeptName() + "  " + opinion.getUserName() + "  " + opinion.getModifyDate() + "）";
                resultBuilder.append(line).append("\n");
            }
        }
        // 去掉最后一个多余的换行符
            if (resultBuilder.length() > 0) {
            resultBuilder.deleteCharAt(resultBuilder.length() - 1);
        }
        String opinionContent = resultBuilder.toString();
        return opinionContent;
    }

}
