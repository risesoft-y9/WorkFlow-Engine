<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-06 16:57:01
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-06-16 11:28:46
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\views\y9form\table\selectTable.vue
-->
<template>
    <div class="margin-bottom-20">
        <el-input
            v-model="tableName"
            clearable="true"
            placeholder="请搜索"
            style="width: 230px"
            @keyup.enter="getTableList"
        >
            <template #prefix>
                <i class="ri-search-line"></i>
            </template>
        </el-input>
    </div>
    <y9Table :config="tableConfig" @on-current-change="onCurrentChange"></y9Table>
</template>

<script lang="ts" setup>
    import { getAllTables } from '@/api/itemAdmin/y9form';

    const props = defineProps({
        currInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });
    const data = reactive({
        //当前节点信息
        tableConfig: {
            //人员列表表格配置
            columns: [
                {
                    title: '序号',
                    type: 'index',
                    width: '80'
                },
                {
                    title: '名称',
                    key: 'name'
                }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        currentRow: null,
        tableName: '', //搜索表名
        tableNames: '' //已经存在的表名
    });

    let { tableConfig, currentRow, tableName, tableNames } = toRefs(data);

    watch(
        () => props.currInfo,
        (newVal) => {}
    );

    onMounted(() => {
        getTableList();
    });

    function onCurrentChange(data) {
        currentRow.value = data;
    }

    async function getTableList() {
        let res = await getAllTables(tableName.value);
        if (res.success) {
            tableConfig.value.tableData = res.data.rows;
            tableNames.value = res.data.tableNames;
        }
    }

    defineExpose({
        currentRow,
        tableNames
    });
</script>

<style lang="scss" scoped></style>
