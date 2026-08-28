<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-07 16:11:11
 * @LastEditors: qinman
 * @LastEditTime: 2023-10-30 09:25:28
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\views\y9form\table\newOrModifyField.vue
-->
<template>
    <el-form
        ref="fieldFormRef"
        :inline-message="true"
        :model="field"
        :rules="rules"
        :status-icon="true"
        class="newOrModifyField"
    >
        <table border="0" cellpadding="0" cellspacing="1" class="layui-table" lay-skin="line row">
            <tbody>
                <tr>
                    <td class="lefttd" style="width: 25%">字段英文名称<font style="color: red">*</font></td>
                    <td class="rigthtd" colspan="3">
                        <el-form-item prop="fieldName">
                            <el-input v-model="field.fieldName" clearable></el-input>
                        </el-form-item>
                    </td>
                </tr>
                <tr>
                    <td class="lefttd" style="width: 25%">字段中文名称<font style="color: red">*</font></td>
                    <td class="rigthtd" colspan="3">
                        <el-form-item prop="fieldCnName">
                            <el-input v-model="field.fieldCnName" clearable></el-input>
                        </el-form-item>
                    </td>
                </tr>
                <tr>
                    <td class="lefttd" style="width: 25%">字段类型</td>
                    <td class="rigthtd" style="width: 25%">
                        <el-select v-model="field.fieldType">
                            <el-option
                                v-for="item in typeList"
                                :key="item.typeName"
                                :label="item.typeName"
                                :value="item.typeName"
                            ></el-option>
                        </el-select>
                    </td>
                    <td class="lefttd" style="width: 25%">字段长度<font style="color: red">*</font></td>
                    <td class="rigthtd" style="width: 25%">
                        <el-form-item prop="fieldLength">
                            <el-input v-model.number="field.fieldLength"></el-input>
                        </el-form-item>
                    </td>
                </tr>
                <tr>
                    <td class="lefttd" style="width: 25%">是否系统字段</td>
                    <td class="rigthtd" style="width: 25%">
                        <el-select v-model="field.isSystemField">
                            <el-option :key="0" :value="0" label="否"></el-option>
                            <el-option :key="1" :value="1" label="是"></el-option>
                        </el-select>
                    </td>
                    <td class="lefttd" style="width: 25%">是否允许为空</td>
                    <td class="rigthtd" style="width: 25%">
                        <el-select v-model="field.isMayNull">
                            <el-option :key="1" :value="1" label="是"></el-option>
                            <el-option :key="0" :value="0" label="否"></el-option>
                        </el-select>
                    </td>
                </tr>
                <tr>
                    <td class="lefttd" style="width: 25%">是否作为流程变量</td>
                    <td class="rigthtd" colspan="3">
                        <el-select v-model="field.isVar">
                            <el-option :key="0" :value="0" label="否"></el-option>
                            <el-option :key="1" :value="1" label="是"></el-option>
                        </el-select>
                    </td>
                </tr>
            </tbody>
        </table>
    </el-form>
</template>

