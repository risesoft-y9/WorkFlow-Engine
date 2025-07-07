package net.risesoft.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.form.Y9FormOptionClass;
import net.risesoft.entity.form.Y9FormOptionValue;

@NoArgsConstructor
@Data
public class OptionClassJsonModel implements Serializable {

    private static final long serialVersionUID = 8096420146567713232L;

    /**
     * 字典类型
     */
    private Y9FormOptionClass optionClass;

    /**
     * 字典数据列表
     */
    private List<Y9FormOptionValue> formOptionValueList;

}
