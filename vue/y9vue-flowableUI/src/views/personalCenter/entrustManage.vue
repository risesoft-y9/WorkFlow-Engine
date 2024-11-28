<template>
    <el-drawer v-model="treeDrawer" :size="450" :title="$t('岗位选择')" class="mydrawer" direction="rtl">
        <div style="margin-bottom: 20px">
            {{ $t('委托时间') }}：
            <el-date-picker
                v-model="date"
                :range-separator="$t('至')"
                clearable
                style="width: 300px"
                type="daterange"
                unlink-panels
                value-format="YYYY-MM-DD"
            />
            <div style="margin-top: 10px">
                {{ $t('委托对象') }}：
                <el-input v-model="entrust.assigneeName" :readonly="true" style="width: 300px"></el-input>
            </div>
        </div>
        <selectTree
            ref="selectTreeRef"
            :selectField="selectField"
            :treeApiObj="treeApiObj"
            @onTreeClick="onNodeClick"
        />
        <div slot="footer" class="dialog-footer" style="text-align: center; margin-top: 15px">
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="saveEntrust"
                ><span>{{ $t('保存') }}</span></el-button
            >
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                @click="treeDrawer = false"
                ><span>{{ $t('取消') }}</span></el-button
            >
        </div>
    </el-drawer>
    <div class="margin-bottom-20">
        <el-button
            :size="fontSizeObj.buttonSize"
            :style="{ fontSize: fontSizeObj.baseFontSize }"
            type="primary"
            @click="addEntrust"
            ><i class="ri-add-line"></i>{{ $t('添加') }}
        </el-button>
    </div>
    <y9Table :config="tableConfig">
        <template #used="{ row, column, index }">
            <font v-if="row.used == 0">{{ $t('未开始') }}</font>
            <font v-if="row.used == 1" style="color: green">{{ $t('委托中') }}</font>
            <font v-if="row.used == 2" style="color: red">{{ $t('已过期') }}</font>
        </template>
        <template #opt="{ row, column, index }">
            <i class="ri-edit-line" @click="editEntrust(row)"></i>
            <i class="ri-delete-bin-line" style="margin-left: 10px" @click="delEntrust(row)"></i>
        </template>
    </y9Table>
</template>

<script lang="ts" setup>
    import { computed, inject, onMounted, reactive } from 'vue';
    import type { ElMessage, ElMessageBox } from 'element-plus';
    import {
        deleteEntrust,
        getEntrustList,
        getOrgList,
        getOrgTree,
        saveOrUpdate,
        treeSearch
    } from '@/api/flowableUI/entrustManage';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const data = reactive({
        date: '',
        entrust: {},
        treeDrawer: false,
        tableConfig: {
            //表格配置
            columns: [
                { title: computed(() => t('序号')), type: 'index', width: '60' },
                { title: computed(() => t('委托对象')), key: 'assigneeName', width: 'auto' },
                { title: computed(() => t('开始日期')), key: 'startTime', width: '100' },
                { title: computed(() => t('结束日期')), key: 'endTime', width: '100' },
                { title: computed(() => t('使用状态')), key: 'used', width: '90', slot: 'used' },
                { title: computed(() => t('更新时间')), key: 'updateTime', width: '160' },
                { title: computed(() => t('操作')), key: 'opt', width: '90', slot: 'opt' }
            ],
            tableData: [],
            pageConfig: false,
            border: 0
        },
        selectTreeRef: '',
        treeApiObj: {
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
                    treeType: 'tree_type_org_position'
                }
            }
        },
        treeNode: [], //tree选择的数据
        selectField: [
            //设置需要选择的字段
            {
                fieldName: 'orgType',
                value: ['Position']
            }
        ]
    });

    let { date, tableConfig, treeDrawer, selectTreeRef, treeApiObj, treeNode, selectField, entrust } = toRefs(data);

    function onNodeClick(node) {
        treeNode.value = node;
        if (treeNode.value != null && treeNode.value.orgType == 'Position') {
            entrust.value.assigneeName = treeNode.value.name;
            entrust.value.assigneeId = treeNode.value.id;
        }
    }

    onMounted(() => {
        reloadTable();
    });

    function saveEntrust() {
        if (date.value == '' || date.value == null) {
            ElMessage({ type: 'error', message: t('请选择委托时间'), offset: 65, appendTo: '.margin-bottom-20' });
            return;
        }
        if (
            entrust.value.assigneeName == null ||
            entrust.value.assigneeName == '' ||
            entrust.value.assigneeName == undefined
        ) {
            ElMessage({ type: 'error', message: t('请选择委托岗位'), offset: 65, appendTo: '.margin-bottom-20' });
            return;
        }
        entrust.value.startTime = date.value.toString().split(',')[0];
        entrust.value.endTime = date.value.toString().split(',')[1];
        entrust.value.itemId = 'ALL';
        let jsonData = JSON.stringify(entrust.value).toString();
        saveOrUpdate(jsonData).then((res) => {
            if (res.success) {
                ElMessage({ type: 'success', message: res.msg, appendTo: '.opinionList-container' });
                reloadTable();
                treeDrawer.value = false;
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.opinionList-container' });
            }
        });
    }

    function reloadTable() {
        getEntrustList().then((res) => {
            tableConfig.value.tableData = res.data;
        });
    }

    function addEntrust() {
        treeNode.value = null;
        treeDrawer.value = true;
        date.value = '';
        entrust.value = {};
        setTimeout(() => {
            selectTreeRef.value.onRefreshTree();
        }, 500);
    }

    function editEntrust(row) {
        entrust.value = JSON.parse(JSON.stringify(row));
        date.value = [];
        date.value.push(row.startTime);
        date.value.push(row.endTime);
        treeNode.value = null;
        treeDrawer.value = true;
        setTimeout(() => {
            selectTreeRef.value.onRefreshTree();
        }, 500);
    }

    function delEntrust(row) {
        ElMessageBox.confirm(t('是否删除该委托?'), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info',
            appendTo: '.margin-bottom-20'
        })
            .then(() => {
                deleteEntrust(row.id).then((res) => {
                    if (res.success) {
                        ElMessage({ type: 'success', message: res.msg, offset: 65, appendTo: '.margin-bottom-20' });
                        reloadTable();
                    } else {
                        ElMessage({ type: 'error', message: res.msg, offset: 65, appendTo: '.margin-bottom-20' });
                    }
                });
            })
            .catch(() => {
                ElMessage({ type: 'info', message: t('已取消删除'), offset: 65, appendTo: '.margin-bottom-20' });
            });
    }
</script>

<style>
    .el-tabs__content {
        overflow: auto;
        position: relative;
        height: 600px;
    }

    .mydrawer .el-drawer__header {
        margin-bottom: 0px;
        border-bottom: 1px solid #f4f4f4 !important;
        padding-bottom: 22px !important;
    }
</style>

<style scoped>
    .margin-bottom-20 {
        /*message */

        :global(.el-message .el-message__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        /*messageBox */

        :global(.el-message-box .el-message-box__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        :global(.el-message-box .el-message-box__title) {
            font-size: v-bind('fontSizeObj.largeFontSize');
        }

        :global(.el-message-box .el-message-box__btns button) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }
</style>
