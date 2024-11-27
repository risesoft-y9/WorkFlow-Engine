<!--
 * @Author: your name
 * @Date: 2021-04-13 10:34:31
 * @LastEditTime: 2024-02-05 16:59:24
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\views\print\printIndex.vue
-->
<template>
    <el-container style="height: 100%; width: 100%; display: block">
        <div style="width: 100%; text-align: right; margin: 15px 0px">
            <!-- 水印颜色：<el-color-picker size="large" v-model="color" @change="colorchange"/> -->
            <el-button
                ref="buttonRef"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                style="margin-left: 10px"
                type="primary"
                @click="addShuiyin"
            >
                <i class="ri-home-6-line"></i><span style="margin-left: 5px">{{ $t(shuiyinStr) }}</span>
            </el-button>
            <el-button
                ref="buttonRef"
                v-print="'#printTest'"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                style="margin-right: 2%"
                type="primary"
            >
                <i class="ri-printer-line"></i><span style="margin-left: 5px">{{ $t('打印') }}</span>
            </el-button>
        </div>
        <div
            id="printTest"
            ref="printTestRef"
            class="printTest"
            style="background-color: #fff; position: absolute; left: 50%; transform: translate(-50%, -50%)"
        >
            <fm-generate-form ref="generateFormRef" :data="formJson" :edit="edit" style="margin: auto">
            </fm-generate-form>
        </div>
    </el-container>
</template>

