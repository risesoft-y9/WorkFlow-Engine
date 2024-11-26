<template>
    <el-container v-loading="loading"
                  :element-loading-text="$t('正在保存中')"
                  :style="style"
                  class="newForm-container"
                  element-loading-background="rgba(0, 0, 0, 0.8)"
                  element-loading-spinner="el-icon-loading"
    >
        <el-aside class="formaside"
                  style="width: 80%;height: auto;overflow: auto;padding: 1% 0% 2% 0%;margin-left: 10%;">
            <fm-generate-form
                id="printTest"
                ref="generateForm"
                :data="formJson"
                :edit="edit"
                :remote="remoteFuncs"
                style="margin: auto;"
            >
            </fm-generate-form>
        </el-aside>
    </el-container>
</template>

<script lang="ts" setup>
import {defineProps, inject, nextTick, onBeforeUnmount, onMounted, reactive} from 'vue';
import {ElMessage} from 'element-plus';
import {EventBus} from '@/components/formMaking/util/event-bus'
import {saveProcessParam} from "@/api/flowableUI/processParam";
import {saveDraft} from "@/api/flowableUI/draft";
import {getBindOpinionFrame} from "@/api/flowableUI/opinion";
import {
    delChildTableRow,
    getAllFieldPerm,
    getChildTableData,
    getDayOrHour,
    getFormData,
    getFormInitData,
    getFormJson,
    getOptionValueList,
    saveChildTableData,
    saveFormData
} from "@/api/flowableUI/form";
import {useFlowableStore} from "@/store/modules/flowableStore";
import y9_storage from '@/utils/storage';
import {useSettingStore} from "@/store/modules/settingStore";
import {useI18n} from 'vue-i18n';

const {t} = useI18n();
const settingStore = useSettingStore()
let style = 'height:calc(100vh - 210px) !important; width: 100%;';
if (settingStore.pcLayout == 'Y9Horizontal') {
    style = 'height:calc(100vh - 260px) !important; width: 100%;';
}
const props = defineProps({
    basicData: Object,
    processInstanceId: String,
})
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo') || {};

const emits = defineEmits(['refreshCount'])
const flowableStore = useFlowableStore()
const data = reactive({
    generateForm: '',
    basicData: props.basicData,
    loading: false,
    formJson: {"list": [], "config": {}},
    formId: '',
    edit: true,//表单是否可编辑
    initDataUrl: "",//表单初始化数据路径
    msgType: false,//是否提示保存成功
    bindOpinionFrameList: [],//绑定意见框列表
    remoteFuncs: {
        getOptionData(resolve, optionInfo) {//动态获取数据字典
            if (optionInfo.indexOf("(") > -1) {
                let str = optionInfo.split("(")[1];
                let type = str.slice(0, str.length - 1);//数据字典类型标识
                getOptionValueList(type).then(res => {
                    if (res.success) {
                        let data = res.data;
                        let option = [];//选项
                        let defaultValue = "";//默认值
                        for (let obj of data) {
                            let optionObj = {};
                            optionObj.value = obj.code;
                            optionObj.label = obj.name;
                            if (obj.defaultSelected == 1) {
                                defaultValue = obj.code;
                            }
                            option.push(optionObj);
                        }
                        resolve({option: option, defaultValue: defaultValue});
                    }
                });
            }
        },
    },
    fileRequired: false,//附件必传
    fileRequired_change: false,//附件必传是否修改
    fileMessage: '',//附件必传提醒内容
});

let {
    generateForm, basicData, loading, formJson, formId, edit, initDataUrl, msgType,
    bindOpinionFrameList, remoteFuncs, fileRequired, fileMessage, fileRequired_change
} = toRefs(data);

