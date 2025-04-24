package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.MergeFile;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface MergeFileService {

    Y9Result<Object> delMergeFile(String[] ids);

    List<MergeFile> getMergeFileList(String personId, String processSerialNumber, String listType, String fileType);

    void saveMergeFile(MergeFile mergeFile);
}