<script lang="ts" setup>
    import { inject, nextTick, onMounted, reactive, ref } from 'vue';
    import { getChildTableData, getFormData, getFormJson } from '@/api/flowableUI/form';
    import { getBindOpinionFrame } from '@/api/flowableUI/opinion';
    import { useRoute } from 'vue-router';
    import y9_storage from '@/utils/storage';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    const currentrRute = useRoute();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const data = reactive({
        formJson: { list: [], config: {} },
        formId: '',
        edit: false, //表单是否可编辑
        processSerialNumber: '',
        processDefinitionId: '',
        taskDefKey: '',
        itemId: '',
        processInstanceId: '',
        activitiUser: '',
        show: false,
        buttonRef: '',
        generateFormRef: '',
        height: '',
        printTestRef: '',
        shuiyinStr: '隐藏水印',
        color: '#333333',
        can: '',
        cans: ''
    });

    let {
        formId,
        formJson,
        edit,
        processSerialNumber,
        processDefinitionId,
        taskDefKey,
        itemId,
        processInstanceId,
        activitiUser,
        show,
        buttonRef,
        generateFormRef,
        height,
        printTestRef,
        shuiyinStr,
        color,
        can,
        cans
    } = toRefs(data);

    onMounted(() => {
        formId.value = currentrRute.query.formId;
        processSerialNumber.value = currentrRute.query.processSerialNumber;
        processDefinitionId.value = currentrRute.query.processDefinitionId;
        taskDefKey.value = currentrRute.query.taskDefKey;
        itemId.value = currentrRute.query.itemId;
        activitiUser.value = currentrRute.query.activitiUser;
        processInstanceId.value = currentrRute.query.processInstanceId;
        document.title = t('打印表单');
        height.value = window.innerHeight - 70;
        printTestRef.value.attributes.style.value = 'width: 100%;background-color: #fff;height:' + height.value + 'px';
        showForm();

        interface watermarkData {
            text?;
            deptName?;
            name?;
        }

        // 定义⽔印⽂字变量
        const userInfo = y9_storage.getObjectItem('ssoUserInfo');
        let dept = userInfo.dn?.split(',')[1]?.split('=')[1];
        let watermarkValue = ref<watermarkData>({
            name: userInfo.name,
            text: computed(() => t('保守秘密，慎之又慎')),
            deptName: dept
        });

        //创建一个画布
        can.value = document.createElement('canvas');
        //设置画布的长宽
        can.value.width = 375;
        can.value.height = 280;

        cans.value = can.value.getContext('2d');
        //旋转角度
        cans.value.rotate((-15 * Math.PI) / 180);
        cans.value.font = `14px STHeiti`;
        //Microsoft YaHei
        //设置填充绘画的颜色、渐变或者模式
        cans.value.fillStyle = '#aaa';
        //设置文本内容的当前对齐方式
        cans.value.textAlign = 'left';
        //设置在绘制文本时使用的当前文本基线
        cans.value.textBaseline = 'Middle';
        //在画布上绘制填色的文本（输出的文本，开始绘制文本的X坐标位置，开始绘制文本的Y坐标位置）
        if (typeof watermarkValue.value == 'string') {
            cans.value.fillText(
                watermarkValue.value ? watermarkValue.value : '',
                can.value.width / 4,
                can.value.height / 2
            );
        } else if (typeof watermarkValue.value == 'object') {
            let strValue = `${watermarkValue.value?.name ? watermarkValue.value?.name : ''}${
                watermarkValue.value?.deptName ? '-' + watermarkValue.value?.deptName : ''
            }${watermarkValue.value?.userIP ? '-' + watermarkValue.value?.userIP : ''}`;
            cans.value.fillText(strValue, can.value.width / 4, can.value.height / 2);
            cans.value.fillText(watermarkValue.value?.text ? watermarkValue.value?.text : '', can.value.width / 4, 160);
        }

        //隐藏最外层水印
        const div1 = document.getElementById('1.23452384164.123412416');
        div1.style.display = 'none';

        const div = document.getElementById('printTest');
        const fromdiv = div.childNodes[0];
        fromdiv.style.background = 'url(' + can.value.toDataURL('image/png') + ') left top repeat';
    });

    function colorchange(val) {
        // 定义⽔印⽂字变量
        const userInfo = y9_storage.getObjectItem('ssoUserInfo');
        let dept = userInfo.dn?.split(',')[1]?.split('=')[1];
        let watermarkValue = ref<watermarkData>({
            name: userInfo.name,
            text: computed(() => t('保守秘密，慎之又慎')),
            deptName: dept
        });
        can.value = document.createElement('canvas');
        //设置画布的长宽
        can.value.width = 375;
        can.value.height = 280;

        cans.value = can.value.getContext('2d');
        //旋转角度
        cans.value.rotate((-15 * Math.PI) / 180);
        cans.value.font = `14px STHeiti`;
        //Microsoft YaHei
        //设置填充绘画的颜色、渐变或者模式
        cans.value.fillStyle = val;
        //设置文本内容的当前对齐方式
        cans.value.textAlign = 'left';
        //设置在绘制文本时使用的当前文本基线
        cans.value.textBaseline = 'Middle';
        //在画布上绘制填色的文本（输出的文本，开始绘制文本的X坐标位置，开始绘制文本的Y坐标位置）
        if (typeof watermarkValue.value == 'string') {
            cans.value.fillText(
                watermarkValue.value ? watermarkValue.value : '',
                can.value.width / 4,
                can.value.height / 2
            );
        } else if (typeof watermarkValue.value == 'object') {
            let strValue = `${watermarkValue.value?.name ? watermarkValue.value?.name : ''}${
                watermarkValue.value?.deptName ? '-' + watermarkValue.value?.deptName : ''
            }${watermarkValue.value?.userIP ? '-' + watermarkValue.value?.userIP : ''}`;
            cans.value.fillText(strValue, can.value.width / 4, can.value.height / 2);
            cans.value.fillText(watermarkValue.value?.text ? watermarkValue.value?.text : '', can.value.width / 4, 160);
        }
        const div = document.getElementById('printTest');
        const fromdiv = div.childNodes[0];
        fromdiv.style.background = 'url(' + can.value.toDataURL('image/png') + ') left top repeat';
    }

    function addShuiyin() {
        if (shuiyinStr.value == '隐藏水印') {
            const div = document.getElementById('printTest');
            const fromdiv = div.childNodes[0];
            fromdiv.style.background = '';
            shuiyinStr.value = '显示水印';
        } else {
            const div = document.getElementById('printTest');
            const fromdiv = div.childNodes[0];
            fromdiv.style.background = 'url(' + can.value.toDataURL('image/png') + ') left top repeat';
            shuiyinStr.value = '隐藏水印';
        }
    }

    async function showForm() {
        let res = await getFormJson(formId.value);
        if (res.success) {
            show.value = true;
            formJson.value = JSON.parse(res.data);
            nextTick(async () => {
                generateFormRef.value.refresh();
                let res1 = await getFormData(formId.value, processSerialNumber.value);
                let data = res1.data;
                for (let key of Object.keys(data)) {
                    //处理多选框
                    if (
                        data[key] != undefined &&
                        data[key] != '' &&
                        data[key].startsWith('[') &&
                        data[key].endsWith(']')
                    ) {
                        if (data[key] == '[]') {
                            data[key] = [];
                        } else {
                            let str = data[key].split('[')[1].split(']')[0];
                            data[key] = str.split(', ');
                        }
                    }
                }
                generateFormRef.value.setData(data);
                setTimeout(() => {
                    //加载意见框
                    initOpinion();
                    initFileList();
                    initChildTable();
                }, 500);
            });
        }
    }

    async function initOpinion() {
        let data = {
            itemId: itemId.value,
            itembox: 'done',
            processDefinitionKey: '',
            processSerialNumber: processSerialNumber.value,
            taskDefKey: taskDefKey.value,
            activitiUser: activitiUser.value,
            processInstanceId: processInstanceId.value,
            taskId: ''
        };
        let res = await getBindOpinionFrame(itemId.value, processDefinitionId.value);
        if (res.success) {
            for (let item of res.data) {
                let customOpinion = generateFormRef.value.getComponent('custom_opinion@' + item);
                if (customOpinion != null) {
                    customOpinion.initOpinion(data);
                }
            }
        }
    }

    function initFileList() {
        //加载附件框
        let data = {
            itembox: 'done',
            processSerialNumber: processSerialNumber.value,
            processInstanceId: processInstanceId.value,
            taskId: ''
        };
        let customFile = generateFormRef.value.getComponent('custom_file');
        if (customFile != null) {
            customFile.initFileList(data);
        }
    }

    async function initChildTable() {
        //加载子表数据
        let childTable = generateFormRef.value.getComponent('childTable');
        if (childTable != null) {
            let tableId = childTable.tableId;
            let res = await getChildTableData(formId.value, tableId, processSerialNumber.value);
            if (res.success) {
                childTable.tableData.length = 0;
                for (let item of res.data) {
                    childTable.tableData.push(item);
                }
            }
        }
    }
