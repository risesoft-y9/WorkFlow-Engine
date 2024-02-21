package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.service.CustomManagementService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@Service(value = "customManagementService")
public class CustomManagementServiceImpl implements CustomManagementService {

}
