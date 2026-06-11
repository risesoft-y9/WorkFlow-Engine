package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.dto.itemadmin.UserChoiceDTO;
import net.risesoft.enums.ItemUserChoiceEnum;

public class UserChoiceUtil {

    public static List<UserChoiceDTO> parse(String userChoice) {
        if (StringUtils.isBlank(userChoice)) {
            return new ArrayList<>();
        }
        List<UserChoiceDTO> userChoiceDTOList = new ArrayList<>();
        String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
        for (String choice : userChoices) {
            UserChoiceDTO userChoiceDTO = new UserChoiceDTO();
            String[] parts = choice.split(SysVariables.COLON);
            if (parts.length == 1) {
                userChoiceDTO.setId(parts[0]);
                userChoiceDTO.setType(ItemUserChoiceEnum.POSITION);
            } else {
                userChoiceDTO.setId(parts[1]);
                userChoiceDTO.setType(ItemUserChoiceEnum.valueOf(Integer.parseInt(parts[0])));
            }
            userChoiceDTOList.add(userChoiceDTO);
        }
        return userChoiceDTOList;
    }
}
