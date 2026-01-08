<template>
    <y9Table :config="tableConfig" :filterConfig="filterConfig">
        <template #addBtn>
            <el-button class="global-btn-main" type="primary" @click="addDynamicRole"
                ><i class="ri-add-line"></i>新增
            </el-button>
            <jsonComps :paramObject="paramObject" :reloadData="getDynamicRoleList"></jsonComps>
        </template>
        <template #kinds="{ row, column, index }">
            <span v-if="row.kinds == 0 || row.kinds == null">无</span>
            <span v-if="row.kinds == 1">部门配置分类</span>
            <span v-if="row.kinds == 2">角色</span>
        </template>
        <template #ranges="{ row, column, index }">
            <span v-if="row.ranges == 0 || row.ranges == null">无限制</span>
            <span v-if="row.ranges == 1">科室</span>
            <span v-if="row.ranges == 2">委办局</span>
        </template>
        <template #useProcessInstanceId="{ row, column, index }">
            <span v-if="row.useProcessInstanceId">流程启动人</span>
            <span v-if="!row.useProcessInstanceId">当前人</span>
        </template>
        <template #opt="{ row, column, index }">
            <el-button class="global-btn-second" size="small" @click="editDynamicRole(row)"
                ><i class="ri-edit-line" link></i>
            </el-button>
            <el-button class="global-btn-second" size="small" @click="delDynamicRole(row)"
                ><i class="ri-delete-bin-line" link></i>
            </el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <NewOrModify ref="newOrModifyRef" :row="row" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { onMounted, reactive } from 'vue';
    import { dynamicRoleList, removeDynamicRole, saveOrUpdate } from '@/api/itemAdmin/dynamicRole';
    import NewOrModify from '@/views/dynamicRole/newOrEdit.vue';
    import JsonComps from '@/components/common/jsonComps.vue';

    const data = reactive({
        tableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '角色名称', key: 'name', width: '200' },
                { title: '种类', key: 'kinds', width: '160', slot: 'kinds' },
                { title: '用户属性', key: 'useProcessInstanceId', width: '150', slot: 'useProcessInstanceId' },
                { title: '类全路径', key: 'classPath', align: 'left' },
                { title: '部门属性', key: 'deptPropCategoryName', width: '100' },
                { title: '角色', key: 'roleName', width: '200' },
                { title: '权限范围', key: 'ranges', width: '100', slot: 'ranges' },
                { title: '操作', slot: 'opt', width: '120' }
            ],
            border: false,
            headerBackground: true,
            tableData: [],
            pageConfig: false //取消分页
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 24,
                    slotName: 'addBtn'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let result = { success: false, msg: '' };
                    let valid = await newOrModifyRef.value.validForm();
                    if (!valid) {
                        reject();
                        return;
                    }
                    let formData = newOrModifyRef.value.dynamicRole;
                    result = await saveOrUpdate(formData);
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        getDynamicRoleList();
                    }
                    resolve();
                });
            },
            visibleChange: (visible) => {}
        },
        row: '',
        newOrModifyRef: '',
        paramObject: { id: '', type: 'dynamicRoleConfig', display: 'flex', jsonBtn: 'all' }
    });

    let { tableConfig, filterConfig, dialogConfig, row, newOrModifyRef, paramObject } = toRefs(data);

    async function getDynamicRoleList() {
        let res = await dynamicRoleList();
        tableConfig.value.tableData = res.data;
    }

    onMounted(() => {
        getDynamicRoleList();
    });

    const addDynamicRole = () => {
        row.value = {};
        Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: '添加动态角色',
            showFooter: true
        });
    };

    const editDynamicRole = (rows) => {
        row.value = rows;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: '编辑动态角色',
            showFooter: true
        });
    };

    const delDynamicRole = (rows) => {
        ElMessageBox.confirm('您确定要删除此动态角色吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                removeDynamicRole(rows.id).then((res) => {
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65 });
                        getDynamicRoleList();
                    } else {
                        ElMessage({ message: res.msg, type: 'error', offset: 65 });
                    }
                });
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: '已取消删除',
                    offset: 65
                });
            });
    };
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
</style>