onMounted(() => {
    EventBus.$on('update_number', (data) => {//监听编号修改,设置表单编号
        nextTick(() => {
            let obj = '{"' + data.tableField + '":"' + data.value + '"}';
            let resdata = JSON.parse(obj);
            generateForm.value.setData(resdata);
            setTimeout(() => {
                saveY9Form(false);//shoutongzhicheng-gongxiaoshe使用，更新编号，保存表单
            }, 100);
        });
    });
    EventBus.$on('table_removeRow', (data) => {//监听子表删除行事件
        nextTick(() => {
            if (data.guid != "") {
                delChildTableRow(formId.value, data.tableId, data.guid).then(res => {
                    if (!res.success) {
                        ElMessage({
                            type: 'error',
                            message: t("删除子表数据失败"),
                            offset: 65,
                            appendTo: '.newForm-container'
                        });
                        initChildTable(data.tableName);
                    }
                });
            }
        });
    });
    EventBus.$on('y9_changeDate', (data) => {//有生云，请休假监听日期变化
        nextTick(() => {
            if (basicData.value.itembox == "add" || basicData.value.itembox == "draft" || basicData.value.taskDefKey == "faqiren") {
                getDayOrHour(data).then(res => {
                    if (res.success) {
                        generateForm.value.setData({"leaveDuration": res.data});
                    }
                });
            }
        });
    });
    EventBus.$on('update_personName', (data) => {//人员树选择修改
        //监听人员修改
        nextTick(() => {
            let name = generateForm.value.getValue(data.tableField);
            if (name != '' && name != undefined && name != null) {
                for (let item of data.value) {
                    if (name.indexOf(item) == -1) {
                        name = name + "，" + item;
                    }
                }
            } else {
                name = data.value.join("，");
            }
            let obj = '{"' + data.tableField + '":"' + name + '"}';
            let resdata = JSON.parse(obj);
            console.log("人员更新：" + obj);
            generateForm.value.setData(resdata);
        });
    });
    EventBus.$on('file_required', (data) => {//与表单选项联动，附件必填动态修改
        nextTick(() => {
            fileRequired.value = data.required;
            fileRequired_change.value = true;
        });
    });
});

onBeforeUnmount(() => {
    EventBus.$off('file_required');
    EventBus.$off('table_removeRow');
    EventBus.$off('update_number');
    EventBus.$off('y9_changeDate');
    EventBus.$off('update_personName');
})

defineExpose({
    show,
    signOpinion,
    saveChangeOpinion,
    saveY9Form,
    saveChildTable,
    saveY9ProcessParam,
    saveY9Draft,
    saveForm,
    generateForm,
    setNumber
})

