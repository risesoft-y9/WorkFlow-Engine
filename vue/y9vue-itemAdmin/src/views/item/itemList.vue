<!--
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-05-05 11:38:27
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 13:49:32
 * @FilePath: \vue\y9vue-itemAdmin\src\views\item\itemList.vue
-->
<template>
    <itemTree
        ref="itemTreeRef"
        class="itemtree"
        :treeApiObj="treeApiObj"
        @onTreeClick="onTreeClick"
        @onDeleteTree="onDeleteTree"
    >
        <template #treeHeaderRight>
            <el-button type="primary" class="global-btn-main" @click="addItem">
                <i class="ri-add-line i_medium"></i>
                <span>新增</span>
            </el-button>
            <el-button class="global-btn-second" @click="orderItem">
                <i class="ri-sort-asc i_medium"></i>
                <span>排序</span>
            </el-button>
        </template>
        <template #rightContainer>
            <div id="loutinav">
                <ul>
                    <li
                        v-for="(item, index) in menuBar"
                        :class="{ active: boxType == item.type }"
                        @click="changeBox(item.type)"
                    >
                        <a :href="item.type">{{ item.name }}</a></li
                    >
                    <li class="last"
                        ><a href="#itemBox" :class="{ active: boxType == '#itemBox' }" @click="changeBox('#itemBox')"
                            ><i class="ri-align-top"></i>顶部</a
                        ></li
                    >
                </ul>
            </div>
            <template v-if="Object.keys(currTreeNodeInfo).length > 0">
                <div id="itemBox" class="topBoxDiv">
                    <baseInfo
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :updateItem="updateItem"
                        :itemList="itemList"
                    ></baseInfo>
                </div>
                <div id="processDefineVersion" class="boxDiv">
                    <processVersionConfig
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionList="processDefinitionList"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                        :selVersion="selVersion"
                    ></processVersionConfig>
                </div>
                <div id="formBox" class="boxDiv">
                    <formConfig
                        ref="formConfigRef"
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionList="processDefinitionList"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                        :selVersion="selVersion"
                    ></formConfig>
                </div>
                <div id="premBox" class="boxDiv">
                    <permConfig
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionId="processDefinitionId"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                    />
                </div>
                <div id="opinionBox" class="boxDiv">
                    <opinionFrameConfig
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionId="processDefinitionId"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                    />
                </div>
                <div id="preFormBox" class="boxDiv">
                    <preFormConfig :currTreeNodeInfo="currTreeNodeInfo" />
                </div>
                <div id="viewBox" class="boxDiv">
                    <viewConfig :currTreeNodeInfo="currTreeNodeInfo" />
                </div>
                <div id="organBox" class="boxDiv">
                    <organWordConfig
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionId="processDefinitionId"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                    />
                </div>
                <div id="printBox" class="boxDiv">
                    <printConfig :currTreeNodeInfo="currTreeNodeInfo"></printConfig>
                </div>
                <div id="signBox" class="boxDiv">
                    <signConfig
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionId="processDefinitionId"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                    />
                </div>
                <div id="routerBox" class="boxDiv">
                    <startNodeConfig
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionId="processDefinitionId"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                    />
                </div>
                <div id="buttonBox" class="boxDiv">
                    <buttonConfig
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionId="processDefinitionId"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                    />
                </div>
                <div id="mappingBindBox" class="boxDiv">
                    <mappingConfig :currTreeNodeInfo="currTreeNodeInfo" :itemList="itemList" />
                </div>
                <div id="dataMoveBox" class="boxDiv">
                    <dataTransfer
                        :currTreeNodeInfo="currTreeNodeInfo"
                        :processDefinitionId="processDefinitionId"
                        :selectVersion="selectVersion"
                        :maxVersion="maxVersion"
                    />
                </div>
            </template>
        </template>
    </itemTree>
    <el-drawer v-model="treeDrawer" direction="rtl" :title="title">
        <el-button-group style="margin-bottom: 10px">
            <el-button type="primary" @click="moveUp"><i class="ri-arrow-up-line"></i>上移</el-button>
            <el-button type="primary" @click="moveDown"><i class="ri-arrow-down-line"></i>下移</el-button>
            <el-button type="primary" @click="saveOrderItem"><span>保存</span></el-button>
        </el-button-group>
        <y9Table :config="itemTableConfig" @on-current-change="handleCurrentChange"></y9Table>
    </el-drawer>
    <y9Dialog v-model:config="listDialogConfig">
        <itemForm ref="itemFormRef" :isEditState="true" :itemList="itemList"></itemForm>
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { onMounted, reactive, ref, toRefs } from 'vue';
    import baseInfo from './baseInfo.vue';
    import itemForm from './itemForm.vue';
    import formConfig from './config/formConfig/formConfig.vue';
    import processVersionConfig from './config/processVersionConfig/processVersionConfig.vue';
    import permConfig from './config/permConfig/permConfig.vue';
    import opinionFrameConfig from './config/opinionFrameConfig/opinionFrameConfig.vue';
    import organWordConfig from './config/organWordConfig/organWordConfig.vue';
    import printConfig from './config/printConfig/printConfig.vue';
    import signConfig from './config/signConfig/signConfig.vue';
    import startNodeConfig from './config/startNodeConfig/startNodeConfig.vue';
    import buttonConfig from './config/buttonConfig/buttonConfig.vue';
    import viewConfig from './config/viewConfig/viewConfig.vue';
    import dataTransfer from './config/dataTransfer/dataTransfer.vue';
    import mappingConfig from './config/mappingConfig/mappingConfig.vue';
    import preFormConfig from './config/preFormConfig/index.vue';

    import { copyItem, deleteItem, getItemList, saveItem, saveOrder } from '@/api/itemAdmin/item/item';
    import { getProcessDefinitionList } from '@/api/itemAdmin/item/itemAdminConfig';

    //tree实例
    let itemTreeRef = ref();

    let formConfigRef = ref();
    //数据
    const data = reactive({
        treeApiObj: {
            //tree接口对象
            topLevel: getItemList
        },
        currTreeNodeInfo: {}, //当前tree节点的信息
        boxType: '#itemBox',
        menuBar: [
            { type: '#itemBox', name: '事项信息' },
            { type: '#processDefineVersion', name: '复制绑定' },
            { type: '#formBox', name: '表单配置' },
            { type: '#premBox', name: '权限配置' },
            { type: '#opinionBox', name: '意见框配置' },
            { type: '#preFormBox', name: '前置表单配置' },
            { type: '#viewBox', name: '视图配置' },
            { type: '#organBox', name: '编号配置' },
            { type: '#printBox', name: '打印配置' },
            { type: '#signBox', name: '签收配置' },
            { type: '#routerBox', name: '路由配置' },
            { type: '#buttonBox', name: '按钮配置' },
            { type: '#mappingBindBox', name: '系统对接' },
            { type: '#dataMoveBox', name: '数据迁移' }
        ],
        //弹窗配置
        listDialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let result = { success: false, msg: '' };
                    let valid = await itemFormRef.value.validForm();
                    if (!valid) {
                        reject();
                        return;
                    }
                    let formData = itemFormRef.value.itemForm;
                    result = await saveItem(JSON.stringify(formData).toString());
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        itemTreeRef.value.onRefreshTree();
                    }
                    resolve();
                });
            },
            visibleChange: (visible) => {
                if (!visible) {
                    listDialogConfig.value.onOkLoading = false;
                }
            }
        },
        itemTableConfig: {
            columns: [
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '事项名称',
                    key: 'name'
                }
            ],
            tableData: [],
            pageConfig: false
        },
        y9ListRef: '', //气泡框的列表实例
        itemFormRef: '',
        processDefinitionId: '',
        selectVersion: 1,
        maxVersion: 1,
        itemList: [],
        processDefinitionList: [],
        treeDrawer: false,
        title: '',
        currentRow: []
    });

    const {
        boxType,
        menuBar,
        treeApiObj,
        currTreeNodeInfo,
        listDialogConfig,
        itemFormRef,
        processDefinitionId,
        selectVersion,
        maxVersion,
        itemList,
        processDefinitionList,
        treeDrawer,
        title,
        itemTableConfig,
        currentRow
    } = toRefs(data);

    onMounted(() => {
        itemList.value = itemTreeRef.value.getTreeData();
    });

    //点击tree的回调
    async function onTreeClick(currTreeNode) {
        let res = await getProcessDefinitionList(currTreeNode.workflowGuid);
        if (res.success) {
            processDefinitionList.value = res.data;
            if (processDefinitionList.value.length > 0) {
                maxVersion.value = processDefinitionList.value[0].version;
                selectVersion.value = processDefinitionList.value[0].version;
                processDefinitionId.value = processDefinitionList.value[0].id;
            }
        }
        currTreeNode.processDefinitionId = processDefinitionId.value;
        currTreeNodeInfo.value = currTreeNode;
    }

    function changeBox(type) {
        boxType.value = type;
        closeExpand();
    }

    function updateItem() {
        itemTreeRef.value.onRefreshTree();
        itemList.value = itemTreeRef.value.getTreeData();
    }

    function addItem() {
        Object.assign(listDialogConfig.value, {
            show: true,
            title: '新增事项',
            showFooter: true
        });
    }

    async function copyItemInfo() {
        if (currTreeNodeInfo.value.id) {
            ElMessageBox.confirm(
                `您确定复制【${currTreeNodeInfo.value.name}】?此功能将复制该选中事项所有绑定配置，除配置的角色和权限外。`,
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'info'
                }
            )
                .then(async () => {
                    let result = { success: false, msg: '' };
                    result = await copyItem(currTreeNodeInfo.value.id);
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        itemTreeRef.value.onRefreshTree();
                    }
                })
                .catch((e) => {
                    ElMessage({
                        type: 'info',
                        message: '已取消复制',
                        offset: 65
                    });
                });
        } else {
            ElMessage({
                type: 'info',
                message: '请选择事项',
                offset: 65
            });
        }
    }

    async function orderItem() {
        treeDrawer.value = true;
        title.value = '事项排序';
        let res = await getItemList();
        if (res.success) {
            itemTableConfig.value.tableData = res.data;
        }
    }

    function handleCurrentChange(val) {
        currentRow.value = val;
    }

    const moveUp = () => {
        //上移
        if (currentRow.value.length == 0) {
            ElNotification({
                title: '操作提示',
                message: '请点击选中一条数据',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }

        let index = 0;
        for (let i = 0; i < itemTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == itemTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            let upRow = itemTableConfig.value.tableData[index - 1];
            let currRow = itemTableConfig.value.tableData[index];
            let tabIndex = upRow.tabIndex;
            upRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            itemTableConfig.value.tableData.splice(index - 1, 1);
            itemTableConfig.value.tableData.splice(index, 0, upRow);
        } else {
            ElNotification({
                title: '操作提示',
                message: '已经是第一条，不可上移',
                type: 'error',
                duration: 2000,
                offset: 80
            });
        }
    };

    const moveDown = () => {
        //下移
        if (currentRow.value.length == 0) {
            ElNotification({ title: '操作提示', message: '请选择数据', type: 'error', duration: 2000, offset: 80 });
            return;
        }

        let index = 0;
        for (let i = 0; i < itemTableConfig.value.tableData.length; i++) {
            if (currentRow.value.id == itemTableConfig.value.tableData[i].id) {
                index = i;
                break;
            }
        }
        if (index + 1 == itemTableConfig.value.tableData.length) {
            ElNotification({
                title: '操作提示',
                message: '已经是最后一条，不可下移',
                type: 'error',
                duration: 2000,
                offset: 80
            });
        } else {
            let downRow = itemTableConfig.value.tableData[index + 1];
            let currRow = itemTableConfig.value.tableData[index];
            let tabIndex = downRow.tabIndex;
            downRow.tabIndex = currRow.tabIndex;
            currRow.tabIndex = tabIndex;
            itemTableConfig.value.tableData.splice(index + 1, 1);
            itemTableConfig.value.tableData.splice(index, 0, downRow);
        }
    };

    function saveOrderItem() {
        let ids = [];
        for (let item of itemTableConfig.value.tableData) {
            ids.push(item.id + ':' + item.tabIndex);
        }
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOrder(ids.toString()).then((res) => {
            loading.close();
            if (res.success) {
                ElNotification({ title: '操作提示', message: res.msg, type: 'success', duration: 2000, offset: 80 });
                itemTreeRef.value.onRefreshTree();
                treeDrawer.value = false;
            } else {
                ElNotification({ title: '操作提示', message: res.msg, type: 'error', duration: 2000, offset: 80 });
            }
        });
    }

    function onDeleteTree(data) {
        ElMessageBox.confirm(`是否删除【${data.name}】?`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })
            .then(async () => {
                let result = { success: false, msg: '' };
                result = await deleteItem(data.id);
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if (result.success) {
                    itemTreeRef.value.onRefreshTree();
                }
            })
            .catch((e) => {
                ElMessage({
                    type: 'info',
                    message: '已取消删除',
                    offset: 65
                });
            });
    }

    //选择流程版本
    function selVersion(processDefinitionId1, selectVersion1) {
        processDefinitionId.value = processDefinitionId1;
        selectVersion.value = selectVersion1;
        currTreeNodeInfo.value.processDefinitionId = processDefinitionId1;
    }

    function closeExpand() {
        formConfigRef.value.closeExpand();
    }
</script>

<style lang="scss">
    .y9-dialog .y9-dialog-content {
        padding: 26px 26px !important;
    }

    .i_medium {
        font-size: medium;
    }

    #loutinav {
        width: auto;
        position: fixed;
        top: 125px;
        right: 17.5px;
        z-index: 0;
    }

    #loutinav ul li {
        width: 100px;
        height: 35px;
        border-bottom: 1px dotted #dddddd;
        list-style: none;
        font-size: 14px;
        text-align: center;
        position: relative;
        cursor: pointer;
        padding: 10px 0;
        background: #ffffff;
        color: #fff;
        &.active {
            background: var(--el-color-primary);
            color: #fff;
            display: block;
        }
    }
    #loutinav ul {
        padding: 0;
    }
    #loutinav ul li a {
        width: 100px;
        height: 35px;
        padding: 10px 0;
        position: absolute;
        top: 0;
        left: 0;
        color: #303133;
        text-decoration: none;
    }

    #loutinav ul li.last {
        background: rgb(0 0 0 / 6%);
        color: #fff;
        border-bottom: 1px solid #ddd;
    }

    #loutinav ul li.active a {
        background: var(--el-color-primary);
        color: #fff;
        display: block;
    }

    #loutinav ul li:hover a {
        background: var(--el-color-primary);
        color: #fff;
        display: block;
    }

    .right-container[data-v-36f781e4] .y9-card {
        margin-bottom: 35px;
    }

    .boxDiv {
        margin-top: 35px;
        width: calc(100% - 100px);
    }

    .topBoxDiv {
        width: calc(100% - 100px);
    }

    .boxDiv .el-table {
        //  overflow-y: auto;
    }
    .topBoxDiv .el-table {
        //  overflow-y: auto;
    }
</style>
<style lang="scss" scoped>
    .el-button {
        padding: 8px 10px !important;
    }

    :deep(.y9-card .y9-card-content) {
        scrollbar-width: none;
    }
</style>
