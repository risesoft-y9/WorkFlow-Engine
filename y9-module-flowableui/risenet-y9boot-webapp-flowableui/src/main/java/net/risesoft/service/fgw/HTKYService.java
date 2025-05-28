package net.risesoft.service.fgw;

import net.risesoft.api.itemadmin.FormDataApi;

import java.util.List;
import java.util.Map;

public interface HTKYService {
    String getTMH(String processSerialNumber);

    byte[] getTmhPicture(String processSerialNumber);

    byte[] getQYTmhPicture(Map<String, Object> map);

    Boolean findIsExist(String wnbh);

    Boolean isAssociated(String bianhao);

    List<String> getFileOnMove(String tmh);


}
