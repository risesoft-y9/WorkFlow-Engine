package net.risesoft.service.fgw;

public interface HTKYService {
    String getTMH(String processSerialNumber);

    byte[] getTmhPicture(String processSerialNumber);
}
