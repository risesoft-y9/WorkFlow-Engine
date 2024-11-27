<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-12 09:42:08
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-14 16:06:14
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\views\item\selectIcon.vue
-->

<template>
    <div>
        <el-input v-model="iconName" placeholder="应用入口名称" style="width: 200px; margin-right: 10px"></el-input>
        <el-button type="primary" @click="iconSearch"><i class="ri-search-line"></i>搜索</el-button>
        <el-table
            :data="iconList"
            border
            height="450px"
            highlight-current-row
            style="width: 100%; margin-top: 16px"
            @current-change="handleCurrentChange"
        >
            <el-table-column align="center" label="默认图标" prop="icon" width="120">
                <template #default="icon_cell">
                    <img :src="'data:image/png;base64,' + icon_cell.row.iconData" style="width: 50px" />
                </template>
            </el-table-column>
            <el-table-column align="center" label="应用入口名称" prop="name" width="auto"></el-table-column>
        </el-table>
    </div>
</template>
<script lang="ts" setup>
    import { readAppIconFile, searchIcon } from '@/api/itemAdmin/item/item';

    const props = defineProps({
        iformDialogConfig: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const data = reactive({
        iconList: [],
        iconName: '',
        iconData: ''
    });

    let { iconList, iconName, iconData } = toRefs(data);

    defineExpose({
        iconData
    });

    onMounted(async () => {
        props.iformDialogConfig.loading = true;
        let res = await readAppIconFile();
        props.iformDialogConfig.loading = false;
        if (res.success) {
            iconList.value = res.data.iconList;
        }
    });

    async function iconSearch() {
        props.iformDialogConfig.loading = true;
        let res = await searchIcon(iconName.value);
        if (res.success) {
            iconList.value = res.data.iconList;
        }
        props.iformDialogConfig.loading = false;
    }

    async function handleCurrentChange(val) {
        iconData.value = 'data:image/png;base64,' + val.iconData;
    }
</script>

<style lang="scss" scoped></style>
