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
        <span v-else-if="list[0].path == '/cplane'" class="title">{{ $t(list[0].meta.title) }}</span>
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
                <el-breadcrumb-item v-else-if="list[0].path == '/cplane'">
                    {{ $t(list[1].meta.title) }}
                </el-breadcrumb-item>
                <!-- <el-breadcrumb-item>
                    <a-link :to="list[0].path">{{ list[0].children != undefined && list[0].children.length > 0 ? list[1].meta.title : list[0].meta.title}}</a-link>
                </el-breadcrumb-item>  -->
            </el-breadcrumb>
        </div>
    </div>
</template>
<script lang="ts">
    import { defineComponent, inject, PropType } from 'vue';
    import { BreadcrumbType } from '@/utils/routes';
    import ALink from '../ALink/index.vue';
    import { useFlowableStore } from '@/store/modules/flowableStore';

    export default defineComponent({
        name: 'BreadCrumbs',
        data() {
            return {
                // theStyle: 'width: 1000px;'  // beta-0.1(因最初的原型稿而增加的代码)
            };
        },
        props: {
            layoutSubName: {
                type: String as Ref<string>,
                required: true
            },
            list: {
                type: Array as PropType<BreadcrumbType[]>,
                default: () => {
                    return [];
                }
            },
            menuCollapsed: {
                type: Boolean as computed<Boolean>,
                required: true
            }
        },
        components: {
            ALink
        },
        setup(props) {
            const flowableStore = useFlowableStore();
            // 注入 字体对象
            const fontSizeObj: any = inject('sizeObjInfo');
            return {
                flowableStore,
                fontSizeObj
            };
        }
        // // beta-0.1(因最初的原型稿而增加的代码)  begin -->
        // 需要调整breadCrumbs宽度的情况
        // watch: {
        //     // 路由变化
        //     $route: {
        //         handler: function (route) {
        //             const settingStore = useSettingStore()
        //             const layout = settingStore.getLayout
        //             const isMenuCollapsed = settingStore.getMenuCollapsed
        //             if (route.name === "homeIndex") {
        //                 this.theStyle = "width: 94%;"
        //             }
        //             if (route.name === "homeIndex" && layout.indexOf('sidebar-separate') > 0) {
        //                 if (!isMenuCollapsed) {
        //                     this.theStyle = "width: 82.3%;"
        //                 } else {
        //                     this.theStyle = "width: 92.5%;"
        //                 }
        //             }
        //             if (route.name !== "homeIndex") {
        //                 this.theStyle = 'width: 1000px; margin: 0 auto;'
        //             }
        //         },
        //         immediate: true
        //     },
        //     // 只点击右上角收缩菜单的情况
        //     menuCollapsedChange(isMenuCollapsed) {
        //         const settingStore = useSettingStore()
        //         const layout = settingStore.getLayout
        //         if (layout.indexOf('sidebar-separate') > 0 && !isMenuCollapsed) {
        //             this.theStyle = "width: 82.3%;"
        //         }
        //         if (layout.indexOf('sidebar-separate') > 0 && isMenuCollapsed) {
        //             this.theStyle = "width: 92.5%;"
        //         }
        //     },
        //     // 只切换layout的情况
        //     layoutChange(layout) {
        //         const settingStore = useSettingStore()
        //         const isMenuCollapsed = settingStore.getMenuCollapsed
        //         console.log("===",layout, isMenuCollapsed);
        //         if (layout === "Y9Default" && !isMenuCollapsed) {
        //             this.theStyle = "width: 96%;"
        //         }
        //         if (layout === "Y9Default" && isMenuCollapsed) {
        //             this.theStyle = "width: 96%;"
        //         }
        //         if (layout.indexOf('sidebar-separate') > 0 && !isMenuCollapsed) {
        //             this.theStyle = "width: 82.3%;"
        //         }
        //         if (layout.indexOf('sidebar-separate') > 0 && isMenuCollapsed) {
        //             this.theStyle = "width: 96%;"
        //         }
        //     }
        // },
        // computed: {
        //     menuCollapsedChange() {
        //         const settingStore = useSettingStore()
        //         return settingStore.getMenuCollapsed
        //     },
        //     layoutChange() {
        //         const settingStore = useSettingStore()
        //         return settingStore.getLayout
        //     }
        // }
        // // beta-0.1(因最初的原型稿而增加的代码)  <-- end
    });
</script>
<style lang="scss" scoped>
    .title {
        font-size: v-bind('fontSizeObj.largerFontSize');
    }

    :deep(.el-breadcrumb) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