</script>

<style lang="scss">
    .printTest .suggest ul li a {
        color: #000 !important;
    }

    .printTest {
        width: 750px !important;
        height: 98% !important;
        margin: auto !important;
    }

    .printTest .el-form {
        background: none !important;

        div {
            background: none !important;
        }

        input {
            background: none !important;
        }

        .el-main {
            background: none !important;
        }

        textarea {
            background: none !important;
        }

        select {
            background: none !important;
        }

        span {
            background: none !important;
            color: #000 !important;
        }
    }

    .printTest .table {
        border-collapse: collapse !important;
        border-collapse: unset !important;
        border-spacing: 0 !important;
        border: 1px solid #000 !important;
    }

    .printTest .table td {
        border: 1px solid #000 !important;
    }

    .printTest .table tr {
        border: 1px solid #000 !important;
    }

    @media print {
        .printTest {
            -webkit-print-color-adjust: exact;
            -moz-print-color-adjust: exact;
            -ms-print-color-adjust: exact;
            print-color-adjust: exact;
            width: 750px;
            height: 98% !important;
            margin: 0;
            font-family: initial !important;
        }
        .printTest > div:first-child {
            -webkit-print-color-adjust: exact;
            -moz-print-color-adjust: exact;
            -ms-print-color-adjust: exact;
            print-color-adjust: exact;
        }
        .printTest .form {
            font-family: initial !important;
        }
        .printTest .table {
            border-collapse: unset !important;
            border-collapse: collapse !important;
            border-spacing: 0 !important;
            border: 1.5px solid #000 !important;
        }

        .printTest .table td {
            border: 1.5px solid #000 !important;
        }

        .printTest .table tr {
            border: 1.5px solid #000 !important;
        }
        @page {
            size: A4;
            margin: 0;
            padding: 0;
            height: 98% !important;
            // resolution: 300dpi;
        }
        html,
        body {
            margin: 0;
            padding: 0;
            height: 98% !important;
        }
    }
</style>
