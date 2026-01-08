<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-05 16:07:56
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-30 15:47:55
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\views\item\itemForm.vue
-->
<template>
    <table border="0" cellpadding="0" cellspacing="1" class="layui-table" lay-skin="line row">
        <tbody>
            <tr>
                <td class="lefttd" style="width: 15%">唯一标识</td>
                <td class="rigthtd" style="width: 35%">
                    <span>{{ itemForm.id }}</span>
                </td>
                <td class="lefttd" rowspan="4" style="width: 15%">事项图标</td>
                <td rowspan="4" style="width: 35%; text-align: center">
                    <img :src="currInfo.iconData" class="avatar" style="width: 120px; height: 120px" />
                    <br />
                    <template v-if="isEditState">
                        <el-row :gutter="20">
                            <el-col :span="6"></el-col>
                            <el-col :span="6" style="line-height: 1; padding-left: 0">
                                <el-button type="primary" @click="selIcon"
                                    ><i class="ri-image-fill"></i>选择图标</el-button
                                >
                            </el-col>
                            <el-col :span="6">
                                <el-upload
                                    ref="upload"
                                    :auto-upload="true"
                                    :http-request="uploadIcon"
                                    :limit="1"
                                    :show-file-list="false"
                                    action=""
                                >
                                    <el-button type="primary"><i class="ri-upload-line"></i>上传图标</el-button>
                                </el-upload>
                            </el-col>
                            <el-col :span="6"></el-col>
                        </el-row>
                        <el-progress
                            v-if="uploadLoading"
                            :percentage="percentage"
                            :show-text="true"
                            :stroke-width="18"
                            :text-inside="true"
                            class="progress"
                            status="success"
                            type="line"
                        ></el-progress>
                    </template>
                </td>
            </tr>
            <tr>
                <td class="lefttd">事项名称<font color="red">*</font></td>
                <td class="rigthtd">
                    <el-input
                        v-if="isEditState"
                        v-model="itemForm.name"
                        @blur="
                            (e, name) => {
                                checkInput(e, '事项名称');
                            }
                        "
                    />
                    <span v-else>{{ currInfo.name }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">事项类型</td>
                <td class="rigthtd">
                    <el-select v-if="isEditState" v-model="itemForm.type">
                        <el-option
                            v-for="(item, index) in itemTypeList"
                            :key="index"
                            :label="item.name"
                            :value="item.value"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.type == '' ? '' : currInfo.type == 'main' ? '主流程' : '子流程' }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">绑定流程<font color="red">*</font></td>
                <td class="rigthtd">
                    <el-select v-if="isEditState" v-model="itemForm.workflowGuid" placeholder="请选择">
                        <el-option
                            v-for="(item, index) in workflowList"
                            :key="index"
                            :label="item.name"
                            :value="item.id"
                        />
                    </el-select>
                    <span v-else>{{ currInfo.workflowGuid }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">应用Url<font color="red">*</font></td>
                <td class="rigthtd" colspan="3">
                    <el-input
                        v-if="isEditState"
                        v-model="itemForm.appUrl"
                        @blur="
                            (e, name) => {
                                checkInput(e, '应用Url');
                            }
                        "
                    />
                    <span v-else>{{ currInfo.appUrl }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">系统中文名<font color="red">*</font></td>
                <td class="rigthtd">
                    <el-input
                        v-if="isEditState"
                        v-model="itemForm.sysLevel"
                        @blur="
                            (e, name) => {
                                checkInput(e, '系统中文名');
                            }
                        "
                    />
                    <span v-else>{{ currInfo.sysLevel }}</span>
                </td>
                <td class="lefttd">系统英文名<font color="red">*</font></td>
                <td class="rigthtd">
                    <el-input
                        v-if="isEditState"
                        v-model="itemForm.systemName"
                        @blur="
                            (e, name) => {
                                checkInput(e, '系统英文名');
                            }
                        "
                    />
                    <span v-else>{{ currInfo.systemName }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">对接事项</td>
                <td class="rigthtd">
                    <el-select v-if="isEditState" v-model="itemForm.dockingItemId" clearable placeholder="请选择">
                        <el-option v-for="item in itemList" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                    <span v-else>{{ dockingItemName }}</span>
                </td>
                <td class="lefttd">对接系统</td>
                <td class="rigthtd">
                    <el-input v-if="isEditState" v-model="itemForm.dockingSystem" />
                    <span v-else>{{ currInfo.dockingSystem }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">显示提交按钮</td>
                <td class="rigthtd">
                    <el-select v-if="isEditState" v-model="itemForm.showSubmitButton" placeholder="请选择">
                        <el-option v-for="(item, index) in $dictionary().boolean" :label="item.name" :value="item.id" />
                    </el-select>
                    <span v-else>{{ currInfo.showSubmitButton ? '是' : '否' }}</span>
                </td>
                <td class="lefttd">是否定制事项</td>
                <td class="rigthtd">
                    <el-select v-if="isEditState" v-model="itemForm.customItem" placeholder="请选择">
                        <el-option v-for="(item, index) in $dictionary().boolean" :label="item.name" :value="item.id" />
                    </el-select>
                    <span v-else>{{ currInfo.customItem ? '是' : '否' }}</span>
                </td>
            </tr>
            <tr>
                <td class="lefttd">事项管理员</td>
                <td class="rigthtd" colspan="3">
                    <template v-if="isEditState">
                        <el-tag v-for="tag in manager" :key="tag.id" class="mx-1" closable @close="handleClose(tag.id)">
                            {{ tag.name }}
                        </el-tag>
                    </template>
                    <template v-else>
                        <el-tag v-for="tag in manager" :key="tag.id" class="mx-1">
                            {{ tag.name }}
                        </el-tag>
                    </template>
                    <el-button v-if="isEditState" class="global-btn-main" type="primary" @click="addManager">
                        <i class="ri-add-line"></i>
                        <span>添加</span>
                    </el-button>
                </td>
            </tr>
        </tbody>
    </table>
    <y9Dialog v-model:config="iformDialogConfig">
        <selectIcon ref="selectIconRef" :iconData="itemForm.iconData" :iformDialogConfig="iformDialogConfig" />
    </y9Dialog>
    <el-drawer v-model="treeDrawer" class="addManagerdrawer" direction="rtl" title="岗位选择">
        <permTree
            ref="permTreeRef"
            :selectField="selectField"
            :showHeader="true"
            :treeApiObj="treeApiObj"
            @onCheckChange="onCheckChange"
        />
        <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
            <el-button type="primary" @click="saveManager"><span>保存</span></el-button>
            <el-button @click="treeDrawer = false"><span>取消</span></el-button>
        </div>
    </el-drawer>
</template>
<script lang="ts" setup>
    import { reactive, ref } from 'vue';
    import type { UploadInstance } from 'element-plus';
    import axios from 'axios';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    import defaultImage from '@/assets/images/default-image.png';
    import { $keyNameAssign } from '@/utils/object';
    import { $dictionary } from '@/utils/data';
    import { getItemData } from '@/api/itemAdmin/item/item';
    import { getOrgList, getOrgTree, treeSearch } from '@/api/itemAdmin/item/permConfig';

    const props = defineProps({
        isEditState: {
            //是否为编辑状态
            type: Boolean
        },
        itemList: {
            type: Array,
            default: () => {
                return [];
            }
        },
        currInfo: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });
    const upload = ref<UploadInstance>();
    const itemTypeList = [
        { name: '请选择', value: '' },
        { name: '主流程', value: 'main' },
        { name: '子流程', value: 'sub' }
    ];
    const data = reactive({
        itemForm: {
            //新增或编辑人员表单
            id: '',
            name: '',
            type: '',
            workflowGuid: '',
            iconData: '',
            appUrl: '',
            sysLevel: '',
            systemName: '',
            customItem: false,
            showSubmitButton: false,
            nature: '',
            dockingItemId: '',
            dockingSystem: '',
            tabIndex: null
        },
        workflowList: [],
        //弹窗配置
        iformDialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let iconData = selectIconRef.value.iconData;
                    if (iconData == '') {
                        ElNotification({
                            title: '失败',
                            message: '请选择图标',
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                        reject();
                        return;
                    }
                    itemForm.value.iconData = iconData;
                    resolve();
                });
            },
            visibleChange: (visible) => {}
        },
        selectIconRef: '',
        dockingItemName: '',
        permTreeRef: '', //tree实例
        treeDrawer: false,
        selectField: [
            //设置需要选择的字段
            {
                fieldName: 'orgType',
                value: ['Position']
            }
        ],
        treeSelectedData: [], //tree选择的数据
        manager: [],
        treeApiObj: {},
        percentage: 0,
        uploadLoading: false
    });

    let {
        itemForm,
        workflowList,
        iformDialogConfig,
        selectIconRef,
        dockingItemName,
        permTreeRef,
        treeDrawer,
        treeApiObj,
        treeSelectedData,
        selectField,
        manager,
        percentage,
        uploadLoading
    } = toRefs(data);

    watch(
        () => props.isEditState,
        (newVal) => {
            if (newVal) {
                //编辑状态给表单赋值
                $keyNameAssign(itemForm.value, props.currInfo);
                getWorkflowList();
            }
        }
    );

    watch(
        () => props.currInfo,
        (newVal) => {
            if (newVal) {
                //编辑状态给表单赋值
                $keyNameAssign(itemForm.value, props.currInfo);
                dockingItemName.value = '';
                if (props.currInfo.iconData == '' || props.currInfo.iconData == null) {
                    props.currInfo.iconData = defaultImage;
                }
                for (let item of props.itemList) {
                    if (item.id == props.currInfo.dockingItemId) {
                        dockingItemName.value = item.name;
                    }
                }
                getWorkflowList();
            }
        }
    );
    onMounted(() => {
        if (props.currInfo.iconData == '' || props.currInfo.iconData == null) {
            props.currInfo.iconData = defaultImage;
        }
        // if(props.isEditState){
        getWorkflowList();
        // }
        dockingItemName.value = '';
        for (let item of props.itemList) {
            if (item.id == props.currInfo.dockingItemId) {
                dockingItemName.value = item.name;
            }
        }
    });

    async function getWorkflowList() {
        let res = await getItemData(props.currInfo.id);
        if (res.success) {
            if (props.currInfo.id == '' || props.currInfo.id == undefined) {
                itemForm.value.id = res.data.item.id;
                props.currInfo.id = res.data.item.id;

                const baseURL = `${window.location.protocol}//${window.location.host}`;
                itemForm.value.appUrl = baseURL + '/flowableUI/index?itemId=' + res.data.item.id;
                props.currInfo.appUrl = baseURL + '/flowableUI/index?itemId=' + res.data.item.id;
            } else {
                itemForm.value = res.data.item;
            }
            manager.value = res.data.manager != undefined ? res.data.manager : [];
            workflowList.value = res.data.workflowList;
        }
    }

    defineExpose({
        itemForm,
        validForm
    });

    function checkInput(e, name) {
        if (!e.target.value) {
            ElNotification({ title: '失败', message: name + '不能为空', type: 'error', duration: 2000, offset: 80 });
        }
    }

    function message() {
        ElNotification({
            title: '失败',
            message: '表单验证不通过',
            type: 'error',
            duration: 2000,
            offset: 80
        });
    }

    async function validForm() {
        if (!itemForm.value.name) {
            message();
            return false;
        }
        if (!itemForm.value.appUrl) {
            message();
            return false;
        }
        if (!itemForm.value.workflowGuid) {
            message();
            return false;
        }
        if (!itemForm.value.sysLevel) {
            message();
            return false;
        }
        if (!itemForm.value.systemName) {
            message();
            return false;
        }
        return true;
    }

    async function selIcon() {
        if (!props.isEditState) {
            return;
        }
        Object.assign(iformDialogConfig.value, {
            show: true,
            width: '22%',
            title: '图标选择',
            showFooter: true
        });
    }

    const uploadIcon = (params) => {
        percentage.value = 0;
        let formData = new FormData();
        formData.append('files', params.file);

        let config = {
            onUploadProgress: (progressEvent) => {
                //progressEvent.loaded:已上传文件大小,progressEvent.total:被上传文件的总大小
                let percent = ((progressEvent.loaded / progressEvent.total) * 100) | 0;
                percentage.value = percent;
            },
            headers: {
                'Content-Type': 'multipart/form-data',
                Authorization: 'Bearer ' + y9_storage.getObjectItem(settings.siteTokenKey, 'access_token')
            }
        };
        uploadLoading.value = true;
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        axios
            .post(import.meta.env.VUE_APP_CONTEXT + 'vue/item/uploadItemIcon', formData, config)
            .then((res) => {
                loading.close();
                if (res.data.success) {
                    uploadLoading.value = false;
                    props.currInfo.iconData = 'data:image/png;base64,' + res.data.data.iconData;
                    itemForm.value.iconData = 'data:image/png;base64,' + res.data.data.iconData;
                }
                upload.value.clearFiles();
                ElMessage({ type: res.data.success ? 'success' : 'error', message: res.data.msg, offset: 65 });
            })
            .catch((err) => {
                ElMessage({ type: 'error', message: '发生异常', offset: 65 });
            });
    };

    //tree点击选择框时触发
    const onCheckChange = (node, isChecked) => {
        //已经选择的节点
        treeSelectedData.value = permTreeRef.value?.y9TreeRef?.getCheckedNodes(true);
    };

    function addManager() {
        let init = false;
        if (treeApiObj.value.topLevel != undefined) {
            init = true;
        }
        treeSelectedData.value = [];
        treeDrawer.value = true;
        treeApiObj.value = {
            //tree接口对象
            topLevel: getOrgList,
            childLevel: {
                //子级（二级及二级以上）tree接口
                api: getOrgTree,
                params: { treeType: 'tree_type_position' }
            },
            search: {
                api: treeSearch,
                params: {
                    key: '',
                    treeType: 'tree_type_position'
                }
            }
        };
        if (init) {
            setTimeout(() => {
                permTreeRef.value.onRefreshTree();
            }, 500);
        }
    }

    function saveManager() {
        if (treeSelectedData.value.length == 0) {
            ElNotification({ title: '提示', message: '请选择岗位', type: 'info', duration: 2000, offset: 80 });
            return;
        }
        for (let i = 0; i < treeSelectedData.value.length; i++) {
            let add = true;
            for (let element of manager.value) {
                if (element.id == treeSelectedData.value[i].id) {
                    add = false;
                }
            }
            if (add) {
                manager.value.push(treeSelectedData.value[i]);
                if (itemForm.value.nature != null && itemForm.value.nature.length > 0) {
                    itemForm.value.nature = itemForm.value.nature + ';' + treeSelectedData.value[i].id;
                } else {
                    itemForm.value.nature = treeSelectedData.value[i].id;
                }
            }
        }
        treeDrawer.value = false;
    }

    function handleClose(id) {
        let newmanager = [];
        for (let element of manager.value) {
            if (element.id != id) {
                newmanager.push(element);
            }
        }
        manager.value = newmanager;
        let arrId = itemForm.value.nature.split(';');
        let newpositionId = [];
        for (let positionId of arrId) {
            if (positionId != id) {
                newpositionId.push(positionId);
            }
        }
        itemForm.value.nature = newpositionId.length > 0 ? newpositionId.join(';') : '';
    }
</script>
<style lang="scss" scoped>
    .addManagerdrawer {
        :deep(.el-dialog__body) {
            padding: 5px 10px;
        }
        :deep(.el-drawer__header) {
            margin-bottom: 0;
            padding-bottom: 16px;
            border-bottom: 1px solid #eee;
        }

        :deep(.y9-card) {
            box-shadow: none;
        }

        :deep(.el-pagination__sizes) {
            width: 120px !important;
        }
    }

    .layui-table {
        :deep(.el-tag) {
            margin-right: 8px;
        }
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
