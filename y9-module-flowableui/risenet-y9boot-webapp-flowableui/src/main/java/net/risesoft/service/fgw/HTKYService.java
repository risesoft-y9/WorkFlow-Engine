package net.risesoft.service.fgw;

import java.util.Map;

public interface HTKYService {
    String getTMH(String processSerialNumber);

    byte[] getTmhPicture(String processSerialNumber);

    byte[] getQYTmhPicture(Map<String, Object> map);
}
