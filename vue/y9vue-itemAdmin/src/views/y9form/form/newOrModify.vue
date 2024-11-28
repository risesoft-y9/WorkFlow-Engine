<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-11 11:29:34
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-09 17:12:49
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\y9form\form\newOrModify.vue
-->
<template>
    <el-form
        ref="y9FormRef"
        :inline-message="true"
        :model="form"
        :rules="rules"
        :status-icon="true"
        class="newOrModifyField"
    >
        <table border="0" cellpadding="0" cellspacing="1" class="layui-table" lay-skin="line row">
            <tbody>
                <tr>
                    <td class="lefttd" style="width: 20%">表单名称</td>
                    <td class="rigthtd">
                        <el-form-item prop="formName">
                            <el-input v-model="form.formName" clearable></el-input>
                        </el-form-item>
                    </td>
                </tr>
                <tr>
                    <td class="lefttd" style="width: 20%">表单类型</td>
                    <td class="rigthtd">
                        <el-select v-model="form.formType">
                            <el-option :key="1" :value="1" label="主表单"></el-option>
                            <el-option :key="2" :value="2" label="前置表单"></el-option>
                        </el-select>
                    </td>
                </tr>
                <tr>
                    <td class="lefttd">所属系统</td>
                    <td class="rigthtd">
                        <span>{{ form.systemCnName }}</span>
                    </td>
                </tr>
            </tbody>
        </table>
    </el-form>
</template>

<script lang="ts" setup>

const props = defineProps({
        currInfo: {
            type: Object,
            default: () => {
                return {};
            }
        },
        formdata: {
            type: Object,
            default: () => {
                return {};
            }
        }
    });
    const data = reactive({
        form: {
            systemCnName: props.currInfo.name,
            systemName: props.currInfo.systemName,
            formType: 1
        },
        rules: { formName: { required: true, trigger: 'blur', message: '请输入表单名称' } },
        y9FormRef: ''
    });
    let { form, rules, y9FormRef } = toRefs(data);

    defineExpose({
        validForm,
        form
    });

    onMounted(() => {
        if (props.formdata != null) {
            form.value = props.formdata;
        }
    });

    async function validForm() {
        let valid = await y9FormRef.value.validate((valid) => {
            return valid;
        });
        return valid;
    }
</script>
<style>
    .newOrModifyForm .el-form-item {
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
