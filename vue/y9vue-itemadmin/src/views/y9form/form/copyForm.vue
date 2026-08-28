<template>
    <el-form
        ref="copyFormDialogRef"
        :inline-message="true"
        :model="copyFormData"
        :rules="rules"
        :status-icon="true"s
        label-width="180px"
    >
        <el-form-item label="选择复制事项" prop="systemName">
            <el-select v-model="copyFormData.systemName" placeholder="请选择复制的事项系统" @change="itemChange">
                <el-option
                    v-for="item in itemList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.systemName"
                >
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="选择复制表单" prop="copyFormId">
            <el-select v-model="copyFormData.copyFormId" filterable placeholder="请选择复制的表单">
                <el-option
                    v-for="form in formList"
                    :key="form.id"
                    :label="form.formName"
                    :value="form.id"
                >
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="绑定业务表" prop="tableName">
            <el-select v-model="copyFormData.tableName" filterable placeholder="请选择当前事项系统建立的业务表">
                <el-option
                    v-for="table in tableList"
                    :key="table.id"
                    :label="table.tableCnName + '(' + table.tableName + ')'"
                    :value="table.tableName"
                >
                </el-option>
            </el-select>
        </el-form-item>
    </el-form>
</template>
<script lang="ts" setup>
import {
    getFormList,
    getAppList,
    getTables,
} from '@/api/itemAdmin/y9form';

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
        itemList:[],
        formList:[],
        tableList:[],
        copyFormDialogRef:'',
        copyFormData:{},
        rules: {
            systemName: { required: true, message: '请选择复制的事项系统' },
            copyFormId: { required: true, message: '请选择复制的表单' },
            tableName: { required: true, message: '请选择当前表单绑定的业务表' },
        },
    });

    let {copyFormDialogRef,copyFormData,itemList,formList,tableList,rules,} =
        toRefs(data);

    onMounted(() => {
        initLoad();
    });
    function initLoad(){
        loadItem();
        loadTable();
    }

    async function itemChange(row){
        console.log('ooooooooooooo',row);
        let res = await getFormList(row,1,500);
        if(res.success){
            formList.value = res.rows;
        }
    }

    async function loadTable(){
        let res = await getTables(props.currInfo.systemName,1,500);
        if(res.success){
            tableList.value = res.rows;
        }
    }

    async function loadItem(){
        let res = await getAppList();
        if(res.success){
            itemList.value = res.data.filter(item => item.name !== '系统列表');
        }
    }

    defineExpose({
        copyFormData,
        copyFormDialogRef
    });

</script>