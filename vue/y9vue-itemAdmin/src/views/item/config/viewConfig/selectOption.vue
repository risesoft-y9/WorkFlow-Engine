<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-12 17:47:26
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\components\formMaking\views\SecondDev\selectOption.vue
-->
<template>
    <div
        v-loading="loading"
        class="selectOption_class"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
        element-loading-text="拼命加载中"
    >
        <el-table
            :data="dataList"
            border
            height="400"
            highlight-current-row
            style="width: 100%"
            @current-change="currentOption"
        >
            <el-table-column align="center" label="序号" type="index" width="60"></el-table-column>
            <el-table-column align="center" label="字典名称" prop="name" width="auto"></el-table-column>
            <el-table-column align="center" label="字典标识" prop="type" width="180"></el-table-column>
            <el-table-column align="center" label="数据字典" prop="opt" width="150">
                <template #default="opt_cell">
                    <el-button link type="primary" @click="optionValue(opt_cell.row)"
                        ><i class="ri-price-tag-2-line"></i>数据字典
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <el-dialog
        v-model="innerVisible"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        append-to-body
        custom-class="optionValue"
        title="数据字典"
        width="25%"
    >
        <div>
            <el-table :data="optionValueList" border height="400" style="width: 100%">
                <el-table-column align="center" label="序号" type="index" width="60"></el-table-column>
                <el-table-column align="center" label="数据名称" prop="name" width="auto"></el-table-column>
                <el-table-column align="center" label="数据代码" prop="code" width="auto"></el-table-column>
            </el-table>
        </div>
    </el-dialog>
</template>

<script lang="ts" setup>
    import { getOptionClassList, getOptionValueList } from '@/api/itemAdmin/optionClass';

    const data = reactive({
        loading: false,
        dataList: [],
        optionValueList: [],
        currentOptionRow: null
    });
    let { loading, dataList, optionValueList, currentOptionRow } = toRefs(data);

    onMounted(() => {
        setTimeout(async () => {
            if (dataList.value == undefined || dataList.value.length == 0) {
                loading.value = true;
                let res = await getOptionClassList();
                loading.value = false;
                if (res.success) {
                    dataList.value = res.data;
                }
            }
        }, 500);
    });

    async function currentOption(val) {
        currentOptionRow.value = val;
    }

    async function optionValue(row) {
        optionValueList.value = [];
        let res = await getOptionValueList(row.type);
        if (res.success) {
            optionValueList.value = res.data;
        }
    }

    defineExpose({
        currentOptionRow
    });
</script>

<style>
    .selectOption_class {
        box-shadow: none !important;
    }
</style>
