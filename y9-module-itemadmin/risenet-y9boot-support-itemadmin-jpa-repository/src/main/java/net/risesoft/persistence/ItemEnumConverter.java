package net.risesoft.persistence;

import net.risesoft.enums.ChaoSongStatusEnum;

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

}