function show(fId) {
    edit.value = true;
    formId.value = fId;
    getFormJson(fId).then(res => {//表单显示
        if (res.success) {
            if (res.data != null) {
                formJson.value = JSON.parse(res.data);
            }
            initDataUrl.value = formJson.value?.config.initDataUrl != undefined ? formJson.value.config.initDataUrl : "";
            nextTick(() => {
                generateForm.value.refresh();
                if (basicData.value.itembox == "add" && basicData.value.formType == undefined) {
                    generateForm.value.setData({
                        guid: basicData.value.processSerialNumber,
                        processInstanceId: basicData.value.processSerialNumber
                    });
                    let Promise = generateForm.value.getData(false);
                    Promise.then(function (value) {
                        getFormInitData(initDataUrl.value, basicData.value.processSerialNumber).then(res => {//表单初始化数据：1.在这里初始化数据，2.在表单设计里通过表单数据源初始化数据
                            let initData = res.data;
                            for (let key of Object.keys(value)) {
                                if (value[key] != undefined && value[key].toString().indexOf("$_") > -1) {
                                    let key0 = value[key].toString().slice(2);
                                    value[key] = initData[key0] == undefined ? "" : initData[key0];
                                }
                            }
                            value.guid = basicData.value.processSerialNumber;
                            value.processInstanceId = basicData.value.processSerialNumber;
                            generateForm.value.setData(value);
                        });
                    }).catch(() => {
                    });
                    setTimeout(() => {//加载意见框,附件列表
                        if (formJson.value.config.permissionForm) {//权限表单，默认开始禁用所有字段
                            generateForm.value.disabledAll(true);
                        }
                        initFieldPerm();//字段填写权限
                        initOpinion();
                        initFileList();
                        initWord();
                        initPersonTree();
                        // initNumber();
                        // initChildTable();
                    }, 500)
                } else {
                    getFormData(fId, basicData.value.processSerialNumber).then(res1 => {//表单数据
                        let formData = res1.data;
                        if (basicData.value.itembox == "add") {//前置表单事项走此方法
                            let Promise = generateForm.value.getData(false);
                            Promise.then(function (value) {
                                getFormInitData(initDataUrl.value, basicData.value.processSerialNumber).then(res => {
                                    let initData = res.data;
                                    for (let key of Object.keys(value)) {
                                        if (value[key] != undefined && value[key].toString().indexOf("$_") > -1) {
                                            let key0 = value[key].toString().slice(2);
                                            value[key] = initData[key0] == undefined ? "" : initData[key0];
                                        }
                                        value[key] = formData[key] != "" ? formData[key] : value[key];//设置前置表单数据
                                    }
                                    value.guid = basicData.value.processSerialNumber;
                                    value.processInstanceId = basicData.value.processSerialNumber;
                                    generateForm.value.setData(value);//初始化数据
                                });
                            }).catch(() => {
                            });
                        } else {
                            let data = res1.data;
                            for (let key of Object.keys(data)) {//处理多选框
                                if (data[key] != undefined && data[key] != '' && data[key].startsWith('[') && data[key].endsWith(']')) {
                                    if (data[key] == '[]') {
                                        data[key] = [];
                                    } else {
                                        let str = data[key].split('[')[1].split(']')[0];
                                        data[key] = str.split(', ');
                                    }
                                }
                            }
                            generateForm.value.setData(data);
                        }
                        setTimeout(() => {//加载意见框,附件列表
                            generateForm.value.disabledAll(true);//默认禁用表单所有字段
                            if (!formJson.value.config.permissionForm) {//非权限表单
                                let positionId = y9_storage.getObjectItem('positionId');
                                if (basicData.value.itembox == "draft") {//草稿启用表单所有字段
                                    generateForm.value.disabledAll(false);
                                }
                                if (basicData.value.itembox == "todo" && basicData.value.startTaskDefKey.indexOf(basicData.value.taskDefKey) > -1
                                    && basicData.value.startor == positionId) {//起草节点可编辑
                                    generateForm.value.disabledAll(false);//启用表单所有字段
                                }
                            }
                            if (basicData.value.itembox == "add") {//前置表单事项走此方法
                                if (formJson.value.config.permissionForm) {//权限表单，默认开始禁用所有字段
                                    generateForm.value.disabledAll(true);
                                } else {
                                    generateForm.value.disabledAll(false);
                                }
                                initFieldPerm();//字段填写权限
                            }
                            if (basicData.value.itembox == "draft" || basicData.value.itembox == "todo") {
                                initFieldPerm();//字段填写权限
                            }
                            initOpinion();
                            // initNumber(res1.data);
                            initFileList();
                            initWord();
                            initPersonTree();
                            initChildTable();
                            initPicture(res1.data);
                        }, 500);
                    });
                }
            });
        }
    });
}

function initOpinion() {//加载意见框
    let data = {
        itemId: basicData.value.itemId,
        itembox: basicData.value.itembox,
        processDefinitionKey: basicData.value.processDefinitionKey,
        processSerialNumber: basicData.value.processSerialNumber,
        taskDefKey: basicData.value.taskDefKey,
        activitiUser: basicData.value.activitiUser,
        processInstanceId: basicData.value.processInstanceId,
        taskId: basicData.value.taskId,
    };
    getBindOpinionFrame(basicData.value.itemId, basicData.value.processDefinitionId).then(res => {
        if (res.success) {
            bindOpinionFrameList.value = res.data;
            for (let item of bindOpinionFrameList.value) {
                let customOpinions = generateForm.value.getComponent('custom_opinion@' + item.opinionFrameMark);
                if (customOpinions != null) {
                    customOpinions.initOpinion(data);
                }
            }
        }
    });
}

function initWord() {//加载正文
    let data = {
        itemId: basicData.value.itemId,
        itembox: basicData.value.itembox,
        processSerialNumber: basicData.value.processSerialNumber,
        processInstanceId: basicData.value.processInstanceId,
        taskId: basicData.value.taskId
    };
    let customWord = generateForm.value.getComponent('custom_word');
    if (customWord != null) {
        customWord.initWord(data);
    }
}

