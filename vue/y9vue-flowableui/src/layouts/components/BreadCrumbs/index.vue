<template>
    <!-- :style="theStyle"  // // beta-0.1(因最初的原型稿而增加的代码) csEdit-->
    <div
        v-if="
            list[1] != undefined &&
            list[1].path != '/index/edit' &&
            list[1].path != '/workIndex/edit' &&
            list[1].path != '/workIndex/csEdit'
        "
        id="breadcrumbs"
        :class="{
            breadcrumbs: true,
            'sidebar-separate-uncollapsed': !menuCollapsed && layoutSubName === 'sidebar-separate',
            'sidebar-separate-menuCollapsed': menuCollapsed && layoutSubName === 'sidebar-separate'
        }"
    >
        <span v-if="list[0].path.indexOf('/workIndex') > -1" class="title">{{ $t(flowableStore.itemName) }}</span>
        <span v-else-if="list[0].path == '/index'" class="title">{{ $t(list[1].meta.title) }}</span>
        <span v-else class="title">{{ $t(list[0].meta.title) }}</span>
        <span class="title"></span>
        <div style="display: flex; align-items: center; cursor: pointer">
            <i class="ri-map-pin-line ri-lx" style="color: var(--el-color-primary)"></i>
            &nbsp;&nbsp;
            <el-breadcrumb>
                <el-breadcrumb-item
                    v-for="item in list"
                    v-if="list[0].path.indexOf('/workIndex') > -1"
                    :key="item.path"
                >
                    {{ item.path == '/workIndex' ? $t(flowableStore.itemName) : $t(item.meta.title) }}
                </el-breadcrumb-item>
                <el-breadcrumb-item v-else-if="list[0].path == '/index'">
                    {{ $t(list[1].meta.title) }}
                </el-breadcrumb-item>
                <!-- <el-breadcrumb-item>
                    <a-link :to="list[0].path">{{ list[0].children != undefined && list[0].children.length > 0 ? list[1].meta.title : list[0].meta.title}}</a-link>
                </el-breadcrumb-item>  -->
            </el-breadcrumb>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { inject } from 'vue';
    import type { BreadcrumbType } from '@/utils/routes';
    import { useFlowableStore } from '@/store/modules/flowableStore';

    defineOptions({ name: 'BreadCrumbs' });

    interface Props {
        layoutSubName: string;
        list?: BreadcrumbType[];
        menuCollapsed: boolean;
    }

    withDefaults(defineProps<Props>(), {
        list: () => []
    });

    const flowableStore = useFlowableStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
</script>
<style lang="scss" scoped>
    .title {
        font-size: v-bind('fontSizeObj.largerFontSize');
    }

    :deep(.el-breadcrumb) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
