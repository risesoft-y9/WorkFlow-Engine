<template>
  <div class="content">
      <el-card>
        <div style="margin-bottom: 8px;">
        <el-button-group>
          <el-button type="primary" @click="addEntrust"><i class="ri-add-line"></i>新增</el-button>
        </el-button-group>
      </div>
        <el-table
            ref="taoHongTemplateTable"
            :data="tableData"
            empty-text="暂无数据"
            @selection-change="handleSelectionChange"
             style="width: 100%;margin-top: 15px;" fit border>
          <el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
          <!-- <el-table-column prop="ownerName" label="委托人" align="center" width="200"></el-table-column> -->
          <el-table-column prop="assigneeName" label="受托人" align="center" width="auto"></el-table-column>
          <el-table-column prop="itemName" label="事项名称" align="center" width="auto"></el-table-column>
          <el-table-column prop="startTime" label="开始时间" align="center" width="130"></el-table-column>
          <el-table-column prop="endTime" label="结束时间" align="center" width="130"></el-table-column>
          <el-table-column prop="used" label="状态" align="center" width="90">
            <template #default="cell">
              <font v-if="cell.row.used == 0" color="green">未开始</font>
              <font v-if="cell.row.used == 1" color="red">使用中</font>
              <font v-if="cell.row.used == 2">已过期</font>
            </template>
          </el-table-column>
          <el-table-column prop="updateTime" label="添加/更新时间" align="center" width="200"></el-table-column>
          <el-table-column prop="opt" label="操作" align="center" width="200">
            <template #default="opt_cell">
              <el-button type="primary" size="small" @click="editEntrust(opt_cell.row)"><i class="ri-edit-line"></i>修改</el-button>
              <el-button type="danger" size="small" @click="delEntrust(opt_cell.row)"><i class="ri-delete-bin-line"></i>删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <NewOrModify ref="newOrModify" :reloadTable="getEntrustList"/>
  </div>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElNotification, ElMessage,ElLoading,FormInstance,UploadInstance } from 'element-plus';
import {entrustList,removeEntrust} from '@/api/itemAdmin/entrust';
import NewOrModify from '@/views/entrust/newOrEdit.vue';

const tableData = ref([]);
async function getEntrustList() {
    let res = await entrustList();
        tableData.value = res.data;
}

getEntrustList()

const newOrModify = ref();
const addEntrust = () => {
  newOrModify.value.show('');
}

const editEntrust = (rows) => {
  newOrModify.value.show(rows.id);
}

const typeManageRef = ref();
const typeManage = () => {
  typeManageRef.value.show();
}

const delEntrust = (rows) => {
    ElMessageBox.confirm("您确定要删除此出差委托吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
          removeEntrust(rows.id).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              getEntrustList();
            } else {
              ElMessage({message:res.msg,type: 'error',offset:65});
            }
          });
        })
        .catch(() => {
         ElMessage({
            type: "info",
            message: "已取消删除",
            offset:65
          });
        });
}

</script>

<style scoped lang="scss">

</style>
