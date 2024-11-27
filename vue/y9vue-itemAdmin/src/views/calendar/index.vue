<template>
    <div style="height: 100%; width: 100%">
        <y9Card :showFooter="false" :showHeader="false" class="calendar-context">
            <FullCalendar
                ref="fullCalendar"
                :options="calendarOptions"
                class="demo-app-calendar"
                style="width: 80%; margin: auto; height: 90%"
            />
        </y9Card>
        <el-dialog
            v-model="dialogVisible"
            :close-on-click-modal="false"
            :close-on-press-escape="false"
            class="tishi"
            title="日历设置"
            width="20%"
        >
            <span>设置日期{{ selectDate }}为?</span>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary" @click="setCalendar(1)">休假</el-button>
                    <el-button v-if="bubanShow" class="global-btn-second" @click="setCalendar(2)">补班</el-button>
                    <el-button @click="removeCalendar">删除</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
    import { onMounted, ref } from 'vue';
    import '@fullcalendar/core/vdom'; // solves problem with Vite
    import FullCalendar from '@fullcalendar/vue3';
    import dayGridPlugin from '@fullcalendar/daygrid';
    import interactionPlugin from '@fullcalendar/interaction';
    import { delCalendar, getCalendar, saveCalendar } from '@/api/itemAdmin/calendar';
    import { calendar } from '@/utils/calendar.js';
    import type { ElMessage } from 'element-plus';

    const dialogVisible = ref(false);
    const selectDate = ref('');
    const bubanShow = ref(false);
    const calendarOptions = {
        plugins: [dayGridPlugin, interactionPlugin],
        initialView: 'dayGridMonth',
        dateClick: function (arg) {
            dialogVisible.value = true;
            let buban1 = arg.dayEl.attributes.class.textContent.indexOf('fc-day-sat') > -1;
            let buban2 = arg.dayEl.attributes.class.textContent.indexOf('fc-day-sun') > -1;
            if (buban1 || buban2) {
                bubanShow.value = true;
            } else {
                bubanShow.value = false;
            }
            selectDate.value = arg.dateStr;
        },
        events: function (info, wrappedSuccess) {
            let startdate = info.start.getFullYear() + '-' + (info.start.getMonth() + 1).toString();
            let enddate = info.end.getFullYear() + '-' + (info.end.getMonth() + 1).toString();
            if (startdate.split('-')[0] != enddate.split('-')[0]) {
                //跨年
                let events = [];
                getCalendar(startdate).then((res) => {
                    let listData = res.data;
                    for (let i = 0; i < listData.length; i++) {
                        let type = listData[i].type;
                        let title = '休';
                        let className = 'xiu';
                        if (type == 2) {
                            title = '班';
                            className = 'ban';
                        }
                        events.push({ id: '', title: title, start: listData[i].date, className: className });
                    }
                });
                getCalendar(enddate).then((res) => {
                    let listData = res.data;
                    for (let i = 0; i < listData.length; i++) {
                        let type = listData[i].type;
                        let title = '休';
                        let className = 'xiu';
                        if (type == 2) {
                            title = '班';
                            className = 'ban';
                        }
                        events.push({ id: '', title: title, start: listData[i].date, className: className });
                    }
                    wrappedSuccess(events);
                });
            } else {
                let events = [];
                getCalendar(startdate).then((res) => {
                    let listData = res.data;
                    for (let i = 0; i < listData.length; i++) {
                        let type = listData[i].type;
                        let title = '休';
                        let className = 'xiu';
                        if (type == 2) {
                            title = '班';
                            className = 'ban';
                        }
                        events.push({ id: '', title: title, start: listData[i].date, className: className });
                    }
                    wrappedSuccess(events);
                });
            }
        },
        firstDay: '1',
        headerToolbar: {
            left: 'title',
            center: '',
            right: 'prevYear,prev today next,nextYear'
        },
        aspectRatio: 1.85,
        buttonText: { today: '今天', month: '月', day: '日', prev: '上一月', next: '下一月', listWeek: '周' },
        locale: 'zh-cn',
        views: {
            //对应月视图
            dayGridMonth: {
                displayEventTime: false, //是否显示时间
                dayCellContent(item) {
                    let _dateF = calendar.solar2lunar(
                        item.date.getFullYear(),
                        item.date.getMonth() + 1,
                        item.date.getDate()
                    );
                    let cDay = _dateF.cDay;
                    let IDayCn = _dateF.IMonthCn + _dateF.IDayCn;
                    let myclass = '';
                    if (_dateF.Term != null) {
                        IDayCn = _dateF.Term;
                        myclass = 'IDayCn';
                    }
                    if (_dateF.festival.length > 0) {
                        IDayCn = _dateF.festival[0];
                        myclass = 'IDayCn';
                    }
                    let html = {
                        html:
                            `<div class="cDay" style="display: table;width: 100%;text-align: right;margin-right: 10px;">${cDay}</div>` +
                            `<div class="${myclass}" style="display: table;width: 100%;margin-left: 10px;">${IDayCn}</div>`
                    };
                    return html;
                }
            }
        }
    };
    const fullCalendar = ref();
    onMounted(() => {
        //calendarShow.value = true;
    });

    const setCalendar = (type) => {
        saveCalendar(selectDate.value, type).then((res) => {
            let calendarApi = fullCalendar.value.getApi();
            calendarApi.refetchEvents();
            if (res.success) {
                dialogVisible.value = false;
                ElMessage({ type: 'success', message: res.msg, offset: 65 });
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65 });
            }
        });
    };

    const removeCalendar = () => {
        delCalendar(selectDate.value).then((res) => {
            let calendarApi = fullCalendar.value.getApi();
            calendarApi.refetchEvents();
            if (res.success) {
                dialogVisible.value = false;
                ElMessage({ type: 'success', message: res.msg, offset: 65 });
            } else {
                ElMessage({ type: 'error', message: res.msg, offset: 65 });
            }
        });
    };
