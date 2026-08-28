<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-07-18 14:51:00
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 10:16:50
-->
<template>
    <el-form ref="dynamicRoleForm" :model="dynamicRole" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name">
            <el-input v-model="dynamicRole.name" clearable></el-input>
        </el-form-item>
        <el-form-item label="种类" prop="kinds">
            <el-radio v-model="dynamicRole.kinds" :label="0" @change="handleChange" style="margin-left: 15px"
                >无</el-radio
            >
            <el-radio v-model="dynamicRole.kinds" :label="1" @change="handleChange">部门属性</el-radio>
            <el-radio v-model="dynamicRole.kinds" :label="2" @change="handleChange">静态角色</el-radio>
        </el-form-item>
        <el-form-item label="用户属性" prop="useProcessInstanceId">
            <el-radio v-model="dynamicRole.useProcessInstanceId" :label="false">当前用户</el-radio>
            <el-radio v-model="dynamicRole.useProcessInstanceId" :label="true" style="margin-left: 15px"
                >流程启动者</el-radio
            >
        </el-form-item>
        <el-form-item label="类路径" prop="classPath">
            <el-select v-model="dynamicRole.classPath" placeholder="请选择" filterable>
                <el-option v-for="cp in classPaths" :key="cp" :label="cp" :value="cp"> </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="部门属性" prop="deptPropCategory" v-if="dynamicRole.kinds == 1">
            <el-select v-model="dynamicRole.deptPropCategory" placeholder="请选择" filterable>
                <el-option v-for="dpc in deptPropCategorys" :key="dpc.code" :label="dpc.name" :value="dpc.code">
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="roleId" v-if="dynamicRole.kinds == 2">
            <el-select v-model="dynamicRole.roleId" placeholder="请选择" filterable>
                <el-option v-for="role in roles" :key="role.id" :label="role.name" :value="role.id"> </el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="权限范围" prop="ranges" v-if="dynamicRole.kinds == 1 || dynamicRole.kinds == 2">
            <el-radio v-model="dynamicRole.ranges" :label="0" style="margin-left: 15px" v-if="dynamicRole.kinds == 2"
                >无限制</el-radio
            >
            <el-radio v-model="dynamicRole.ranges" :label="1">科室</el-radio>
            <el-radio v-model="dynamicRole.ranges" :label="2">委办局</el-radio>
        </el-form-item>
        <el-form-item label="描述" prop="description">
            <el-input
                v-model="dynamicRole.description"
                :rows="2"
                maxlength="500"
                resize="none"
                show-word-limit
                type="textarea"
            ></el-input>
        </el-form-item>
    </el-form>
</template>
<script lang="ts" setup>
    import { onMounted, reactive, ref, toRefs } from 'vue';
    import { FormRules } from 'element-plus';
    import { deptPropCategory, getClasses, getDynamicRole, publicRole } from '@/api/itemAdmin/dynamicRole';

    const rules = reactive<FormRules>({
        name: { required: true, message: '请输入名称', trigger: 'blur' },
        classPath: { required: true, message: '请输入类路径', trigger: 'blur' }
    });
    const props = defineProps({
        row: {
            type: Object,
            default: () => {
                return {};
            }
        }
    });
    let dynamicRoleForm = ref();
    const data = reactive({
        dynamicRole: {
            id: '',
            name: '',
            classPath: '',
            kinds: 0,
            ranges: 1,
            roleId: '',
            deptPropCategory: '',
            useProcessInstanceId: false,
            description: ''
        },
        deptPropCategorys: [],
        roles: [],
        classPaths: []
    });

    let { dynamicRole, deptPropCategorys, roles, classPaths } = toRefs(data);

    onMounted(() => {
        if (props.row.id != undefined) {
            getDynamicRole(props.row.id).then((res) => {
                dynamicRole.value = res.data;
            });
        }
        getDeptPropCategory();
        getRoles();
        getClassPaths();
    });

    defineExpose({
        dynamicRole,
        validForm
    });

    async function validForm() {
        let valid = await dynamicRoleForm.value.validate((valid) => {
            return valid;
        });
        return valid;
    }

    async function getDeptPropCategory() {
        let ret = await deptPropCategory();
        deptPropCategorys.value = ret.data.map((item) => ({
            ...item,
            code: parseInt(item.code, 10)
        }));
    }

    async function getRoles() {
        let ret = await publicRole();
        roles.value = ret.data;
    }

    async function getClassPaths() {
        let ret = await getClasses();
        classPaths.value = ret.data;
    }

    function handleChange(value) {
        if (value === 1) {
            dynamicRole.value.classPath = 'net.risesoft.service.dynamicrole.impl.v1.DeptPropCategory';
        } else if (value === 2) {
            dynamicRole.value.classPath = 'net.risesoft.service.dynamicrole.impl.v1.RoleFilter';
        } else {
            dynamicRole.value.classPath = '';
        }
    }
</script>
<style lang="scss">
    .addOrEditDynamicRole .text-center {
        text-align: center;
    }
</style>
