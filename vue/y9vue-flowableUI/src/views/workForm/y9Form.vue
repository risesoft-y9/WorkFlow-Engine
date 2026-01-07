<!-- 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-04-23 15:08:38
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-07 09:47:09
 * @Descripttion: 表单信息
 * @FilePath: \vue\y9vue-flowableUI\src\views\workForm\y9Form.vue
-->
<template>
    <el-container
        v-loading="loading"
        :element-loading-text="$t('正在保存中')"
        :style="style"
        class="y9Form-container"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        element-loading-spinner="el-icon-loading"
    >
        <el-aside class="formaside">
            <fm-generate-form
                id="printTest"
                ref="generateForm"
                :data="formJson"
                :edit="edit"
                :remote="remoteFuncs"
                style="margin: auto"
            >
            </fm-generate-form>
        </el-aside>
    </el-container>
</template>

<script lang="ts" setup>
    import { inject, nextTick, onBeforeUnmount, onMounted, reactive, ref, toRefs } from 'vue';
    import { EventBus } from '@/components/formMaking/util/event-bus';
    import { saveProcessParam } from '@/api/flowableUI/processParam';
    import { saveDraft } from '@/api/flowableUI/draft';
    import { getBindOpinionFrame } from '@/api/flowableUI/opinion';
    import { findByCustomNumber, getTempNumber, saveNumberString } from '@/api/flowableUI/organWord';
    import {
        getAllFieldPerm,
        getCommonDayOrHour,
        getDayOrHour,
        getFormData,
        getFormFieldByFormId,
        getOptionValueList,
        saveFormData
    } from '@/api/flowableUI/form';
    import { formJsonDataList, initFormJsonData } from '../data';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    let style = 'height:calc(100vh - 210px) !important; width: 100%;';
    if (settingStore.pcLayout == 'Y9Horizontal') {
        style = 'height:calc(100vh - 260px) !important; width: 100%;';
    }
    const props = defineProps({
        basicData: {
            type: Object,
            default() {
                return {};
            }
        },
        processInstanceId: String,
        initFormData: {
            type: Object,
            default() {
                return {};
            }
        }
    });
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};

    const emits = defineEmits(['refreshCount', 'fromBindValue', 'oneClickSet']);
    const flowableStore = useFlowableStore();

    let generateForm = ref();

    const data = reactive({
        basicData: props.basicData,
        loading: false,
        formJson: { list: [], config: { size: 'medium' } },
        formId: '',
        edit: true, //表单是否可编辑
        initDataUrl: '', //表单初始化数据路径
        msgType: false, //是否提示保存成功
        bindOpinionFrameList: [] as any, //绑定意见框列表
        remoteFuncs: {
            getOptionData(resolve, optionInfo) {
                //动态获取数据字典
                if (optionInfo != undefined && optionInfo.indexOf('(') > -1) {
                    let str = optionInfo.split('(')[1];
                    let type = str.slice(0, str.length - 1); //数据字典类型标识
                    getOptionValueList(type).then((res) => {
                        if (res.success) {
                            let data = res.data;
                            let option = []; //选项
                            let defaultValue = ''; //默认值
                            for (let obj of data) {
                                let optionObj = {};
                                optionObj.value = obj.code;
                                optionObj.label = obj.name;
                                if (obj.defaultSelected == 1) {
                                    defaultValue = obj.code;
                                }
                                option.push(optionObj);
                            }
                            resolve({ option: option, defaultValue: defaultValue });
                        }
                    });
                }
            }
        },
        fileRequired: false, //附件必传
        fileRequired_change: false, //附件必传是否修改
        fileMessage: '', //附件必传提醒内容
        formFieldUsed: [] as any //表单字段内容使用
    });

    let {
        basicData,
        loading,
        formJson,
        formId,
        edit,
        initDataUrl,
        msgType,
        bindOpinionFrameList,
        remoteFuncs,
        fileRequired,
        fileMessage,
        fileRequired_change,
        formFieldUsed
    } = toRefs(data);

    onMounted(() => {
        EventBus.$on('opinion_Click', (data) => {
            //监听意见框点击事件
            nextTick(() => {
                console.log('clickType', data.clickType);
                if (data.clickType != 'noAction') {
                    emits('oneClickSet', data);
                }
            });
        });

        EventBus.$on('update_number', (data) => {
            //监听编号修改,设置表单编号
            nextTick(() => {
                let obj = '{"' + data.tableField + '":"' + data.value + '"}';
                let resdata = JSON.parse(obj);
                generateForm.value.setData(resdata);
                setTimeout(() => {
                    saveY9Form(false); //shoutongzhicheng-gongxiaoshe使用，更新编号，保存表单
                }, 100);
            });
        });
        EventBus.$on('y9_changeDate', (data) => {
            //有生云，请休假监听日期变化
            nextTick(() => {
                if (
                    basicData.value.itembox == 'add' ||
                    basicData.value.itembox == 'draft' ||
                    basicData.value.taskDefKey == 'faqiren'
                ) {
                    getDayOrHour(data).then((res) => {
                        if (res.success) {
                            generateForm.value.setData({ leaveDuration: res.data });
                        }
                    });
                }
            });
        });
        EventBus.$on('y9_changeCommonDate', (data) => {
            //有生云，请休假监听日期变化
            nextTick(() => {
                if (
                    basicData.value.itembox == 'add' ||
                    basicData.value.itembox == 'draft' ||
                    basicData.value.taskDefKey == 'faqiren' ||
                    basicData.value.taskDefKey == 'shenqingren'
                ) {
                    getCommonDayOrHour(data).then((res) => {
                        if (res.success) {
                            generateForm.value.setData({ shichang: res.data });
                        }
                    });
                }
            });
        });
        EventBus.$on('update_personName', (data) => {
            //人员树选择修改
            //监听人员修改
            nextTick(() => {
                let name = generateForm.value.getValue(data.tableField);
                if (name != '' && name != undefined && name != null) {
                    for (let item of data.value) {
                        if (name.indexOf(item) == -1) {
                            name = name + '，' + item;
                        }
                    }
                } else {
                    name = data.value.join('，');
                }
                let obj = '{"' + data.tableField + '":"' + name + '"}';
                let resdata = JSON.parse(obj);
                console.log('人员更新：' + obj);
                generateForm.value.setData(resdata);
            });
        });
        EventBus.$on('file_required', (data) => {
            //与表单选项联动，附件必填动态修改
            nextTick(() => {
                fileRequired.value = data.required;
                fileRequired_change.value = true;
            });
        });
        EventBus.$on('number_DblClick', (data) => {
            //监听编号框点击事件
            nextTick(() => {
                findByCustomNumber(
                    basicData.value.itemId,
                    basicData.value.processDefinitionId,
                    basicData.value.taskDefKey
                ).then((res) => {
                    if (res.success) {
                        if (res.data.length > 0) {
                            if (res.data[0].hasPermission) {
                                getTempNumber(basicData.value.itemId, res.data[0].custom).then((res) => {
                                    if (res.data.success) {
                                        let obj = '{"' + data.tableField + '":"' + res.data.tempNumber + '"}';
                                        let resdata = JSON.parse(obj);
                                        generateForm.value.setData(resdata);
                                        //generateForm.value.setData({ 'bianhao' : res.data.tempNumber});
                                    } else {
                                        ElMessage({
                                            type: 'error',
                                            message: t(res.data.msg),
                                            appendTo: '.y9Form-container'
                                        });
                                    }
                                });
                            } else {
                                console.log('当前事项流程任务节点未绑定编号配置或者当前人没有权限编号');
                            }
                        }
                    }
                });
            });
        });
    });

    onBeforeUnmount(() => {
        EventBus.$off('opinion_Click');
        EventBus.$off('file_required');
        EventBus.$off('table_removeRow');
        EventBus.$off('update_number');
        EventBus.$off('y9_changeDate');
        EventBus.$off('y9_changeCommonDate');
        EventBus.$off('update_personName');
        EventBus.$off('number_DblClick');
    });

    defineExpose({
        show,
        signOpinion,
        saveY9Form,
        saveY9ProcessParam,
        saveY9Draft,
        saveForm,
        generateForm,
        setNumber
    });

    async function show(fId, oldFormId) {
        edit.value = true;
        formId.value = fId;
        basicData.value.formId = fId;
        let formJsonData = formJsonDataList.value.find((item) => item.formId === formId.value);
        if (formJsonData == undefined) {
            await initFormJsonData(formId.value);
        }
        formJsonData = formJsonDataList.value.find((item) => item.formId === formId.value);
        formJson.value = formJsonData.formJson;
        // initDataUrl.value = formJson.value?.config.initDataUrl != undefined ? formJson.value.config.initDataUrl : '';
        nextTick(() => {
            generateForm.value.refresh();
            let Promise = generateForm.value.getData(false);
            Promise.then(async function (value) {
                let getFormDataRes = await getFormData(fId, basicData.value.processSerialNumber);
                let formData = getFormDataRes.data;
                let initData = props.initFormData;
                if (Object.keys(formData).length > 0) {
                    //数据库有数据
                    for (let key of Object.keys(value)) {
                        if (value[key] != undefined && value[key].toString().indexOf('$_') > -1) {
                            let key0 = value[key].toString().slice(2);
                            if (formData[key] == undefined || formData[key] == '') {
                                //表单数据为空,设置初始化数据
                                formData[key] = initData[key0] == undefined ? '' : initData[key0];
                            }
                        }
                        if (value[key] != undefined && value[key].toString() == '2000-01-01') {
                            //日期初始化数据
                            let Options = generateForm.value.getOptions(key);
                            if (Options != null) {
                                let defaultValueField = Options.defaultValueField;
                                if (defaultValueField != undefined && defaultValueField.indexOf('$_') > -1) {
                                    let key0 = defaultValueField.slice(2);
                                    if (formData[key] == undefined || formData[key] == '') {
                                        //表单数据为空,设置初始化数据
                                        formData[key] = initData[key0] == undefined ? '' : initData[key0];
                                    }
                                }
                            }
                        }
                        // value[key] = formData[key] != '' ? formData[key] : value[key]; //设置前置表单数据
                    }
                    generateForm.value.setData(formData); //设置数据
                } else {
                    //数据库没有数据
                    for (let key of Object.keys(value)) {
                        if (value[key] != undefined && value[key].toString().indexOf('$_') > -1) {
                            let key0 = value[key].toString().slice(2);
                            value[key] = initData[key0] == undefined ? '' : initData[key0];
                        }
                        if (value[key] != undefined && value[key].toString() == '2000-01-01') {
                            //日期初始化数据
                            let Options = generateForm.value.getOptions(key);
                            if (Options != null) {
                                let defaultValueField = Options.defaultValueField;
                                if (defaultValueField != undefined && defaultValueField.indexOf('$_') > -1) {
                                    let key0 = defaultValueField.slice(2);
                                    value[key] = initData[key0] == undefined ? '' : initData[key0];
                                }
                            }
                        }
                    }
                    value.guid = basicData.value.processSerialNumber;
                    value.processInstanceId = basicData.value.processSerialNumber;
                    await generateForm.value.setData(value);
                }
            }).catch(() => {});
            setTimeout(() => {
                //加载意见框,附件列表
                generateForm.value.disabledAll(true); //默认禁用表单所有字段
                if (!formJson.value.config.permissionForm) {
                    //非权限表单
                    let positionId = sessionStorage.getItem('positionId');
                    if (basicData.value.itembox == 'draft' || basicData.value.itembox == 'add') {
                        //草稿启用表单所有字段
                        generateForm.value.disabledAll(false);
                    }
                    if (
                        basicData.value.itembox == 'todo' &&
                        basicData.value.startTaskDefKey.indexOf(basicData.value.taskDefKey) > -1 &&
                        basicData.value.startor == positionId
                    ) {
                        //起草节点可编辑
                        generateForm.value.disabledAll(false); //启用表单所有字段
                    }
                } else {
                    //权限表单
                    if (
                        basicData.value.itembox == 'draft' ||
                        basicData.value.itembox == 'todo' ||
                        basicData.value.itembox == 'add'
                    ) {
                        initFieldPerm(); //字段填写权限
                    }
                }
                initOpinion();
                // initNumber(res1.data);
                initFileList();
                // initAttachmentFileList();
                nextTick(() => {
                    initPersonTree();
                });

                // initPicture(res1.data);

                //获取表单字段，获取字段内容使用情况
                getFormFieldByFormId(basicData.value.formId).then((res) => {
                    if (res.success && res.data) {
                        formFieldUsed.value = [];
                        for (let item of res.data) {
                            if (item.contentUsedFor != '' && item.contentUsedFor != null) {
                                formFieldUsed.value.push(item);
                            }
                        }
                    }
                });
            }, 500);
        });
    }

    function initOpinion() {
        //加载意见框
        let data = {
            itemId: basicData.value.itemId,
            itembox: basicData.value.itembox,
            processDefinitionKey: basicData.value.processDefinitionKey,
            processSerialNumber: basicData.value.processSerialNumber,
            taskDefKey: basicData.value.taskDefKey,
            processInstanceId: basicData.value.processInstanceId,
            taskId: basicData.value.taskId
        };
        getBindOpinionFrame(basicData.value.itemId, basicData.value.processDefinitionId).then((res) => {
            if (res.success) {
                bindOpinionFrameList.value = res.data;
                for (let item of bindOpinionFrameList.value) {
                    let customOpinions = generateForm.value.getComponent(
                        'custom_opinion@' + (item.opinionFrameMark != undefined ? item.opinionFrameMark : item)
                    );
                    if (customOpinions != null) {
                        customOpinions.initOpinion(data);
                    }
                }
            }
        });
    }

    function initPicture(formData) {
        //加载图片显示组件
        let data = {
            itembox: basicData.value.itembox,
            processSerialNumber: basicData.value.processSerialNumber,
            processInstanceId: basicData.value.processInstanceId,
            taskId: basicData.value.taskId
        };
        for (let i = 1; i < 10; i++) {
            let customPicture = generateForm.value.getComponent('custom_picture' + i.toString());
            if (customPicture != null) {
                customPicture.initPicture(data, formData);
            }
        }
    }

    async function setNumber() {
        //设置编号
        let number = null;
        if (basicData.value.itembox == 'add' || basicData.value.itembox == 'draft') {
            let data = {
                processDefinitionId: basicData.value.processDefinitionId,
                processSerialNumber: basicData.value.processSerialNumber,
                itemId: basicData.value.itemId,
                taskDefKey: basicData.value.taskDefKey,
                itembox: basicData.value.itembox
            };
            let formJsonStr = JSON.stringify(formJson.value).toString();
            if (formJsonStr.indexOf('custom_numberButton@') > -1) {
                let str = formJsonStr.split('custom_numberButton@')[1];
                let numberCustom = str.split('"')[0];
                let customNumberButton = generateForm.value.getComponent('custom_numberButton@' + numberCustom);
                if (customNumberButton != null) {
                    await generateForm.value.getData(false).then(async (formData) => {
                        number = await customNumberButton.setNumber(data, formData);
                    });
                }
            }
        }
        return number;
    }

    function initNumber(formData) {
        //加载编号按钮
        if (
            basicData.value.itembox == 'add' ||
            basicData.value.itembox == 'draft' ||
            basicData.value.itembox == 'todo'
        ) {
            let data = {
                processDefinitionId: basicData.value.processDefinitionId,
                processSerialNumber: basicData.value.processSerialNumber,
                itemId: basicData.value.itemId,
                taskDefKey: basicData.value.taskDefKey,
                itembox: basicData.value.itembox
            };
            let formJsonStr = JSON.stringify(formJson.value).toString();
            if (formJsonStr.indexOf('custom_numberButton@') > -1) {
                let str = formJsonStr.split('custom_numberButton@')[1];
                let numberCustom = str.split('"')[0];
                let customNumberButton = generateForm.value.getComponent('custom_numberButton@' + numberCustom);
                if (customNumberButton != null) {
                    customNumberButton.initNumber(data, formData);
                }
            }
        }
    }

    function initFileList() {
        //加载附件框
        let data = {
            itembox: basicData.value.itembox,
            processSerialNumber: basicData.value.processSerialNumber,
            processInstanceId: basicData.value.processInstanceId,
            taskId: basicData.value.taskId
        };
        let customFile = generateForm.value.getComponent('custom_file');
        if (customFile != null) {
            if (!fileRequired_change.value) {
                //附件必填验证没有修改过
                for (let item of formJson.value.list) {
                    if ('custom-file' == item.el) {
                        if (item.rules[0]?.required) {
                            //附件是否必填
                            fileRequired.value = true;
                            fileMessage.value = item.rules[0].message;
                        }
                    }
                }
            }
            customFile.initFileList(data);
        }
    }

    function initAttachmentFileList() {
        //加载附件框
        let data = {
            itembox: basicData.value.itembox,
            processSerialNumber: basicData.value.processSerialNumber,
            processInstanceId: basicData.value.processInstanceId,
            taskId: basicData.value.taskId
        };
        let customFile = generateForm.value.getComponent('custom_attachmentFile');
        if (customFile != null) {
            if (!fileRequired_change.value) {
                //附件必填验证没有修改过
                for (let item of formJson.value.list) {
                    if ('custom-attachmentFile' == item.el) {
                        if (item.rules[0]?.required) {
                            //附件是否必填
                            fileRequired.value = true;
                            fileMessage.value = item.rules[0].message;
                        }
                    }
                }
            }
            console.log('custom_attachmentFile', customFile);

            customFile.initAttachmentFileList(data);
        }
    }

    function initPersonTree() {
        //加载人员选择树
        let data = {
            itembox: basicData.value.itembox,
            processSerialNumber: basicData.value.processSerialNumber,
            processInstanceId: basicData.value.processInstanceId,
            taskId: basicData.value.taskId
        };
        let customPersonTree = generateForm.value.getComponent('custom_personTree');
        if (customPersonTree instanceof Array) {
            customPersonTree.map((item) => {
                item.initPersonTree(data);
            });
        } else {
            if (null != customPersonTree) {
                customPersonTree.initPersonTree(data);
            }
        }
    }

    async function checkNumberOccupy() {
        //验证编号是否被占用,用于保存和发送前验证
        let checkStr = '';
        let numberString = generateForm.value.getValue('bianhao');
        console.log('numberString', numberString);
        if (numberString != undefined && numberString[0] == '0') {
            checkStr = await findByCustomNumber(
                basicData.value.itemId,
                basicData.value.processDefinitionId,
                basicData.value.taskDefKey
            ).then(async (res) => {
                if (res.success) {
                    if (res.data.length > 0) {
                        if (res.data[0].hasPermission) {
                            return await saveNumberString(
                                res.data[0].custom,
                                basicData.value.itemId,
                                numberString,
                                basicData.value.processSerialNumber
                            ).then((res) => {
                                if (res.data.success) {
                                    console.log(res.data.msg);
                                    return '';
                                } else {
                                    return '当前编号已经被占用，请重新双击编号框生成新编号';
                                }
                            });
                        } else {
                            console.log('当前事项流程任务节点未绑定编号配置');
                        }
                    }
                }
            });
        }
        let test = checkStr;
        return test;
    }

    function saveForm(type) {
        saveY9Form(type)
            .then((value) => {
                return saveY9ProcessParam();
            })
            .then((value) => {
                return saveY9Draft();
            });
    }

    function saveY9Form(type) {
        msgType.value = type;
        return new Promise((resolve, reject) => {
            generateForm.value
                .getData(true)
                .then((data) => {
                    if (
                        basicData.value.itembox == 'todo' ||
                        basicData.value.itembox == 'add' ||
                        basicData.value.itembox == 'draft'
                    ) {
                        if (fileRequired.value) {
                            //附件必传验证
                            let customFile = generateForm.value.getComponent('custom_file');
                            let message = fileMessage.value == '' ? '请上传附件' : fileMessage.value;
                            if (customFile != null && customFile.fileTableConfig.tableData.length == 0) {
                                ElMessage({ type: 'error', message: t(message), appendTo: '.y9Form-container' });
                                return reject(new Error(t(message)).message);
                            }
                        }
                        let jsonData = JSON.stringify(data).toString();
                        loading.value = true;
                        saveFormData(formId.value, jsonData)
                            .then((res) => {
                                loading.value = false;
                                if (res.success) {
                                    //保存成功，修改标题数据
                                    flowableStore.$patch({
                                        documentTitle: data.title
                                    });
                                    if (msgType.value) {
                                        ElMessage({
                                            type: 'success',
                                            message: t('保存表单成功'),
                                            appendTo: '.y9Form-container'
                                        });
                                    }
                                } else {
                                    ElMessage({
                                        type: 'error',
                                        message: t('保存表单失败'),
                                        appendTo: '.y9Form-container'
                                    });
                                    reject();
                                }
                                resolve();
                            })
                            .catch(() => {
                                loading.value = false;
                                reject(new Error(t('保存表单发生异常')).message);
                            });
                    } else {
                        resolve();
                    }
                })
                .catch(() => {
                    ElMessage({ type: 'error', message: t('表单验证不通过'), appendTo: '.y9Form-container' });
                    reject(new Error(t('表单验证不通过')).message);
                });
        });
    }

    function saveY9ProcessParam() {
        return new Promise((resolve, reject) => {
            if (
                basicData.value.itembox == 'todo' ||
                basicData.value.itembox == 'add' ||
                basicData.value.itembox == 'draft'
            ) {
                let obj = getTileAndNumberAndLevel();
                let customItem = flowableStore.getCustomItem == undefined ? false : flowableStore.getCustomItem;
                saveProcessParam(
                    basicData.value.itemId,
                    basicData.value.processSerialNumber,
                    basicData.value.processInstanceId,
                    obj.title,
                    obj.number,
                    obj.level,
                    customItem
                )
                    .then((res0) => {
                        //保存流程变量
                        if (!res0.success) {
                            ElMessage({ type: 'error', message: t('保存变量失败'), appendTo: '.y9Form-container' });
                            reject();
                        }
                        resolve();
                    })
                    .catch(() => {
                        ElMessage({ type: 'error', message: t('保存变量发生异常'), appendTo: '.y9Form-container' });
                        reject(new Error(t('保存变量发生异常')).message);
                    });
            } else {
                resolve();
            }
        });
    }

    function saveY9Draft() {
        return new Promise((resolve, reject) => {
            if (basicData.value.itembox == 'add' || basicData.value.itembox == 'draft') {
                let obj = getTileAndNumberAndLevel();
                saveDraft(
                    basicData.value.processSerialNumber,
                    basicData.value.itemId,
                    basicData.value.processDefinitionKey,
                    obj.number,
                    obj.level,
                    obj.title
                )
                    .then((res1) => {
                        if (!res1.success) {
                            ElMessage({ type: 'error', message: t('保存草稿失败'), appendTo: '.y9Form-container' });
                            reject();
                        } else {
                            emits('refreshCount');
                        }
                        resolve();
                    })
                    .catch(() => {
                        ElMessage({ type: 'error', message: t('保存草稿发生异常'), appendTo: '.y9Form-container' });
                        reject(new Error(t('保存草稿发生异常')).message);
                    });
            } else {
                resolve();
            }
        });
    }

    //获取标题，编号，紧急程度
    function getTileAndNumberAndLevel() {
        let title = generateForm.value.getValue('title');
        let number = generateForm.value.getValue('number');
        let level = generateForm.value.getValue('level');
        for (let item of formFieldUsed.value) {
            switch (item.contentUsedFor) {
                case 'title':
                    title = generateForm.value.getValue(item.columnName);
                    break;
                case 'number':
                    number = generateForm.value.getValue(item.columnName);
                    break;
                case 'level':
                    level = generateForm.value.getValue(item.columnName);
            }
        }
        return { title: title, number: number, level: level };
    }

    async function initFieldPerm() {
        //字段权限控制
        let res = await getAllFieldPerm(
            basicData.value.formId,
            basicData.value.taskDefKey,
            basicData.value.processDefinitionId
        );
        if (res.success) {
            let writeField = [];
            for (let item of res.data) {
                writeField.push(item.fieldName);
            }
            if (writeField.length > 0) {
                generateForm.value.clearValidate(writeField); //移除其他字段必填项验证
                generateForm.value.disabled(writeField, false); //开启字段填写
            } else {
                if (formJson.value.config.permissionForm) {
                    //权限表单，当前任务没有配置写权限，则移除所有字段必填验证
                    generateForm.value.clearAllValidate(); //移除所有字段必填项验证
                }
            }
        } else {
            ElMessage({ type: 'error', message: res.msg, appendTo: '.y9Form-container' });
        }
    }

    function signOpinion() {
        //验证必填意见
        let msg = '';
        if (
            basicData.value.itembox == 'add' ||
            basicData.value.itembox == 'draft' ||
            basicData.value.itembox == 'todo'
        ) {
            for (let item of bindOpinionFrameList.value) {
                let customOpinion = generateForm.value.getComponent('custom_opinion@' + item);
                if (customOpinion != null) {
                    let addableObj = customOpinion.addable;
                    let opinionName = customOpinion.opinionName;
                    if (addableObj.addable && addableObj.signOpinion) {
                        //addable添加意见标识，signOpinion必填意见标识
                        if (msg == '') {
                            msg = '请填写【' + opinionName + '】';
                        } else {
                            msg += '<br><br>请填写【' + opinionName + '】';
                        }
                    }
                }
            }
        }
        return msg;
    }
