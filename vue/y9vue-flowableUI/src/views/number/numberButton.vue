<template>
    <div class="number-button"></div>
</template>

<script lang="ts" setup>
    //shoutongzhicheng-gongxiaoshe使用的编号
    import { inject } from 'vue';
    import { checkNumber, findByCustom, getNumber } from '@/api/flowableUI/organWord';
    import { useI18n } from 'vue-i18n';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const props = defineProps({
        numberCustom: String, //绑定编号标识
        tableField: {
            //关联表单字段
            type: String,
            default: () => {
                return '';
            }
        },
        numberName: String //绑定编号名称
    });

    const emits = defineEmits(['update_number']);
    const ruleForm = ref<FormInstance>();
    const data = reactive({
        numberInfo: {},
        numberData: {},
        organWord: '', //机关代字
        organWordList: [],
        currentNumber: '', //当前编号
        characterValue: '',
        number: '',
        year: ''
    });

    let { numberData, organWordList, characterValue, currentNumber, number, year } = toRefs(data);

    defineExpose({
        initNumber,
        setNumber
    });

    function initNumber(data, formData) {
        console.log('加载编号按钮：' + props.numberName + '(' + props.numberCustom + ')');
        numberData.value = data;
        // updateNumber(formData);
    }

    async function setNumber(data, formData) {
        console.log('加载编号按钮：' + props.numberName + '(' + props.numberCustom + ')');
        numberData.value = data;
        await updateNumber(formData);
        return currentNumber.value;
    }

    async function updateNumber(formData) {
        // if(numberData.value.itembox == 'todo'){
        //获取编号按钮权限
        await findByCustom(
            numberData.value.itemId,
            numberData.value.processDefinitionId,
            numberData.value.taskDefKey,
            props.numberCustom
        ).then(async (res) => {
            if (res.success) {
                if (res.data.length > 0) {
                    if (res.data[0].hasPermission) {
                        organWordList.value = res.data;
                        if (organWordList.value.length > 0) {
                            let nowDate = new Date();
                            year.value = nowDate.getFullYear();
                            characterValue.value = organWordList.value[0].name;
                            if (formData != undefined) {
                                if (formData[props.tableField] == '' || formData[props.tableField] == undefined) {
                                    await getY9Number();
                                } else {
                                    currentNumber.value = formData[props.tableField];
                                }
                            } else {
                                await getY9Number();
                            }
                        }
                    }
                }
            }
        });
        // }
    }

    async function getY9Number(type) {
        await getNumber(numberData.value.itemId, props.numberCustom, characterValue.value, year.value).then(
            async (res) => {
                if (res.success) {
                    let nowDate = new Date();
                    let month = nowDate.getMonth() + 1;
                    let date = nowDate.getDate();
                    let monthStr = '';
                    let dateStr = '';
                    if (month < 10) {
                        monthStr = '0' + month;
                    } else {
                        monthStr = month.toString();
                    }
                    if (date < 10) {
                        dateStr = '0' + date;
                    } else {
                        dateStr = date.toString();
                    }
                    number.value = res.data.numberTemp.toString();
                    let newnumber = '';
                    if (number.value.length == 1) {
                        newnumber = '000' + number.value;
                    } else if (number.value.length == 2) {
                        newnumber = '00' + number.value;
                    } else if (number.value.length == 3) {
                        newnumber = '0' + number.value;
                    }
                    currentNumber.value = '〔' + year.value + monthStr + dateStr + '〕' + newnumber + '号';
                    let data = {};
                    data.tableField = props.tableField;
                    data.value = currentNumber.value;
                    console.log(11, data);

                    emits('update_number', data); //向父组件GenerateElementItem传值
                    if (type == '1') {
                        ElMessage({
                            type: 'success',
                            message: t('编号已变更'),
                            offset: 65,
                            appendTo: '.number-button'
                        });
                    }
                    await saveNumber();
                }
            }
        );
    }

    async function saveNumber() {
        //保存编号
        let msg = false;
        let res = await checkNumber(
            numberData.value.itemId,
            props.numberCustom,
            characterValue.value,
            year.value,
            number.value,
            numberData.value.processSerialNumber
        );
        if (res.success) {
            if (res.data.status == 0) {
                ElMessage({ type: 'error', message: t('当前编号已被使用'), offset: 65, appendTo: '.number-button' });
                await getY9Number('1');
            } else if (res.data.status == 1) {
                msg = true;
            } else if (res.data.status == 2) {
                ElMessage({ type: 'error', message: t('当前编号不存在'), offset: 65, appendTo: '.number-button' });
            } else if (res.data.status == 3) {
                ElMessage({ type: 'error', message: t('编号异常'), offset: 65, appendTo: '.number-button' });
            }
        }
        return msg;
    }
</script>

<style lang="scss" scoped>
    .number-button {
        /*message */
        :global(.el-message .el-message__content) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }
</style>

<style lang="scss">
    .numberForm .el-form-item__content {
        margin: auto;
        line-height: 40px !important;
    }

    .numberForm .el-form-item {
        margin-bottom: 0 !important;
    }

    .numberForm .el-input__inner {
        height: 31px !important;
    }
</style>