function initPicture(formData) {//加载图片显示组件
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

async function setNumber() {//设置编号
    let number = null;
    if (basicData.value.itembox == "add" || basicData.value.itembox == "draft") {
        let data = {
            processDefinitionId: basicData.value.processDefinitionId,
            processSerialNumber: basicData.value.processSerialNumber,
            itemId: basicData.value.itemId,
            taskDefKey: basicData.value.taskDefKey,
            itembox: basicData.value.itembox,
        };
        let formJsonStr = JSON.stringify(formJson.value).toString();
        if (formJsonStr.indexOf("custom_numberButton@") > -1) {
            let str = formJsonStr.split("custom_numberButton@")[1];
            let numberCustom = str.split('"')[0];
            let customNumberButton = generateForm.value.getComponent("custom_numberButton@" + numberCustom);
            if (customNumberButton != null) {
                await generateForm.value.getData(false).then(async formData => {
                    number = await customNumberButton.setNumber(data, formData);
                })
            }
        }
    }
    return number;
}

function initNumber(formData) {//加载编号按钮
    if (basicData.value.itembox == "add" || basicData.value.itembox == "draft" || basicData.value.itembox == "todo") {
        let data = {
            processDefinitionId: basicData.value.processDefinitionId,
            processSerialNumber: basicData.value.processSerialNumber,
            itemId: basicData.value.itemId,
            taskDefKey: basicData.value.taskDefKey,
            itembox: basicData.value.itembox,
        };
        let formJsonStr = JSON.stringify(formJson.value).toString();
        if (formJsonStr.indexOf("custom_numberButton@") > -1) {
            let str = formJsonStr.split("custom_numberButton@")[1];
            let numberCustom = str.split('"')[0];
            let customNumberButton = generateForm.value.getComponent("custom_numberButton@" + numberCustom);
            if (customNumberButton != null) {
                customNumberButton.initNumber(data, formData);
            }
        }
    }
}

function initFileList() {//加载附件框
    let data = {
        itembox: basicData.value.itembox,
        processSerialNumber: basicData.value.processSerialNumber,
        processInstanceId: basicData.value.processInstanceId,
        taskId: basicData.value.taskId,
    };
    let customFile = generateForm.value.getComponent('custom_file');
    if (customFile != null) {
        if (!fileRequired_change.value) {//附件必填验证没有修改过
            for (let item of formJson.value.list) {
                if ('custom-file' == item.el) {
                    if (item.rules[0]?.required) {//附件是否必填
                        fileRequired.value = true;
                        fileMessage.value = item.rules[0].message;
                    }
                }
            }
        }
        customFile.initFileList(data);
    }
}

function initPersonTree() {//加载人员选择树
    let data = {
        itembox: basicData.value.itembox,
        processSerialNumber: basicData.value.processSerialNumber,
        processInstanceId: basicData.value.processInstanceId,
        taskId: basicData.value.taskId
    };
    for (let i = 1; i < 6; i++) {
        let customPersonTree = generateForm.value.getComponent('custom_personTree' + i.toString());
        if (customPersonTree != null) {
            customPersonTree.initPersonTree(data);
        }
    }
}

function initChildTable(tableName) {//加载子表数据
    if (tableName == undefined) {
        let formJsonStr = JSON.stringify(formJson.value).toString();
        if (formJsonStr.indexOf("childTable@") > -1) {
            let str = formJsonStr.split("childTable@");
            for (let i = 1; i < str.length; i++) {//多个子表
                tableName = str[i].split('"')[0];
                let childTable = generateForm.value.getComponent("childTable@" + tableName);
                if (childTable != null) {
                    let childTableInfo = childTable.widget.childTableInfo;
                    let tableId = childTableInfo.split("@")[1];
                    if (basicData.value.itembox != "add" && basicData.value.itembox != "draft") {
                        generateForm.value.disabled(["childTable@" + tableName], true);//disabled子表单
                    }
                    //generateForm.value.disabled(['childTable@y9_form_baoxiao_zibiao'], true);//disabled子表单
                    getChildTableData(formId.value, tableId, basicData.value.processSerialNumber).then(res => {
                        if (res.success) {
                            let tableData = [];
                            for (let item of res.data) {
                                tableData.push(item);
                            }
                            childTable.tableData = tableData;
                        }
                    });
                }
            }
        }
    } else {//加载单个子表
        let childTable = generateForm.value.getComponent("childTable@" + tableName);
        if (childTable != null) {
            let childTableInfo = childTable.widget.childTableInfo;
            let tableId = childTableInfo.split("@")[1];
            if (basicData.value.itembox != "add" && basicData.value.itembox != "draft") {
                generateForm.value.disabled(["childTable@" + tableName], true);//disabled子表单
            }
            //generateForm.value.disabled(['childTable@y9_form_baoxiao_zibiao'], true);//disabled子表单
            getChildTableData(formId.value, tableId, basicData.value.processSerialNumber).then(res => {
                if (res.success) {
                    let tableData = [];
                    for (let item of res.data) {
                        tableData.push(item);
                    }
                    childTable.tableData = tableData;
                }
            });
        }
    }
}

function saveChildTable() {//保存子表数据
    let formJsonStr = JSON.stringify(formJson.value).toString();
    if (formJsonStr.indexOf("childTable@") > -1) {
        let str = formJsonStr.split("childTable@");
        for (let i = 1; i < str.length; i++) {//多个子表
            let tableName = str[i].split('"')[0];
            let childTable = generateForm.value.getComponent("childTable@" + tableName);
            if (childTable != null) {
                let childTableInfo = childTable.widget.childTableInfo;
                let tableId = childTableInfo.split("@")[1];
                let tableData = childTable.tableData;
                if (tableData.length > 0) {
                    for (let item of tableData) {
                        item.parentProcessSerialNumber = basicData.value.processSerialNumber;
                    }
                    let jsonData = JSON.stringify(tableData).toString();
                    saveChildTableData(formId.value, tableId, basicData.value.processSerialNumber, jsonData).then(res0 => {
                        if (!res0.success) {
                            ElMessage({type: 'error', message: t("保存子表数据失败"), appendTo: '.newForm-container'});
                        } else {
                            initChildTable(tableName);
                        }
                    });
                }
            }
        }
    }
}

function saveForm(type) {
    saveY9Form(type).then(value => {
        return saveY9ProcessParam();
    }).then(value => {
        return saveY9Draft();
    });
}

function saveY9Form(type) {
    msgType.value = type;
    return new Promise((resolve, reject) => {
        generateForm.value.getData(true).then(data => {
            if (basicData.value.itembox == "todo" || basicData.value.itembox == "add" || basicData.value.itembox == "draft") {
                if (fileRequired.value) {//附件必传验证
                    let customFile = generateForm.value.getComponent('custom_file');
                    let message = fileMessage.value == '' ? '请上传附件' : fileMessage.value;
                    if (customFile != null && customFile.fileTableConfig.tableData.length == 0) {
                        ElMessage({type: 'error', message: t(message), appendTo: '.newForm-container'});
                        return reject(new Error(t(message)).message);
                    }
                }
                let jsonData = JSON.stringify(data).toString();
                loading.value = true;
                saveFormData(formId.value, jsonData).then(res => {
                    loading.value = false;
                    if (res.success) {
                        //保存成功，修改标题数据
                        flowableStore.$patch({
                            documentTitle: data.title
                        })
                        if (msgType.value) {
                            ElMessage({type: 'success', message: t("保存表单成功"), appendTo: '.newForm-container'});
                        }

                        saveChildTable();//保存子表数据
                    } else {
                        ElMessage({type: 'error', message: t("保存表单失败"), appendTo: '.newForm-container'});
                        reject();
                    }
                    resolve();
                }).catch(() => {
                    loading.value = false;
                    reject(new Error(t("保存表单发生异常")).message);
                });
            } else {
                resolve();
            }
        }).catch(() => {
            ElMessage({type: 'error', message: t("表单验证不通过"), appendTo: '.newForm-container'});
            reject(new Error(t("表单验证不通过")).message);
        });
    });
}

function saveY9ProcessParam() {
    return new Promise((resolve, reject) => {
        if (basicData.value.itembox == "todo" || basicData.value.itembox == "add" || basicData.value.itembox == "draft") {
            let title = generateForm.value.getValue("title");
            let number = generateForm.value.getValue("number");
            let level = generateForm.value.getValue("level");
            let customItem = flowableStore.getCustomItem == undefined ? false : flowableStore.getCustomItem;
            saveProcessParam(basicData.value.itemId, basicData.value.processSerialNumber, basicData.value.processInstanceId,
                title, number, level, customItem).then(res0 => {//保存流程变量
                if (!res0.success) {
                    ElMessage({type: 'error', message: t("保存变量失败"), appendTo: '.newForm-container'});
                    reject();
                }
                resolve();
            }).catch(() => {
                ElMessage({type: 'error', message: t("保存变量发生异常"), appendTo: '.newForm-container'});
                reject(new Error(t("保存变量发生异常")).message);
            });
        } else {
            resolve();
        }
    });
}

function saveY9Draft() {
    return new Promise((resolve, reject) => {
        if (basicData.value.itembox == "add" || basicData.value.itembox == "draft") {
            let title = generateForm.value.getValue("title");
            let number = generateForm.value.getValue("number");
            let level = generateForm.value.getValue("level");
            saveDraft(basicData.value.processSerialNumber, basicData.value.itemId, basicData.value.processDefinitionKey, number, level, title).then(res1 => {
                if (!res1.success) {
                    ElMessage({type: 'error', message: t("保存草稿失败"), appendTo: '.newForm-container'});
                    reject();
                } else {
                    emits("refreshCount");
                }
                resolve();
            }).catch(() => {
                ElMessage({type: 'error', message: t("保存草稿发生异常"), appendTo: '.newForm-container'});
                reject(new Error(t("保存草稿发生异常")).message);
            });
        } else {
            resolve();
        }
    });
}

function initFieldPerm() {//字段权限控制
    getAllFieldPerm(basicData.value.formId, basicData.value.taskDefKey, basicData.value.processDefinitionId).then(res => {
        if (res.success) {
            let writeField = [];
            for (let item of res.data) {
                writeField.push(item.fieldName);
            }
            if (writeField.length > 0) {
                generateForm.value.clearValidate(writeField);//移除其他字段必填项验证
                generateForm.value.disabled(writeField, false);//开启字段填写
            } else {
                if (formJson.value.config.permissionForm) {//权限表单，当前任务没有配置写权限，则移除所有字段必填验证
                    generateForm.value.clearAllValidate();//移除所有字段必填项验证
                }
            }
        } else {
            ElMessage({type: 'error', message: res.msg, appendTo: '.newForm-container'});
        }
    });
}

function signOpinion() {//验证必填意见
    let msg = "";
    if (basicData.value.itembox == "add" || basicData.value.itembox == "draft" || basicData.value.itembox == "todo") {
        for (let item of bindOpinionFrameList.value) {
            let customOpinion = generateForm.value.getComponent('custom_opinion@' + item);
            if (customOpinion != null) {
                let addableObj = customOpinion.addable;
                let opinionName = customOpinion.opinionName;
                if (addableObj.addable && addableObj.signOpinion) {//addable添加意见标识，signOpinion必填意见标识
                    if (msg == "") {
                        msg = "请填写【" + opinionName + "】";
                    } else {
                        msg += "<br><br>请填写【" + opinionName + "】";
                    }
                }
            }
        }
    }
    return msg;
}

function saveChangeOpinion() {//保存未保存的意见内容
    //opinionRef.value.saveChange();
}

</script>

<style lang="scss" scoped>
.formaside {
    /* background-color: #e7e8ec; */
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
    border-bottom: 2px solid #E6794A;
    color: #E6794A;
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
.newForm-container {
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
}


:deep(.el-form-item__content) {
    font-size: v-bind('fontSizeObj.smallFontSize');
}

:deep(.el-input__wrapper) {
    font-size: v-bind('fontSizeObj.smallFontSize');
}

:deep(.el-form-item__error) {
    font-size: v-bind('fontSizeObj.smallFontSize');
}

:deep(.el-form-item__error) {
    font-size: v-bind('fontSizeObj.smallFontSize');
}

/* 设置一些级别样式，可根据具体需求修改 */
.fm-form {

    :deep(.form) {
        padding-left: 10px;
        padding-right: 10px;
    }

    :deep(.fm-form-item) {
        margin-top: 8px;
    }

    :deep( .el-input) {
        border: #e4e7ed solid 1px;
    }

    :deep(.el-textarea) {
        border: #e4e7ed solid 1px;
    }

    :deep(.el-form-item__label) {
        line-height: 40px;
        height: 40px;
    }

    :deep(.el-form-item__content) {
        margin: 0px 2px;
    }

    :deep( .suggest ) {
        border: #e4e7ed solid 1px;
    }


}

</style>