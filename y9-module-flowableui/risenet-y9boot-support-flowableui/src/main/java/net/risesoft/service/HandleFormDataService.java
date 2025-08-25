package net.risesoft.service;

import java.util.List;
import java.util.Map;

public interface HandleFormDataService {

    void execute(String itemId, List<Map<String, Object>> items, List<String> processSerialNumbers);
}