</script>

<style lang="scss">
    @import '@/theme/fullcalendar-vars.scss';

    .fc table {
        font-size: 1.1em;
    }

    .fc-toolbar .fc-center {
        float: right;
        margin-right: 47%;
    }

    .fc-toolbar h2 {
        font-weight: bold;
    }

    .fc .fc-button {
        font-size: 1.1em;
    }

    .fc .fc-col-header-cell-cushion {
        font-weight: bold;
        font-size: 20px;
    }

    .fc-scroller .fc-scroller-liquid-absolute {
        overflow: hidden;
    }

    .fc .fc-day-sat .fc-daygrid-day-top .cDay {
        text-align: right;
        font-family: '微软雅黑';
        padding: 12px 12px 0 12px;
        color: #f76161;
    }

    .fc-day-sat {
        color: #f76161;

        .IDayCn {
            color: #f76161 !important;
        }
    }

    .fc .fc-day-sun .fc-daygrid-day-top .cDay {
        text-align: right;
        font-family: '微软雅黑';
        padding: 12px 12px 0 12px;
        color: #f76161;
    }

    .fc-day-sun {
        color: #f76161;

        .IDayCn {
            color: #f76161 !important;
        }
    }

    .fc-day-today {
        background-color: #d1dafd !important;
    }

    .fc .fc-daygrid-day-top {
        margin-top: 5px;
    }

    .fc .fc-daygrid-day-top .cDay {
        text-align: right;
        font-size: 20px;
        font-family: '微软雅黑';
        color: #666;
        font-weight: bold;
        padding: 12px 12px 0 12px;
    }

    .fc .IDayCn {
        font-family: '微软雅黑';
        font-weight: bold;
        padding: 12px 12px 0 12px;
        color: var(--el-color-primary-light-3);
    }

    .fc-theme-standard .fc-scrollgrid {
        border-left: 1px solid #999;
        border-top: 1px solid #999;
    }

    .fc td,
    .fc th {
        border-style: solid;
        border-width: 1px;
        padding: 0;
        vertical-align: top;
        border-color: #999;
    }

    .fc-direction-ltr .fc-daygrid-event.fc-event-end,
    .fc-direction-rtl .fc-daygrid-event.fc-event-start {
        margin-right: 10px;
    }

    .xiu {
        color: #fff;
        background-color: #f76161;
        border-color: #f76161;

        text-align: center;
        width: 20px;
        float: right;
    }

    .ban {
        color: #fff;
        background-color: #4e5877;
        border-color: #4e5877;
        text-align: center;
        width: 20px;
        float: right;
    }

    .fc-daygrid-day:hover {
        background-color: rgba(78, 110, 242, 0.1);
    }

    .dialogDiv {
        height: 500px;
        overflow: auto;
    }

    .shareInfo {
        /* 水平、垂直居中 */
        display: flex;
        justify-content: center;
        align-items: center;
        height: auto;

        .share-content {
            overflow: auto;
            height: 68px;
            width: 11.5vw;
        }

        .share-info {
            margin-top: 10px;
            width: 100%;

            .share-name {
                margin-right: 18px;
                display: block;
                float: left;
            }

            .share-time {
                float: right;
                margin-top: 2px;
            }
        }
    }

    .leader-card {
        background-color: #ececec;
        text-align: center;
        border-radius: 5px;
        padding: 16px;
        text-align: center;

        .leader-body {
            overflow: hidden;
        }
    }

    .divider {
        margin-top: 18px;
        margin-bottom: 18px;
    }

    .el-input__wrapper {
        width: 96%;
    }

    .frequency > {
        :deep(.el-form-item--default .el-form-item__content) {
            width: 100% !important;
        }
    }

    .fc .fc-toolbar-title {
        color: var(--el-color-primary-light-3);
    }

    :root {
        --fc-button-text-color: var(--el-color-primary-light-3);
        --fc-button-bg-color: rgba(0, 0, 0, 0);
        --fc-button-border-color: var(--el-color-primary-light-3);
        --fc-button-hover-border-color: var(--el-color-primary-light-3);
        --fc-button-hover-bg-color: #eef0f7;
        --fc-button-active-bg-color: var(--el-color-primary-light-7);
        --fc-today-bg-color: var(--el-color-primary-light-7);
    }

    .fc .fc-button-primary:not(:disabled).fc-button-active {
        color: #fff;
        background-color: var(--el-color-primary-light-5);
        border-color: var(--el-color-primary-light-3);
    }

    .fc-button-group {
        border-color: var(--el-color-primary-light-3);
    }

    //日历--- 周/日中的颜色
    .fc-theme-standard .fc-list-day-cushion {
        background-color: var(--el-color-primary-light-5);
        color: #f8ffff;
    }

    .fc-list-table {
        line-height: 3.5em;
    }

    .fc .fc-list-event-dot {
        //border: 5px solid var(--el-color-primary);
        border: 5px solid var(--el-color-primary-light-3);
    }

    .fc .fc-list-table td,
    .fc .fc-list-day-cushion {
        padding: 0 14px;
    }

    //隐藏日历中标题太长的部分
    .fc-daygrid-day {
        overflow: hidden;
    }

    //去掉点击按钮时的描边状态
    .fc-button {
        box-shadow: none !important;
    }

    .share-card {
        background-color: #dfe3f1 !important;
        border-radius: 10px !important;
        margin: 8px 16px;

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            //border-bottom: 1px solid #eee;
        }

        .y9-card-header {
            //border-bottom: 0 !important;
            padding-bottom: 8px !important;
        }
    }
</style>
