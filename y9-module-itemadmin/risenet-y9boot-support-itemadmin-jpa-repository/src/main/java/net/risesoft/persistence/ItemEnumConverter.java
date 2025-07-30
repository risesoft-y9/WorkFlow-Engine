package net.risesoft.persistence;

import net.risesoft.enums.ChaoSongStatusEnum;
import net.risesoft.enums.ItemButtonTypeEnum;

/**
 * @author qinman
 * @date 2025/07/22
 * @since 9.6.9
 */
public class ItemEnumConverter {

    public static class ChaoSongStatusEnumConverter extends AbstractEnumConverter<ChaoSongStatusEnum, Integer> {
        public ChaoSongStatusEnumConverter() {
            super(ChaoSongStatusEnum.class);
        }
    }

    public static class ItemButtonTypeEnumConverter extends AbstractEnumConverter<ItemButtonTypeEnum, Integer> {
        public ItemButtonTypeEnumConverter() {
            super(ItemButtonTypeEnum.class);
        }
    }

}
