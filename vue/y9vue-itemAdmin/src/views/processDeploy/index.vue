<!--
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-05-05 11:38:27
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-10 17:07:18
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-processAdmin\src\views\processDeploy\index.vue
-->
<template>
    <y9Table :config="deployTableConfig" :filterConfig="filterConfig">
        <template #deployProcess>
            <el-upload
                :before-upload="beforeAvatarUpload"
                :http-request="saveFile"
                :show-file-list="false"
                action=""
                name="file"
                style="display: inline-block"
            >
                <el-button type="primary"><i class="ri-database-2-line" />部署</el-button>
                <font style="font-family: 微软雅黑; font-size: 14px; color: red; margin-left: 5px"
                    >(文件格式xml/zip/bpmn)</font
                >
            </el-upload>
        </template>
        <template #pStatus="{ row, column, index }">
            <font v-if="row.suspended" style="color: red">挂起</font>
            <font v-else style="color: #67c23a">激活</font>
        </template>
        <template #opt_button="{ row, column, index }">
            <el-button class="global-btn-second" size="small" @click="showGraphTrace(row)"
                ><i class="ri-image-line" title="流程图"></i>流程图
            </el-button>
            <el-button v-if="!row.suspended" class="global-btn-second" size="small" @click="suspend(row)"
                ><i class="ri-pause-circle-line" title="挂起"></i>挂起
            </el-button>
            <el-button v-else class="global-btn-second" size="small" @click="active(row)"
                ><i class="ri-play-circle-line" title="激活"></i>激活
            </el-button>
            <el-button class="global-btn-second" size="small" @click="delDeploy(row)"
                ><i class="ri-delete-bin-line" title="删除"></i>删除
            </el-button>
        </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
        <GraphTrace ref="GraphTraceChild" :processDefinitionId="processDefinitionId" />
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { onMounted, reactive } from 'vue';
    import type { ElLoading, ElMessage, ElMessageBox, UploadProps } from 'element-plus';
    import GraphTrace from '@/views/processDeploy/graphTrace.vue';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings.ts';
    import { deleteDeploy, deploy, getDeployList, switchSuspendOrActive } from '@/api/processAdmin/processDeploy';

    const data = reactive({
        processDefinitionId: '',
        actionUrl:
            import.meta.env.VUE_APP_CONTEXT +
            'vue/repository/deploy?access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token'),
        deployTableConfig: {
            //人员列表表格配置
            columns: [
                { title: '序号', type: 'index', width: '60' },
                { title: '名称', key: 'name' },
                { title: '流程定义key', key: 'key', width: '200' },
                { title: '版本', key: 'version', width: '100' },
                { title: '部署时间', key: 'deploymentTime', width: '180' },
                { title: '状态', key: 'suspended', width: '180', slot: 'pStatus' },
                { title: '操作', width: '280', slot: 'opt_button' }
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
                    slotName: 'deployProcess'
                }
            ]
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {}
        }
    });

    let { actionUrl, processDefinitionId, filterConfig, deployTableConfig, dialogConfig } = toRefs(data);

    onMounted(() => {
        getTableList();
    });

    async function getTableList() {
        getDeployList().then((res) => {
            if (res.success) {
                deployTableConfig.value.tableData = res.data;
            }
        });
    }

    function showGraphTrace(row) {
        processDefinitionId.value = row.id;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: '流程图【' + row.name + '】',
            showFooter: false
        });
    }

    function suspend(row) {
        ElMessageBox.confirm('是否挂起【' + row.name + '】?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                switchSuspendOrActive('suspend', row.id).then((res) => {
                    ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
                    loading.close();
                    if (res.success) {
                        getTableList();
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: '已取消删除', offset: 65 });
            });
    }

    function active(row) {
        ElMessageBox.confirm('是否激活【' + row.name + '】?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                switchSuspendOrActive('active', row.id).then((res) => {
                    ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
                    loading.close();
                    if (res.success) {
                        getTableList();
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: '已取消删除', offset: 65 });
            });
    }

    function delDeploy(row) {
        ElMessageBox.confirm('是否删除流程定义【' + row.name + '】?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
                deleteDeploy(row.deploymentId).then((res) => {
                    ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
                    loading.close();
                    if (res.success) {
                        getTableList();
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: '已取消删除', offset: 65 });
            });
    }

    const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
        if (
            rawFile.name.indexOf('.xml') == -1 &&
            rawFile.name.indexOf('.zip') == -1 &&
            rawFile.name.indexOf('.bpmn') == -1
        ) {
            ElMessage({ type: 'error', message: '请上传指定格式的文件!', offset: 65 });
            return false;
        }
        return true;
    };

    function saveFile(params) {
        //部署流程
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        deploy(params.file)
            .then((res) => {
                loading.close();
                ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
                if (res.success) {
                    getTableList();
                }
            })
            .catch(() => {
                loading.close();
                ElMessage({ type: 'error', message: '发生异常', offset: 65 });
            });
    }
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
</style>
