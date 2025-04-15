package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.FileAttribute;
import net.risesoft.repository.jpa.FileAttributeRepository;
import net.risesoft.service.FileAttributeService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileAttributeServiceImpl implements FileAttributeService {

    private final FileAttributeRepository fileAttributeRepository;

    @Override
    public List<FileAttribute> getFileAttribute(String pcode) {
        if (StringUtils.isBlank(pcode)) {// 一级
            return fileAttributeRepository.findByPcode1();
        } else if (pcode.length() == 4) {// 二级
            return fileAttributeRepository.findByPcode2(pcode + "%");
        } else if (pcode.length() == 8) {// 三级
            return fileAttributeRepository.findByPcode3(pcode + "%");
        }
        return List.of();
    }
}
