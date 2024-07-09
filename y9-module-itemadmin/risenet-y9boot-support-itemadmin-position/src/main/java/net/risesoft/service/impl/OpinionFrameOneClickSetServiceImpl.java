package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.OpinionFrameOneClickSet;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.OpinionFrameOneClickSetRepository;
import net.risesoft.service.OpinionFrameOneClickSetService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "opinionFrameOpinionFrameOneClickSetService")
@RequiredArgsConstructor
public class OpinionFrameOneClickSetServiceImpl implements OpinionFrameOneClickSetService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final OpinionFrameOneClickSetRepository opinionFrameOneClickSetRepository;

    @Override
    @Transactional(readOnly = false)
    public Map<String,Object> save(OpinionFrameOneClickSet opinionFrameOneClickSet) {
        Map<String,Object> ret_Map = new HashMap<String,Object>();
        try {
            OpinionFrameOneClickSet oneClick = opinionFrameOneClickSetRepository.findByBindIdAndOneSetTypeAndExecuteAction(opinionFrameOneClickSet.getBindId(), opinionFrameOneClickSet.getOneSetType(), opinionFrameOneClickSet.getExecuteAction());
            if (null == oneClick) {
                opinionFrameOneClickSet.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                opinionFrameOneClickSet.setCreateDate(sdf.format(new Date()));
                opinionFrameOneClickSet.setUserId(Y9LoginUserHolder.getPersonId());
                opinionFrameOneClickSetRepository.save(opinionFrameOneClickSet);
                ret_Map.put("success",true);
                ret_Map.put("msg","一键配置成功！");
            } else {
                ret_Map.put("success",false);
                ret_Map.put("msg","该设置已经存在，请勿重复配置！");
            }
        }catch (Exception e){
            e.printStackTrace();
            ret_Map.put("success",false);
            ret_Map.put("msg","一键配置失败！");
        }
        return ret_Map;
    }

    @Override
    public List<OpinionFrameOneClickSet> findByBindId(String bindId) {
        return opinionFrameOneClickSetRepository.findByBindId(bindId);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        opinionFrameOneClickSetRepository.deleteById(id);
    }
}
