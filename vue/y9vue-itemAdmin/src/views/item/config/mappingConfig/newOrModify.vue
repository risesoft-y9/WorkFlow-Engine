<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-13 09:56:19
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-13 14:11:52
 * @FilePath: \workspace-y9boot-9.5-vue\y9vue-itemAdmin\src\views\itemAdmin\item\mappingConfig\newOrModify.vue
-->
<template>
    <el-form
        ref="mappingForm"
        :inline-message="true"
        :model="mappingConf"
        :rules="rules"
        :status-icon="true"
        label-width="120px"
    >
        <el-form-item label="数据库表" prop="tableName">
            <el-select v-model="mappingConf.tableName" placeholder="请选择" @change="tableChange">
                <el-option
                    v-for="table in tableList"
                    :key="table.id"
                    :label="table.tableName + '(' + table.tableCnName + ')'"
                    :value="table.tableName"
                >
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="数据库字段" prop="columnName">
            <el-select v-model="mappingConf.columnName" placeholder="请选择">
                <el-option
                    v-for="column in columnList"
                    :key="column.id"
                    :label="column.fieldName + '(' + column.fieldCnName + ')'"
                    :value="column.fieldName"
                >
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item v-if="activeName == 'item'" label="映射数据库表" prop="mappingTableName">
            <el-select v-model="mappingConf.mappingTableName" placeholder="请选择" @change="mappingTableChange">
                <el-option
                    v-for="table in mappingTableList"
                    :key="table.id"
                    :label="table.tableName + '(' + table.tableCnName + ')'"
                    :value="table.tableName"
                >
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="映射字段" prop="mappingName">
            <el-select v-if="activeName == 'item'" v-model="mappingConf.mappingName" placeholder="请选择">
                <el-option
                    v-for="column in mappingColumnList"
                    :key="column.id"
                    :label="column.fieldName + '(' + column.fieldCnName + ')'"
                    :value="column.fieldName"
                >
                </el-option>
            </el-select>
            <el-input v-if="activeName == 'system'" v-model="mappingConf.mappingName"></el-input>
        </el-form-item>

        <el-form-item v-if="activeName == 'item'" label="对接事项">
            <el-input v-model="dockingItem" :readonly="true"></el-input>
        </el-form-item>
        <el-form-item v-if="activeName == 'system'" label="对接系统">
            <el-input v-model="currInfo.dockingSystem" :readonly="true"></el-input>
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
    import { getColumns, getConfInfo } from '@/api/itemAdmin/item/mappingConfig';
    import { onMounted, watch } from 'vue';
    import { ref } from 'vue-demi';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },
        id: String,
        activeName: String,
        dockingItemName: String
    });
    var dockingItem = ref(props.dockingItemName);

    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        tableList: [],
        columnList: [],
        rules: {
            tableName: { required: true, message: '请选择业务表', trigger: 'blur' },
            columnName: { required: true, message: '请选择字段名称', trigger: 'blur' },
            mappingTableName: { required: true, message: '请选择映射数据库表', trigger: 'blur' },
            mappingName: { required: true, message: '请输入映射字段', trigger: 'blur' }
        },
        mappingItemId: '',
        mappingConf: {},
        mappingTableList: [],
        mappingColumnList: [],
        mappingForm: ''
    });

    let {
        currInfo,
        tableList,
        columnList,
        mappingTableList,
        mappingColumnList,
        mappingConf,
        mappingForm,
        mappingItemId,
        rules
    } = toRefs(data);

    watch(
        () => props.id,
        (newVal) => {
            getInfo();
        }
    );

    onMounted(() => {
        getInfo();
    });

    defineExpose({
        mappingConf,
        mappingForm
    });

    function getInfo() {
        getConfInfo(props.id, currInfo.value.id, props.activeName == 'item' ? currInfo.value.dockingItemId : '').then(
            (res) => {
                tableList.value = res.data.tableList;
                if (props.activeName == 'item') {
                    mappingTableList.value = res.data.mappingTableList;
                }
                if (props.id != '') {
                    mappingConf.value = res.data.itemMappingConf;
                    if (mappingConf.value.tableName != null) {
                        tableChange(mappingConf.value.tableName);
                    }
                    if (props.activeName == 'item') {
                        mappingTableChange(mappingConf.value.mappingTableName);
                    }
                }
            }
        );
    }

    function tableChange(val) {
        getColumns(val).then((res) => {
            columnList.value = res.data;
        });
    }

    function mappingTableChange(val) {
        getColumns(val).then((res) => {
            mappingColumnList.value = res.data;
        });
    }
</script>

<style></style>
