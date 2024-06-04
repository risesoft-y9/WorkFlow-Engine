<template>
  <div class="el-card is-always-shadow" style="height: 100%;float: left;background-color: #fff;margin-right: 10px;width: 130px;">
    <el-menu router>
      <template v-if="flowableStore.appType != 'yuejian' && flowableStore.appType != 'search' && flowableStore.appType != 'monitorCenter'">
        <el-menu-item index="add" >
          <i class="ri-file-add-line" style="margin-right: 5px;"></i>{{ $t('新建') }}
        </el-menu-item>
        <el-menu-item index="draft">
          <i class="ri-draft-line" style="margin-right: 5px;"></i>{{ $t('草稿箱') }}
        </el-menu-item>
        <el-menu-item index="todo">
          <i class="ri-todo-line" style="margin-right: 5px;"></i>{{ $t('待办件') }}
        </el-menu-item>
        <el-menu-item index="doing">
          <i class="ri-repeat-fill" style="margin-right: 5px;"></i>{{ $t('在办件') }}
        </el-menu-item>
        <el-menu-item index="done">
          <i class="ri-time-line" style="margin-right: 5px;"></i>{{ $t('办结件') }}
        </el-menu-item>
        <el-menu-item index="draftRecycle">
          <i class="ri-delete-bin-line" style="margin-right: 5px;"></i>{{ $t('回收站') }}
        </el-menu-item>
      </template>
      <template v-if="flowableStore.appType == 'yuejian'">
        <el-menu-item index="csTodo">
          <i class="ri-message-2-line" style="margin-right: 5px;"></i>{{ $t('未阅件') }}
        </el-menu-item>

        <el-menu-item index="csDone">
          <i class="ri-chat-check-line" style="margin-right: 5px;"></i>{{ $t('已阅件') }}
        </el-menu-item>

        <el-menu-item index="csPiyue">
          <i class="ri-chat-heart-line" style="margin-right: 5px;"></i>{{ $t('批阅件') }}
        </el-menu-item>
      </template>

      <template v-if="flowableStore.appType == 'search'">
        <el-menu-item index="searchList">
          <i class="ri-file-list-line" style="margin-right: 5px;"></i>{{ $t('个人所有件') }}
        </el-menu-item>

        <el-menu-item index="yuejianList">
          <i class="ri-chat-poll-line" style="margin-right: 5px;"></i>{{ $t('阅件') }}
        </el-menu-item>

        <el-menu-item index="emailList">
          <i class="ri-mail-line" style="margin-right: 5px;"></i>{{ $t('公务邮件') }}
        </el-menu-item>
      </template>
      
      <template v-if="flowableStore.appType == 'monitorCenter'">
        <el-menu-item index="monitorBanjian">
          <i class="ri-file-search-line" style="margin-right: 5px;"></i>{{ $t('监控办件') }}
        </el-menu-item>

        <el-menu-item index="monitorChaosong">
          <i class="ri-video-chat-line" style="margin-right: 5px;"></i>{{ $t('监控阅件') }}
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>
<script lang="ts" setup>
  import { ref, defineProps, onMounted, watch,reactive, inject} from 'vue';
  import { useRoute,useRouter } from 'vue-router';
  import { useFlowableStore } from "@/store/modules/flowableStore";
  import {getItem} from "@/api/flowableUI/index";
  import { useI18n } from 'vue-i18n';
  const { t } = useI18n();
  // 注入 字体对象
  const fontSizeObj: any = inject('sizeObjInfo');
  const flowableStore = useFlowableStore();
  const currentrRute = useRoute()

  watch(()=> flowableStore.itemId,(newVal,oldVal)=>{
  if(currentrRute.path.indexOf("/workIndex") > -1 && newVal != oldVal){
    getItem(flowableStore.itemId).then(res=>{
        flowableStore.$patch({
            itemInfo: res.data.itemModel,
            itemName: res.data.itemModel.name,
            deptManage: res.data.deptManage,
            monitorManage: res.data.monitorManage
        });
    }).catch(()=>{
        ElMessage({ type: "info", message: t("数据加载失败"), appendTo: '.is-always-shadow' });
    })
  }
 });
</script>

<style lang="scss" scoped>
.is-always-shadow {
  :global(.el-message .el-message__content) {
    font-size: v-bind('fontSizeObj.baseFontSize');
  }
}
</style>