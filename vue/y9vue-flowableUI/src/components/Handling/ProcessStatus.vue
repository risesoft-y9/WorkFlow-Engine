
<!-- 流转状态 -->
<template>
    <div class="process-style">
       <div class="line-status">
            <!-- 首个点 -->
            <div class="top-dot"></div>
            <!-- 单个item -->
            <template v-for="(item, index) in tabsList" :key="index">
                <div class="node" :style="{height:index==0 ? '50px':''}">
                <!-- 竖线 -->
                <div class="line"></div>
                <!-- 线，点，时间等一些信息 -->
                <div class="item-style" 
                :class="{ 'item-left' : item.placement === 'left', 'item-right' : item.placement === 'right' }">
                    <!-- 点的类型 -->
                    <div class="dot"
                    v-if="item.placement === 'right'" 
                    :class="{ 'solid' : item.type === 'solid', 'current': item.type === 'current' }">
                        <span v-if="item.type == 'current'"></span>
                    </div>
                    <!-- 横线  -->
                    <div class="cross"></div>
                    <!-- 点的类型 -->
                    <div class="dot" 
                    v-if="item.placement === 'left'"
                    :class="{ 'solid' : item.type === 'solid', 'current': item.type === 'current' }">
                        <span v-if="item.type == 'current'"></span>
                    </div>
                    <!-- 状态 -->
                    <div class="status" 
                        :class="{ 'status-right': item.placement == 'right', 'status-left': item.placement == 'left',
                        'status-current-left':  item.placement == 'left' && item.type == 'current'}">
                        {{ item.content}}
                    </div>
                    <!-- 人员 -->
                    <div class="name" 
                    :class="{ 'name-left': item.placement == 'left', 'name-right': item.placement == 'right' }">
                        <div v-if="item.dataType == 'array'" style="display: flex;flex-direction: column;">
                            <span style="text-align: center;margin-bottom: 5px;">{{item.name[0]}}</span>
                            <span style="text-align: center;">{{item.name[1]}}</span>
                        </div>
                        <div v-else style="display: flex;flex-direction: column;">
                            {{item.name}}
                        </div>
                    </div>
                    <!-- 时间 -->
                    <div class="time">
                        <div v-if="item.timeType == 'array'" class="time-group"
                            :class="{ 'time-group-right': item.placement == 'right' }">
                            <span>{{ item.timestamp[0] }}</span>
                            <span>丨</span>
                            <span>{{ item.timestamp[1] }}</span>
                        </div>
                        <div v-else class="time-single" 
                        :class="{ 'time-single-right': item.placement == 'right', 'time-single-left': item.placement == 'left'}">
                            {{ item.timestamp }}
                        </div>
                    </div>
                </div>
            </div>
            </template>
            <!-- 最后的线 -->
            <div class="last-line"></div>
       </div>
    </div>
</template>
<script lang="ts" setup>
import { reactive, toRefs, watch, inject } from 'vue';
import { $dataType } from '@/utils/object'
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');
let props = defineProps({
    list: {
        type: Array,
        default: () => []
    }
})

const data = reactive({
    // 页签 数组'办件单', '沟通交流', '历程', '附件'
    tabsList: [
        {
            timestamp: ['2018-04-03 20:46', '2022-09-11 14:22'], // 时间戳 array | string 多个用数组 单个用string
            placement: 'left', // 时间戳位置
            type: 'solid', // 节点类型， solid 实心，hollow 空心，current 当前状态
            name: '人员1', // 人员
            content: '吃饭', // 过程
        },
        {
            timestamp: ['2018-04-03 20:46', '2022-09-11 14:22'], // 时间戳 array | string 多个用数组 单个用string
            placement: 'right', // 时间戳位置
            type: 'solid', // 节点类型， solid 实心，hollow 空心，current 当前状态
            name: '人员2', // 人员
            content: '订单', // 过程
        },
        {
            timestamp: '2018-04-03 20:46', // 时间戳 array | string 多个用数组 单个用string
            placement: 'left', // 时间戳位置
            type: 'current', // 节点类型， solid 实心，hollow 空心，current 当前状态
            name: '人员3', // 人员
            content: '官方无人', // 过程
        },
        {
            placement: 'right', // 时间戳位置
            type: 'current', // 节点类型， solid 实心，hollow 空心，current 当前状态
            name: '人员人员人员4', // 人员
            timestamp: '2018-04-03 20:46',
            content: '违法金额', // 过程
        },
        {
            placement: 'left', // 时间戳位置
            type: 'hollow', // 节点类型， solid 实心，hollow 空心，current 当前状态
            name: '人员5', // 人员
            timestamp: '2018-04-03 20:46',
            content: '并行处理', // 过程
        },
    ],
    // 当前活跃值
    activeIndex: 0,
   
})
let {
    tabsList,
    activeIndex,
} = toRefs(data);

