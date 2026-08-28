<template>
    <div
        v-if="tableList.length == 0 && optType != 'custom'"
        style="margin-bottom: 10px; text-align: center; color: red"
    >
        如无数据表可选择，表单请先绑定数据库字段！
    </div>
    <el-form
        ref="viewFormRef"
        :inline-message="true"
        :model="viewFormData"
        :rules="rules"
        :status-icon="true"
        label-width="120px"
    >
        <el-form-item v-if="optType != 'custom'" label="数据库表" prop="tableName">
            <el-select v-model="viewFormData.tableName" placeholder="请选择" @change="tableChange">
                <el-option
                    v-for="table in tableList"
                    :key="table.id"
                    :label="table.tableCnName + '(' + table.tableName + ')'"
                    :value="table.tableName"
                >
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item v-if="optType != 'custom'" label="数据库字段" prop="columnName">
            <el-select v-model="viewFormData.columnName" filterable placeholder="请选择" @change="columnChange">
                <el-option
                    v-for="column in columnList"
                    :key="column.id"
                    :label="column.fieldName + '(' + column.fieldCnName + ')'"
                    :value="column.fieldName"
                >
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item v-if="optType == 'custom'" label="字段名称" prop="columnName">
            <el-input
                v-model="viewFormData.columnName"
                placeholder="请输入或者选择固定字段"
                style="width: 60%"
            ></el-input>
            <el-select v-model="selectValue" placeholder="请选择固定字段" style="width: 40%" @change="selectField">
                <el-option
                    v-for="item in optionData"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value + '-' + item.label"
                >
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="显示名称" prop="disPlayName">
            <el-input v-model="viewFormData.disPlayName"></el-input>
        </el-form-item>
        <el-form-item label="显示宽度" prop="disPlayWidth">
            <el-input v-model="viewFormData.disPlayWidth"></el-input>
        </el-form-item>
        <el-form-item label="显示位置" prop="disPlayAlign">
            <el-select v-model="viewFormData.disPlayAlign" placeholder="请选择">
                <el-option key="left" label="靠左" value="left"></el-option>
                <el-option key="center" label="居中" value="center"></el-option>
                <el-option key="right" label="靠右" value="right"></el-option>
            </el-select>
        </el-form-item>

        <el-form-item v-if="optType == 'table'" label="开启搜索条件" prop="openSearch">
            <el-switch
                v-model="viewFormData.openSearch"
                :active-value="1"
                :inactive-value="0"
                active-text="开启"
                inactive-text="关闭"
                inline-prompt
                @change="switchchange"
            />
        </el-form-item>

        <el-form-item
            v-if="optType == 'table' && viewFormData.openSearch == '1'"
            label="输入框类型"
            prop="inputBoxType"
        >
            <el-select v-model="viewFormData.inputBoxType" placeholder="请选择">
                <el-option key="search" label="文本输入框(带搜索图标)" value="search"></el-option>
                <el-option key="input" label="文本输入框" value="input"></el-option>
                <el-option key="select" label="下拉框" value="select"></el-option>
                <el-option key="date" label="日期" value="date"></el-option>
            </el-select>
        </el-form-item>

        <el-form-item v-if="optType == 'table' && viewFormData.openSearch == '1'" label="搜索框宽度" prop="spanWidth">
            <el-input v-model="viewFormData.spanWidth"></el-input>
        </el-form-item>

        <el-form-item v-if="optType == 'table' && viewFormData.openSearch == '1'" label="搜索名称" prop="labelName">
            <el-input v-model="viewFormData.labelName"></el-input>
        </el-form-item>

        <el-form-item
            v-if="optType == 'table' && viewFormData.inputBoxType == 'select' && viewFormData.openSearch == '1'"
            label="数据字典"
            prop="optionClass"
        >
            <el-input v-model="viewFormData.optionClass" :readonly="true" @click="selectOption"></el-input>
        </el-form-item>
    </el-form>

    <y9Dialog v-model:config="dialogConfig">
        <selectOptionDialog ref="selectOptionRef" />
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { onMounted, reactive, toRefs, watch } from 'vue';
    import { getViewInfo } from '@/api/itemAdmin/item/viewConfig';
    import selectOptionDialog from './selectOption.vue';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },
        id: String,
        optType: String,
        viewType: String
    });

    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        viewFormRef: '',
        viewFormData: {
            itemId: props.currTreeNodeInfo.id,
            viewType: props.viewType,
            tableName: '',
            columnName: '',
            disPlayName: '',
            disPlayWidth: '120',
            disPlayAlign: 'center',
            openSearch: '0',
            inputBoxType: '',
            spanWidth: '',
            labelName: '',
            optionClass: ''
        },
        tableList: [] as any,
        columnList: [] as any,
        tablefield: [] as any,
        rules: {
            tableName: { required: true, message: '请选择业务表' },
            columnName: { required: true, message: '请输入或者选择字段名称' },
            disPlayName: { required: true, message: '请输入显示名称' },
            disPlayWidth: { required: true, message: '请输入显示宽度' },
            disPlayAlign: { required: true, message: '请选择显示位置' }
        },
        selectValue: '',
        optionData: [
            {
                label: '序号',
                value: 'serialNumber'
            },
            {
                label: '操作',
                value: 'opt'
            },
            {
                label: '发起人',
                value: 'creatUserName'
            },
            {
                label: '办理环节',
                value: 'taskName'
            },
            {
                label: '发送人',
                value: 'taskSender'
            },
            {
                label: '发送时间',
                value: 'taskCreateTime'
            },
            {
                label: '办理人',
                value: 'taskAssignee'
            },
            {
                label: '任务创建时间',
                value: 'taskCreateTime'
            },
            {
                label: '事项名称',
                value: 'itemName'
            }
        ],
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    console.log(selectOptionRef.value.currentOptionRow);
                    if (selectOptionRef.value.currentOptionRow == null) {
                        ElMessage({ type: 'error', message: '请选择数据字典', offset: 65 });
                        reject();
                    } else {
                        let optionClass = selectOptionRef.value.currentOptionRow;
                        viewFormData.value.optionClass = optionClass.type + '(' + optionClass.name + ')';
                        resolve();
                    }
                });
            },
            visibleChange: (visible) => {}
        },
        selectOptionRef: ''
    });

    let {
        currInfo,
        selectOptionRef,
        dialogConfig,
        viewFormRef,
        viewFormData,
        tableList,
        columnList,
        rules,
        tablefield,
        selectValue,
        optionData
    } = toRefs(data);

    watch(
        () => props.id,
        (newVal) => {
            getViewInfoDetail();
        }
    );

    onMounted(() => {
        getViewInfoDetail();
        if (viewFormData.value.viewType == 'done') {
            optionData.value.push(
                {
                    label: '办结人',
                    value: 'user4Complete'
                },
                {
                    label: '办结时间',
                    value: 'endTime'
                }
            );
        }
    });

    function getViewInfoDetail() {
        getViewInfo(props.id, props.currTreeNodeInfo.id).then((res) => {
            tableList.value = res.data.tableList;
            tablefield.value = res.data.tablefield;
            console.log('AAAAA' + props.id);
            if (props.id != '') {
                viewFormData.value = res.data.itemViewConf;
                if (viewFormData.value.tableName != null) {
                    tableChange(viewFormData.value.tableName);
                }
            } else {
                //viewFormData.value.tableName = tableList.value.length > 0 ? tableList.value[0].tableName : "";
            }
        });
    }

    function tableChange(val) {
        // getColumns(val,props.currTreeNodeInfo.id).then(res => {
        // 	columnList.value = res.data;
        // });
        tablefield.value.forEach((element) => {
            if (element.tableName == val) {
                columnList.value = element.fieldlist;
            }
        });
    }

    function columnChange(val) {
        let obj = {};
        obj = columnList.value.find((item) => {
            return item.fieldName === val;
        });
        viewFormData.value.disPlayName = obj.fieldCnName;
    }

    function selectField(val) {
        viewFormData.value.columnName = val.split('-')[0];
        viewFormData.value.disPlayName = val.split('-')[1];
    }

    function switchchange(val) {
        if (val == 1) {
            rules.value.inputBoxType = { required: true, message: '请选择输入框类型' };
        } else {
            rules.value.inputBoxType = {};
        }
    }

    function selectOption() {
        Object.assign(dialogConfig.value, {
            show: true,
            width: '30%',
            title: '字典选择',
            showFooter: true
        });
    }

    defineExpose({
        viewFormRef,
        viewFormData
    });
</script>

<style lang="scss" scoped></style>
