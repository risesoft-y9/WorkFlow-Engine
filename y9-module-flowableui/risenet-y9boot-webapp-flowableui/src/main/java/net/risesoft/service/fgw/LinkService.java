package net.risesoft.service.fgw;

import net.risesoft.entity.GLJ;
import net.risesoft.entity.ProcessModel;
import net.risesoft.entity.CXLink;
import net.risesoft.pojo.Y9Page;

import java.util.List;
import java.util.Map;

public interface LinkService {

    List<ProcessModel> allProccesId(String processInstanceId);

    Y9Page<Map<String, Object>> findPiByProcessId(String processId, String fromInstanceId, String searchMapStr, Integer page, Integer row);

    void saveLink(String processInstanceId, List<GLJ> to);

    void deleteById(String linkid);

    Map<String, List<CXLink>> findByInstanceId(String processInstanceId);
}
