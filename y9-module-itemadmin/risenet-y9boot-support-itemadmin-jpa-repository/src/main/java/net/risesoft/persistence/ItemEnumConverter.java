package net.risesoft.persistence;

import java.util.Arrays;

import net.risesoft.enums.ActRuDetailSignStatusEnum;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.ChaoSongStatusEnum;
import net.risesoft.enums.DocumentCopyStatusEnum;
import net.risesoft.enums.DynamicRoleKindsEnum;
import net.risesoft.enums.DynamicRoleRangesEnum;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.enums.ItemFormTemplateTypeEnum;
import net.risesoft.enums.ItemFormTypeEnum;
import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.ItemTableTypeEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;

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

    public static class ItemFormTypeEnumConverter extends AbstractEnumConverter<ItemFormTypeEnum, Integer> {
        public ItemFormTypeEnumConverter() {
            super(ItemFormTypeEnum.class);
        }
    }

    public static class ItemFormTemplateTypeEnumConverter
        extends AbstractEnumConverter<ItemFormTemplateTypeEnum, Integer> {
        public ItemFormTemplateTypeEnumConverter() {
            super(ItemFormTemplateTypeEnum.class);
        }
    }

    public static class ItemTableTypeEnumConverter extends AbstractEnumConverter<ItemTableTypeEnum, Integer> {
        public ItemTableTypeEnumConverter() {
            super(ItemTableTypeEnum.class);
        }
    }

    public static class ItemInterfaceTypeEnumConverter extends AbstractEnumConverter<ItemInterfaceTypeEnum, String> {
        public ItemInterfaceTypeEnumConverter() {
            super(ItemInterfaceTypeEnum.class);
        }
    }

    public static class ActRuDetailStatusEnumConverter extends AbstractEnumConverter<ActRuDetailStatusEnum, Integer> {
        public ActRuDetailStatusEnumConverter() {
            super(ActRuDetailStatusEnum.class);
        }
    }

    public static class ActRuDetailSignStatusEnumConverter
        extends AbstractEnumConverter<ActRuDetailSignStatusEnum, Integer> {
        public ActRuDetailSignStatusEnumConverter() {
            super(ActRuDetailSignStatusEnum.class);
        }

        @Override
        public Integer convertToDatabaseColumn(ActRuDetailSignStatusEnum attribute) {
            return attribute != null ? attribute.getValue() : ActRuDetailSignStatusEnum.NONE.getValue();
        }

        @Override
        public ActRuDetailSignStatusEnum convertToEntityAttribute(Integer dbData) {
            if (dbData == null || dbData == -1) {
                return ActRuDetailSignStatusEnum.NONE;
            }
            if (dbData == -2) {
                return ActRuDetailSignStatusEnum.REFUSE;
            }
            return Arrays.stream(ActRuDetailSignStatusEnum.values())
                .filter(e -> e.getValue().equals(dbData))
                .findFirst()
                .orElse(ActRuDetailSignStatusEnum.NONE);
        }
    }

    public static class DocumentCopyStatusEnumConverter extends AbstractEnumConverter<DocumentCopyStatusEnum, Integer> {
        public DocumentCopyStatusEnumConverter() {
            super(DocumentCopyStatusEnum.class);
        }
    }

    public static class DynamicRoleKindsEnumConverter extends AbstractEnumConverter<DynamicRoleKindsEnum, Integer> {
        public DynamicRoleKindsEnumConverter() {
            super(DynamicRoleKindsEnum.class);
        }

        @Override
        public Integer convertToDatabaseColumn(DynamicRoleKindsEnum attribute) {
            if (attribute == null) {
                return DynamicRoleKindsEnum.NONE.getValue();
            }
            return super.convertToDatabaseColumn(attribute);
        }

        @Override
        public DynamicRoleKindsEnum convertToEntityAttribute(Integer dbData) {
            if (dbData == null) {
                return DynamicRoleKindsEnum.NONE;
            }
            return super.convertToEntityAttribute(dbData);
        }
    }

    public static class DynamicRoleRangesEnumConverter extends AbstractEnumConverter<DynamicRoleRangesEnum, Integer> {
        public DynamicRoleRangesEnumConverter() {
            super(DynamicRoleRangesEnum.class);
        }

        @Override
        public Integer convertToDatabaseColumn(DynamicRoleRangesEnum attribute) {
            if (attribute == null) {
                return DynamicRoleRangesEnum.NONE.getValue();
            }
            return super.convertToDatabaseColumn(attribute);
        }

        @Override
        public DynamicRoleRangesEnum convertToEntityAttribute(Integer dbData) {
            if (dbData == null) {
                return DynamicRoleRangesEnum.NONE;
            }
            return super.convertToEntityAttribute(dbData);
        }
    }

    public static class ItemPermissionEnumConverter extends AbstractEnumConverter<ItemPermissionEnum, Integer> {
        public ItemPermissionEnumConverter() {
            super(ItemPermissionEnum.class);
        }
    }

    public static class SignDeptDetailStatusEnumConverter
        extends AbstractEnumConverter<SignDeptDetailStatusEnum, Integer> {
        public SignDeptDetailStatusEnumConverter() {
            super(SignDeptDetailStatusEnum.class);
        }
    }
}