watch( () => props.list, (newVal: any) => {
   if(newVal.length){
        tabsList.value = newVal;
        console.log(newVal);
        
   }
   tabsList.value.map(item => {
        if($dataType(item.timestamp) == 'array'){
            item.timeType = 'array';
        }else {
            item.timeType = 'string';
        }

        if($dataType(item.name) == 'array'){
            item.dataType = 'array';
        }else {
            item.dataType = 'string';
        }
    })
},
{
    deep: true,
	immediate: true,
})




</script>
<style lang="scss" scoped>
.process-style {
    background-color: transparent;
    margin-top: 15px;
    .line-status{
        display: flex;
        flex-direction: column;
        align-items: center;
        .node {
            height: 100px;
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;
            .line {
                width: 3px;
                height: 100px;
                background-color: var(--el-color-primary);
            }
            .item-style {
                display: flex;
                justify-content: center;
                align-items: center;
            }
            .item-left {
                margin-left: -120px;
            }
            .item-right {
                margin-left: 120px;
            }
            .cross {
                width: 120px;
                height: 2px;
                background-color: var(--el-color-primary);
            }
            .solid {
                background-color: var(--el-color-primary);
            }
            .dot {
                width: 10px;
                height: 10px;
                border-radius: 50%;
                z-index: 2;
                border: 2px solid var(--el-color-primary);
            }
            .current {
                width: 18px;
                height: 18px;
                border: 2px solid var(--el-color-primary);
                display: flex;
                justify-content: center;
                align-items: center;
                span {
                    display: inline-block;
                    width: 8px;
                    height: 8px;
                    background-color: var(--el-color-primary);
                    border-radius: 20px;
                }
            }
            .status {
                position: absolute;
                width: 110px;
                font-size: v-bind('fontSizeObj.baseFontSize');
                color: var(--el-color-primary);
            }
            .status-right {
                display: flex;
                left: 2%;
                justify-content: flex-end;
            }
            // .status-current-right {
            //     display: flex;
            //     left: 0%;
            //     justify-content: flex-end;
            // }
            .status-left {
                left: 20px;
            }
            .status-current-left {
                left: 22px;
            }
            .name {
                display: flex;
                position: absolute;
                width: 100px;
                font-size: v-bind('fontSizeObj.smallFontSize');
                bottom: 12px;
            }
            .name-left {
                justify-content: center;
                right: 20px;
            }
            .name-right {
                justify-content: center;
            }
            .time {
                position: absolute;
                width: 150px;
                font-size: 12px;
                color: #999;
                .time-group {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    position: absolute;
                    bottom: -50px;
                    left: 9%;
                }
                .time-group-right {
                    position: absolute;
                    left: 12%;
                }
                .time-single {
                    position: absolute;
                    bottom: -22px;
                }
                .time-single-right {
                    left: 14%;
                }
                .time-single-left {
                    right: 14%;
                }
            }

        }
        .top-dot {
            width: 10px;
            height: 10px;
            border-radius: 20px;
            background-color: var(--el-color-primary);
        }
        .last-line {
            width: 3px;
            height: 200px;
            background-image: linear-gradient(to bottom, var(--el-color-primary) 85%, #fff);
        }
    }
}


</style>
  