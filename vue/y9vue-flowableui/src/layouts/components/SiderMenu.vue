<template>
    <!-- <div id="y9-menu"> -->
    <el-menu
        :collapse="menuCollapsed"
        :collapse-transition="false"
        :default-active="localDefaultActive"
        :ellipsis="false"
        :mode="menuMode"
        :unique-opened="true"
    >
        <sider-menu-item
            v-for="item in newMenuData"
            :key="item.path"
            :belongTopMenu="belongTopMenu"
            :routeItem="item"
        ></sider-menu-item>
    </el-menu>
    <!-- </div> -->
</template>
<script lang="ts" setup>
    import { computed, ref } from 'vue';
    import type { RoutesDataItem } from '@/utils/routes';
    import SiderMenuItem from './SiderMenuItem.vue';
    import { useRoute } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';

    defineOptions({ name: 'SiderMenu' });

    interface Props {
        menuCollapsed?: boolean;
        menuMode?: string;
        belongTopMenu?: string;
        defaultActive?: string;
        menuData?: RoutesDataItem[];
    }

    const props = withDefaults(defineProps<Props>(), {
        menuCollapsed: false,
        menuMode: 'vertical',
        belongTopMenu: '',
        defaultActive: '',
        menuData: () => []
    });

    const newMenuData = computed<RoutesDataItem[]>(() => {
        // 对每一个路由模块（位置：src->router->modules）（即菜单）进一步数据处理，以适配菜单列表样式
        const MenuItems: RoutesDataItem[] = [];
        for (let index = 0, len = props.menuData.length; index < len; index += 1) {
            const route: RoutesDataItem = props.menuData[index];
            // 如果对应的路由模块（位置：src->router->modules）只有一个子路由，提升为单个菜单（即它不需要菜单下拉显示）
            if (!route.hidden && route.children) {
                route.children.length === 1 || route.name === 'index'
                    ? MenuItems.push(...(route.children as RoutesDataItem))
                    : MenuItems.push(route as RoutesDataItem[]);
            }
        }
        return MenuItems;
    });
    const flowableStore = useFlowableStore();
    let route = useRoute();
    const defaultOpened = ref('');
    // 局部默认激活路径，根据路由动态计算，覆盖父组件传入的同名 prop
    const localDefaultActive = ref('');
    if (route.path.indexOf('/workIndex') > -1) {
        localDefaultActive.value =
            '/workIndex/todo' + (flowableStore.itemList.length > 0 ? flowableStore.itemList[0].url : '');
        defaultOpened.value = flowableStore.itemList.length > 0 ? flowableStore.itemList[0].url : '';
        if (flowableStore.itemList.length > 0) {
            flowableStore.$patch({
                appType: flowableStore.itemList[0].url,
                itemName: flowableStore.itemList[0].name
            });
        }
        if (route.path.indexOf('/csEdit') > -1 && route.query.listType == 'csTodo') {
            //统一待办打开阅件，默认选中未阅件菜单
            localDefaultActive.value = 'yuejian';
        }
        if (route.path.indexOf('/searchList') > -1) {
            localDefaultActive.value = 'search';
        }
    } else if (route.path.indexOf('/index/') > -1) {
        if (route.query.listType != undefined && route.query.listType != '') {
            localDefaultActive.value = '/index/' + route.query.listType;
        } else {
            localDefaultActive.value = '/index/todo';
        }
    }

    const selectmenu = (index) => {
        console.log('selectmenu', index);
        localDefaultActive.value = index;
    };
</script>
<style lang="scss" scoped>
    //  #y9-menu{
    //    & > ul{
    //      :deep(a){
    //        text-decoration: none;
    //      }
    //    }
    //  }
</style>