</script>

<style lang="scss" scoped>
    .formaside {
        width: 100%;
        height: auto;
        overflow: auto;
        padding: 1% 0% 2% 0%;
    }

    .ul {
        height: 40px;
        line-height: 40px;
        margin: 0;
        padding-left: 10px;
        border-bottom: 1px solid #aaa;
    }

    .ul li {
        height: 39px;
        line-height: 40px;
        width: 65px;
        display: inline-block;
        margin-right: 8px;
        cursor: pointer;
    }

    .selectClass {
        border-bottom: 2px solid #e6794a;
        color: #e6794a;
    }

    .el-timeline {
        padding-left: 10px;
        padding-top: 20px;
    }

    .ishide {
        display: none;
    }

    /*  .number{
        margin-right: 10px;
        width: 10px;
        text-align: center;
        }
        .el-timeline-item__timestamp{
        margin-left: 17px;
        } */

    /*message */
    .y9Form-container {
        :global(.el-message .el-message__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }

    /* 设置一些级别样式，可根据具体需求修改 */
    .fm-form {
        :deep(.form) {
            padding-left: 10px;
            padding-right: 10px;
        }

        :deep(.fm-form-item) {
            margin-top: 8px;

            .el-form-item__content {
                .el-input__wrapper {
                    border: #e4e7ed solid 1px;
                }

                .el-textarea {
                    border: #e4e7ed solid 1px;
                }
            }
        }

        :deep(.el-form-item__label) {
            line-height: 40px;
            height: 40px;
        }

        :deep(.el-form-item__content) {
            margin: 0px 2px;
        }

        :deep(.suggest) {
            border: #e4e7ed solid 1px;
            padding: 5px 10px;
        }
    }
</style>