<script lang="ts" setup>
    import { getFieldInfo, saveField } from '@/api/itemAdmin/y9form';

    const props = defineProps({
        tableId: String,
        fieldId: String,
        fieldList: Array,
        updateField: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return null;
            }
        },
        pushField: Function
    });
    var key = {
        if: 'if',
        else: 'else',
        begin: 'begin',
        end: 'end',
        function: 'function',
        add: 'add',
        having: 'having',
        exists: 'exists',
        table: 'table',
        insert: 'insert',
        into: 'into',
        in: 'in',
        group: 'group',
        delete: 'delete',
        update: 'update',
        field: 'field',
        from: 'from',
        drop: 'drop',
        new: 'new',
        like: 'like',
        load: 'load',
        to: 'to'
    };
    var isBeginWithLetter = (rule, value, callback) => {
        let pattern = new RegExp('^[a-z|A-Z|_]+');
        let b = pattern.test(value);
        if (!b) {
            callback(new Error('字段英文名称必须以字母开头'));
        } else {
            callback();
        }
    };
    var indexOfSymbol = (rule, value, callback) => {
        if (value.indexOf('-') != -1) {
            callback(new Error('字段英文名称包含-符号'));
        } else {
            callback();
        }
    };
    var isValidAscii = (rule, value, callback) => {
        let pattern = new RegExp('^[a-z|A-Z|0-9|_|-]+$');
        let b = pattern.test(value);
        if (!b) {
            callback(new Error('字段英文名称必须全为字母或者数字'));
        } else {
            callback();
        }
    };
    var keyword = (rule, value, callback) => {
        if (key[value] != null) {
            callback(new Error('字段英文名称不能为系统关键字'));
        } else {
            callback();
        }
    };

    const data = reactive({
        title: '',
        field: {},
        oldfieldName: '',
        typeList: [],
        databaseName: '',
        fieldFormRef: '',
        rules: {
            fieldName: [
                { required: true, message: '请输入字段英文名称', trigger: 'blur' },
                { validator: isBeginWithLetter, trigger: 'blur' },
                { validator: isValidAscii, trigger: 'blur' },
                { validator: indexOfSymbol, trigger: 'blur' },
                { validator: keyword, trigger: 'blur' }
            ],
            fieldCnName: [
                { required: true, message: '请输入字段中文名称', trigger: 'blur' },
                { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
            ],
            fieldLength: [
                { type: 'number', min: 1, message: '字段长度必须为数字值且大于0', trigger: 'blur' },
                { required: true, message: '请输入字段长度', trigger: 'blur' }
            ]
        }
    });
    let { title, field, oldfieldName, typeList, databaseName, rules, fieldFormRef } = toRefs(data);

    onMounted(() => {
        getFieldData();
    });

    async function getFieldData() {
        oldfieldName.value = null;
        field.value = { isSystemField: 0, isMayNull: 1, fieldType: '' };
        let res = await getFieldInfo(props.fieldId, props.tableId);
        if (res.success) {
            typeList.value = res.data.typeList;
            if (props.fieldId != '') {
                //已保存的修改
                field.value = res.data.field;
                oldfieldName.value = res.data.field.fieldName;
                title.value = '修改业务表字段';
            } else if (props.updateField != null && props.fieldId == '') {
                //未保存的修改
                field.value = JSON.parse(JSON.stringify(props.updateField));
                field.value.fieldType = props.updateField.fieldType.split('(')[0];
                oldfieldName.value = props.updateField.fieldName;
                title.value = '修改业务表字段';
            } else {
                //新增
                field.value.tableId = props.tableId;
                title.value = '新增业务表字段';
                field.value.fieldType = typeList.value[0].typeName;
            }
            databaseName.value = res.data.databaseName;
        }
    }

    defineExpose({
        validForm,
        saveOrModifyField
    });

    async function validForm() {
        let valid = await fieldFormRef.value.validate((valid) => {
            return valid;
        });
        return valid;
    }

    async function saveOrModifyField() {
        await props.fieldList.forEach((element) => {
            if (oldfieldName.value == null) {
                if (element.fieldName == field.value.fieldName) {
                    ElNotification({
                        title: '失败',
                        message: '字段英文名称已存在，请修改',
                        type: 'error',
                        duration: 2000,
                        offset: 80
                    });
                    return;
                }
            }
        });
        if (props.tableId == '') {
            //新增
            field.value.state = 0;
            if (props.updateField != null) {
                //未保存的修改
                field.value.fieldType = field.value.fieldType + '(' + field.value.fieldLength + ')';
                props.pushField(field.value, 'update');
                return { success: true, msg: '保存成功', type: 'reload' };
            }
            field.value.id = '';
            field.value.opt = 'true';
            field.value.fieldType = field.value.fieldType + '(' + field.value.fieldLength + ')';
            props.pushField(field.value);
            return { success: true, msg: '保存成功', type: 'reload' };
        } else {
            let res = await saveField(field.value);
            return res;
        }
    }
</script>
<style>
    .newOrModifyField .el-form-item {
        margin-bottom: 0;
    }
</style>
<style lang="scss" scoped>
    .layui-table {
        width: 100%;
        border-collapse: collapse;
        border-spacing: 0;

        td {
            position: revert;
            padding: 5px 10px;
            min-height: 32px;
            line-height: 32px;
            font-size: 14px;
            border-width: 1px;
            border-style: solid;
            border-color: #e6e6e6;
            display: table-cell;
            vertical-align: inherit;
        }

        .lefttd {
            background: #f5f7fa;
            text-align: center;
            // margin-right: 4px;
            width: 14%;
        }

        .rightd {
            display: flex;
            flex-wrap: wrap;
            word-break: break-all;
            white-space: pre-wrap;
        }
    }
</style>
