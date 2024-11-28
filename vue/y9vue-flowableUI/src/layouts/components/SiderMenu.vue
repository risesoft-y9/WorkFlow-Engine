<template>
    <!-- <div id="y9-menu"> -->
    <el-menu
        :collapse="menuCollapsed"
        :collapse-transition="false"
        :default-active="defaultActive"
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
<script lang="ts">
    import { computed, ComputedRef, defineComponent, ref, toRefs } from 'vue';
    import { RoutesDataItem } from '@/utils/routes';
    import SiderMenuItem from './SiderMenuItem.vue';
    import { useRoute } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';

    interface SiderMenuSetupData {
        newMenuData: ComputedRef<RoutesDataItem[]>;
    }

    export default defineComponent({
        name: 'SiderMenu',
        props: {
            menuCollapsed: {
                type: Boolean,
                default: false
            },
            menuMode: {
                type: String,
                default: 'vertical'
            },
            belongTopMenu: {
                type: String,
                default: ''
            },
            defaultActive: {
                type: String,
                default: ''
            },
            menuData: {
                type: Array,
                default: () => {
                    return [];
                }
            }
        },
        components: {
            SiderMenuItem
        },
        onBeforeMount: {},
        setup(props): SiderMenuSetupData {
            const { menuData } = toRefs(props);
            const newMenuData = computed<RoutesDataItem[]>(() => {
                // 对每一个路由模块（位置：src->router->modules）（即菜单）进一步数据处理，以适配菜单列表样式
                const MenuItems: RoutesDataItem[] = [];
                for (let index = 0, len = menuData.value.length; index < len; index += 1) {
                    const route: RoutesDataItem = menuData.value[index];
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
            const defaultActive = ref('');
            if (route.path.indexOf('/workIndex') > -1) {
                defaultActive.value =
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
                    defaultActive.value = 'yuejian';
                }
                if (route.path.indexOf('/searchList') > -1) {
                    defaultActive.value = 'search';
                }
            } else if (route.path.indexOf('/index/') > -1) {
                if (route.query.listType != undefined && route.query.listType != '') {
                    defaultActive.value = '/index/' + route.query.listType;
                } else {
                    defaultActive.value = '/index/todo';
                }
            }

            const selectmenu = (index) => {
                console.log('selectmenu', index);
                defaultActive.value = index;
            };
            return {
                newMenuData,
                defaultActive,
                defaultOpened,
                selectmenu
            };
        }
    });
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
